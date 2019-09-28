package com.example.test_mapbox;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;

import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatDelegate;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.example.test_mapbox.MainActivity.m_savedInstanceState;

public class Embedded_NavigationView extends CoordinatorLayout implements OnNavigationReadyCallback {

	private final NavigationFragmentLocationCallback callback = new NavigationFragmentLocationCallback(this);
	private LocationEngine locationEngine;
	public static final float INITIAL_ZOOM = 16.5f;
	private Location lastLocation;

	@BindView(R.id.navigationView)
	protected NavigationView navigationView;

	@BindView(R.id.spacer)
	protected View spacer;

	@BindView(R.id.speed_limit)
	protected TextView speedWidget;

	@BindView(R.id.fabNavigation)
	protected FloatingActionButton fabNavigation;

	@BindView(R.id.fabPreviewRoute)
	protected FloatingActionButton fabPreviewRoute;

	private Embedded_NavigationView getInstance() {
		return this;
	}

	public Embedded_NavigationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Embedded_NavigationView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public Embedded_NavigationView(Context context) {
		super(context);
		init();
	}

	public void init() {

		try {
			// For styling the InstructionView
			getContext().setTheme(R.style.NavigationViewLight);

			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
		} catch (Exception e) {
		}


		try {
			inflate(getContext(), R.layout.mapbox_activity_embedded_navigation, this);
			ButterKnife.bind(this);

			CameraPosition initialPosition = new CameraPosition.Builder()
					.target(new LatLng(45, -74))
					.zoom(6)
					.build();

			navigationView.onCreate(m_savedInstanceState);
			navigationView.initialize(this, initialPosition);

			navigationView.setHapticFeedbackEnabled(false);

			navigationView.findViewById(R.id.feedbackFab).setVisibility(View.GONE);
			navigationView.findViewById(R.id.instructionView).setVisibility(View.INVISIBLE);
			navigationView.findViewById(R.id.recenterBtn).setVisibility(View.GONE);
			navigationView.findViewById(R.id.wayNameView).setVisibility(View.GONE);
			navigationView.findViewById(R.id.alertView).setVisibility(View.GONE);
			navigationView.findViewById(R.id.summaryBottomSheet).setVisibility(View.INVISIBLE);
			navigationView.findViewById(R.id.routeOverviewBtn).setVisibility(View.INVISIBLE);

			fabPreviewRoute.hide();
			fabNavigation.show();

		} catch (Exception e) {
		}
	}

	@Override
	public void onNavigationReady(boolean isRunning) {
		try {
			// For Location updates
			initializeLocationEngine();

			navigationView.retrieveNavigationMapboxMap().retrieveMap().getUiSettings().setCompassEnabled(true);
			navigationView.retrieveNavigationMapboxMap().retrieveMap().getUiSettings().setCompassFadeFacingNorth(false);

		} catch (Exception e) {
		}
	}

