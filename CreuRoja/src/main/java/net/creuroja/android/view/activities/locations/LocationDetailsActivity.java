package net.creuroja.android.view.activities.locations;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import net.creuroja.android.R;
import net.creuroja.android.model.locations.Location;
import net.creuroja.android.view.fragments.locations.LocationDetailFragment;
import net.creuroja.android.view.fragments.locations.OnDirectionsRequestedListener;

public class LocationDetailsActivity extends ActionBarActivity
		implements LocationDetailFragment.OnLocationDetailsInteractionListener,
		OnDirectionsRequestedListener {
	public final static String EXTRA_LOCATION_ID = "locationId";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_details);

		Toolbar toolbar = (Toolbar) findViewById(R.id.location_detail_toolbar);
		setSupportActionBar(toolbar);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
			case R.id.action_settings:
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	//TODO: Implement
	@Override public boolean onDirectionsRequested(Location location) {
		return false;
	}

	//TODO: Implement
	@Override public void onRemoveRouteRequested() {

	}

	//TODO: Implement
	@Override public boolean hasDirections() {
		return false;
	}
}
