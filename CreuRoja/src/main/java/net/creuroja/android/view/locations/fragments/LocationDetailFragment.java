package net.creuroja.android.view.locations.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.creuroja.android.R;
import net.creuroja.android.model.db.CreuRojaContract;
import net.creuroja.android.model.locations.LocationFactory;
import net.creuroja.android.model.locations.Location;
import net.creuroja.android.view.locations.OnDirectionsRequestedListener;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the
 * {@link OnLocationDetailsListener}
 * interface.
 */
public class LocationDetailFragment extends Fragment
		implements LoaderManager.LoaderCallbacks<Cursor> {
	private static final String ARG_LOCATION_ID = "locationId";
	private static final int LOADER_LOCATION = 1;

	private Location mLocation;

	private TextView mNameView;
	private TextView mDescriptionView;
	private TextView mPhoneView;
	private TextView mAddressView;
	private TextView mLatitudeView;
	private TextView mLongitudeView;
	private TextView mTypeView;
	private TextView mUpdatedAtView;

	private OnLocationDetailsListener mListener;
	private OnDirectionsRequestedListener mDirectionsListener;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public LocationDetailFragment() {
	}

	public static LocationDetailFragment newInstance(int locationId) {
		LocationDetailFragment fragment = new LocationDetailFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_LOCATION_ID, locationId);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//If getArguments is null, we come from an intent. If not, we put the fragment manually
		if (getArguments() == null) {
			getLoaderManager()
					.restartLoader(LOADER_LOCATION, getActivity().getIntent().getExtras(), this);
		} else {
			getLoaderManager().restartLoader(LOADER_LOCATION, getArguments(), this);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_location_detail, container, false);
		prepareViews(view);
		return view;
	}

	private void prepareViews(View view) {
		mNameView = (TextView) view.findViewById(R.id.fragment_location_detail_name);
		mDescriptionView = (TextView) view.findViewById(R.id.fragment_location_detail_description);
		mPhoneView = (TextView) view.findViewById(R.id.fragment_location_detail_phone);
		mAddressView = (TextView) view.findViewById(R.id.fragment_location_detail_address);
		mLatitudeView = (TextView) view.findViewById(R.id.fragment_location_detail_latitude);
		mLongitudeView = (TextView) view.findViewById(R.id.fragment_location_detail_longitude);
		mTypeView = (TextView) view.findViewById(R.id.fragment_location_detail_type);
		mUpdatedAtView = (TextView) view.findViewById(R.id.fragment_location_detail_updated_at);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnLocationDetailsListener) activity;
			mDirectionsListener = (OnDirectionsRequestedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(
					activity.toString() + " must implement OnLocationDetailsInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
		mDirectionsListener = null;
	}

	@Override public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		Uri uri = CreuRojaContract.Locations.CONTENT_URI;
		String selection = CreuRojaContract.Locations.REMOTE_ID + "=?";
		String[] selectionArgs = {Integer.toString(bundle.getInt(ARG_LOCATION_ID))};
		return new CursorLoader(getActivity(), uri, null, selection, selectionArgs, null);
	}

	@Override public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		if (cursor.moveToFirst()) {
			mLocation = LocationFactory.fromCursor(cursor);
			showLocationInfo();
		}
	}

	@Override public void onLoaderReset(Loader<Cursor> cursorLoader) {

	}

	private void showLocationInfo() {
		mNameView.setText(mLocation.name);
		mDescriptionView.setText(mLocation.description);
		mPhoneView.setText(mLocation.phone);
		mAddressView.setText(mLocation.address);
		mLatitudeView.setText(Double.toString(mLocation.latitude));
		mLongitudeView.setText(Double.toString(mLocation.longitude));
		mTypeView.setText(getString(mLocation.type.nameString));
		mUpdatedAtView.setText(mLocation.updatedAt);

		//TODO: Replace when button is displayed
		mLatitudeView.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View view) {
				mDirectionsListener.onDirectionsRequested(mLocation);
			}
		});
	}

	public void setLocation(Location location) {
		this.mLocation = location;
		showLocationInfo();
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p/>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnLocationDetailsListener {
	}

}