	void checkFirstUpdate(Location location) {
		try {
			if (lastLocation == null) {

				CameraPosition initialPosition = new CameraPosition.Builder()
						.target(new LatLng(location.getLatitude(), location.getLongitude()))
						.zoom(INITIAL_ZOOM)
						.tilt(50)
						.build();

				navigationView.retrieveNavigationMapboxMap().retrieveMap().setCameraPosition(initialPosition);
				navigationView.retrieveNavigationMapboxMap().retrieveMap().getLocationComponent().setLocationEngine(locationEngine);

				//Fake route for enable all map items
				DirectionsRoute directionsRoute = DirectionsRoute.fromJson("{\"routeIndex\":\"0\",\"distance\":0.0,\"duration\":0.0,\"geometry\":\"qyp_vAjdoukC??\",\"weight\":0.0,\"weight_name\":\"routability\",\"legs\":[{\"distance\":0.0,\"duration\":0.0,\"summary\":\"\",\"steps\":[{\"distance\":0.0,\"duration\":0.0,\"geometry\":\"qyp_vAjdoukC??\",\"name\":\"\",\"mode\":\"driving\",\"maneuver\":{\"location\":[0,0],\"bearing_before\":0.0,\"bearing_after\":0.0,\"instruction\":\"\",\"type\":\"depart\",\"modifier\":\"straight\"},\"voiceInstructions\":[],\"bannerInstructions\":[],\"driving_side\":\"right\",\"weight\":0.0,\"intersections\":[{\"location\":[0,0],\"bearings\":[0],\"entry\":[true],\"out\":0}]}],\"annotation\":{}}],\"routeOptions\":{\"baseUrl\":\"https://api.mapbox.com\",\"user\":\"mapbox\",\"profile\":\"driving-traffic\",\"coordinates\":[[0,0],[0,0]],\"alternatives\":false,\"language\":\"fr\",\"bearings\":\";\",\"continue_straight\":true,\"roundabout_exits\":true,\"geometries\":\"polyline6\",\"overview\":\"full\",\"steps\":true,\"annotations\":\"congestion,distance\",\"voice_instructions\":true,\"banner_instructions\":true,\"voice_units\":\"metric\",\"access_token\":\"%TOKEN%\",\"uuid\":\"cjwpqxutt08qr8wqfat2ou3fm\"},\"voiceLocale\":\"--\"}".replace("%TOKEN%", Mapbox.getAccessToken()));
				NavigationViewOptions.Builder options = NavigationViewOptions.builder().directionsRoute(directionsRoute);

				navigationView.retrieveNavigationMapboxMap().updateWaynameQueryMap(false);
				navigationView.startNavigation(options.build());

				navigationView.retrieveNavigationMapboxMap().removeRoute();
				//navigationView.stopNavigation();
				navigationView.retrieveMapboxNavigation().setCameraEngine(new SimpleCamera_Navigation());
				navigationView.retrieveNavigationMapboxMap().retrieveMap().getLocationComponent().setLocationEngine(locationEngine);
				navigationView.resetCameraPosition();
				navigationView.retrieveNavigationMapboxMap().clearMarkers();
				navigationView.retrieveNavigationMapboxMap().updateWaynameQueryMap(false);

				fabNavigation.hide();

				//Stop the fake navigation
				new Handler().postDelayed(() -> navigationView.stopNavigation(), 5000);
			}

			lastLocation = location;
		} catch (Exception e) {
		}
	}

	@SuppressLint("MissingPermission")
	private void initializeLocationEngine() {
		try {
			locationEngine = LocationEngineProvider.getBestLocationEngine(getContext());
			LocationEngineRequest request = buildEngineRequest();
			locationEngine.requestLocationUpdates(request, callback, null);
		} catch (Exception e) {
		}
	}

	@NonNull
	private LocationEngineRequest buildEngineRequest() {
		return new LocationEngineRequest.Builder(1000)
				.setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
				.setFastestInterval(500)
				.build();
	}

	public void onResume() {
		if (navigationView != null)
			navigationView.onResume();
	}

	public void onPause() {
		if (navigationView != null)
			navigationView.onPause();
	}

	public void onStop() {
		if (navigationView != null)
			navigationView.onStop();
	}

	public void onDestroy() {
		if (navigationView != null)
			navigationView.onDestroy();
	}

	public void onLowMemory() {
		if (navigationView != null)
			navigationView.onLowMemory();
	}

	public void SaveInstanceState(Bundle outState) {
		navigationView.onSaveInstanceState(outState);
		super.onSaveInstanceState();
	}

	public void RestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		navigationView.onRestoreInstanceState(savedInstanceState);
	}


	/*
	 * LocationEngine callback
	 */

	private static class NavigationFragmentLocationCallback implements LocationEngineCallback<LocationEngineResult> {

		private final WeakReference<Embedded_NavigationView> activityWeakReference;

		NavigationFragmentLocationCallback(Embedded_NavigationView activity) {
			this.activityWeakReference = new WeakReference<>(activity);
		}

		@Override
		public void onSuccess(LocationEngineResult result) {
			try {
				Embedded_NavigationView activity = activityWeakReference.get();
				if (activity != null) {

					Location location = result.getLastLocation();
					if (location == null) {
						return;
					}
					activity.checkFirstUpdate(location);
					//activity.updateLocation(location);
				}
			} catch (Exception e) {
			}
		}

		@Override
		public void onFailure(@NonNull Exception exception) {
			Timber.e(exception);
		}
	}
}
