package net.creuroja.android.view.general.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;

import net.creuroja.android.R;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On handset devices,
 * settings are presented as a single list. On tablets, settings are split by category, with
 * category headers shown to the left of the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html"> Android Design:
 * Settings</a> for design guidelines and the
 * <a href="http://developer.android.com/guide/topics/ui/settings.html"> Settings API Guide</a>
 * for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {
	/**
	 * A preference value change listener that updates the preference's summary to reflect its new
	 * value.
	 */
	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener =
			new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object value) {
					String stringValue = value.toString();

					if (preference instanceof ListPreference) {
						// For list preferences, look up the correct display value in the
						// preference's 'entries' list and set its summary
						ListPreference listPreference = (ListPreference) preference;
						int index = listPreference.findIndexOfValue(stringValue);

						preference.setSummary(
								(index >= 0) ? listPreference.getEntries()[index] : null);

					} else if (preference instanceof RingtonePreference) {
						// For ringtone preferences, look up the correct display value using
						// RingtoneManager.
						if (TextUtils.isEmpty(stringValue)) {
							// Empty values correspond to 'silent' (no ringtone).
							preference.setSummary(null);
						} else {
							Ringtone ringtone = RingtoneManager
									.getRingtone(preference.getContext(), Uri.parse(stringValue));

							if (ringtone == null) {
								// Clear the summary if there was a lookup error.
								preference.setSummary(null);
							} else {
								// Set the summary to reflect the new ringtone display name.
								preference.setSummary(ringtone.getTitle(preference.getContext()));
							}
						}
					} else {
						// For all other preferences, set the summary to the value's simple string
						// representation.
						preference.setSummary(stringValue);
					}
					return true;
				}
			};

	/**
	 * Helper method to determine if the device has an extra-large screen. For example, 10"
	 * tablets are extra-large.
	 */
	private static boolean isXLargeTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout &
				Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	/**
	 * Determines whether the simplified settings UI should be shown. This is true if the device
	 * doesn't have newer APIs like {@link PreferenceFragment}, or the device doesn't have an
	 * extra-large screen. In these cases, a single-pane "simplified" settings UI should be shown.
	 */
	private static boolean isSimplePreferences(Context context) {
		return Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB || !isXLargeTablet(context);
	}

	/**
	 * Binds a preference's summary to its value. More specifically, when the preference's value is
	 * changed, its summary (line of text below the preference title) is updated to reflect the
	 * value. The summary is also immediately updated upon calling this method. The exact display
	 * format is dependent on the type of preference.
	 *
	 * @see #sBindPreferenceSummaryToValueListener
	 */
	private static void bindPreferenceSummaryToValue(Preference preference) {
		preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

		// Trigger the listener immediately with the preference's current value.
		sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
				PreferenceManager.getDefaultSharedPreferences(preference.getContext())
						.getString(preference.getKey(), ""));
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		if (isSimplePreferences(this)) {
			setupSimplePreferencesScreen();
		}

	}

	/**
	 * Shows the simplified settings UI if the device configuration if the
	 * device configuration dictates that a simplified, single-pane UI should be
	 * shown.
	 */
	private void setupSimplePreferencesScreen() {
		// In the simplified UI, fragments are not used at all and we instead use the older
		// PreferenceActivity APIs.

		// Add 'data and sync' preferences, and a corresponding header.
		PreferenceCategory fakeHeader = new PreferenceCategory(this);
		fakeHeader.setTitle(R.string.pref_header_data_sync);
		addPreferencesFromResource(R.xml.pref_data_sync);

		// Bind the summaries of EditText/List/Dialog/Ringtone preferences to their values. When
		// their values change, their summaries are updated to reflect the new value, per the
		// Android Design guidelines.
		bindPreferenceSummaryToValue(findPreference("sync_frequency"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onIsMultiPane() {
		return isXLargeTablet(this) && !isSimplePreferences(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onBuildHeaders(List<Header> target) {
		if (!isSimplePreferences(this)) {
			loadHeadersFromResource(R.xml.pref_headers, target);
		}
	}

	/**
	 * This fragment shows data and sync preferences only. It is used when the activity is showing
	 * a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class DataSyncPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_data_sync);

			// Bind the summaries of EditText/List/Dialog/Ringtone preferences to their values.
			// When their values change, their summaries are updated to reflect the new value, per
			// the Android Design guidelines.
			bindPreferenceSummaryToValue(findPreference("sync_frequency"));
		}
	}
}
