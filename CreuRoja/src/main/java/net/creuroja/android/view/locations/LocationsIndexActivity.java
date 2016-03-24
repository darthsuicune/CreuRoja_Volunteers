package net.creuroja.android.view.locations;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import net.creuroja.android.dagger.LocationsIndexActivityComponent;

public class LocationsIndexActivity extends AppCompatActivity {

	LocationsIndexActivityComponent component;

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		component.inject(this);
	}
}
