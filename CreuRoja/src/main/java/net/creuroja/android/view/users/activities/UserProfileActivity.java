package net.creuroja.android.view.users.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import net.creuroja.android.R;
import net.creuroja.android.view.general.activities.SettingsActivity;
import net.creuroja.android.model.users.User;
import net.creuroja.android.view.users.fragments.UserProfileFragment;

public class UserProfileActivity extends ActionBarActivity
		implements UserProfileFragment.OnUserProfileChangedListener {
	public static final String TAG_PROFILE = "CRUserProfile";
	UserProfileFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_profile);
		if (savedInstanceState == null) {
			setFragment();
		}
	}

	private void setFragment() {
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		if (fragment == null) {
			fragment = (UserProfileFragment) manager.findFragmentByTag(TAG_PROFILE);
			if (fragment == null) {
				fragment = UserProfileFragment.newInstance();
			}
		}
		transaction.replace(R.id.user_profile_container, fragment, TAG_PROFILE).commit();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_settings:
				openSettings();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override public void onUserProfileChanged(User user) {
		//Nothing to do for now
	}

	private void openSettings() {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}
}
