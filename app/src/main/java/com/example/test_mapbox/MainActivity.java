package com.example.test_mapbox;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.BLUETOOTH;
import static android.Manifest.permission.BLUETOOTH_ADMIN;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WAKE_LOCK;

public class MainActivity extends FragmentActivity {

	public Embedded_NavigationView objEmbedded_navigationView;

	public static MainActivity objMainActivity;
	public FrameLayout objFrameMap;
	public static Bundle m_savedInstanceState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		m_savedInstanceState = savedInstanceState;

		String[] PERMISSIONS = {
				INTERNET,
				ACCESS_NETWORK_STATE,
				ACCESS_LOCATION_EXTRA_COMMANDS,
				BLUETOOTH,
				BLUETOOTH_ADMIN,
				READ_PHONE_STATE,
				WAKE_LOCK,
				ACCESS_WIFI_STATE,
				ACCESS_FINE_LOCATION,
				ACCESS_COARSE_LOCATION
		};

		ActivityCompat.requestPermissions(this, PERMISSIONS, 0);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		objMainActivity = this;

		Mapbox.getInstance(objMainActivity, "");
		objEmbedded_navigationView = new Embedded_NavigationView(objMainActivity);

		objFrameMap = new FrameLayout(this);
		objFrameMap.addView(objEmbedded_navigationView);

		((ViewGroup) this.findViewById(R.id.layer_mapview_1)).addView(objFrameMap);

		this.findViewById(R.id.layer_mapview_1).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				((LinearLayout) objFrameMap.getParent()).removeView(objFrameMap);
				((ViewGroup) objMainActivity.findViewById(R.id.layer_mapview_1)).addView(objFrameMap);
			}
		});

		this.findViewById(R.id.layer_mapview_2).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				((LinearLayout) objFrameMap.getParent()).removeView(objFrameMap);
				((ViewGroup) objMainActivity.findViewById(R.id.layer_mapview_2)).addView(objFrameMap);
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();

		if (objEmbedded_navigationView != null)
			objEmbedded_navigationView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();

		if (objEmbedded_navigationView != null)
			objEmbedded_navigationView.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();

		if (objEmbedded_navigationView != null)
			objEmbedded_navigationView.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (objEmbedded_navigationView != null)
			objEmbedded_navigationView.onDestroy();
	}

	@Override
	public void onLowMemory() {
		if (objEmbedded_navigationView != null)
			objEmbedded_navigationView.onLowMemory();
	}

}