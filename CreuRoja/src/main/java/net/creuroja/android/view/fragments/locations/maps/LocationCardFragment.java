package net.creuroja.android.view.fragments.locations.maps;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.creuroja.android.R;
import net.creuroja.android.model.locations.Location;
import net.creuroja.android.view.fragments.locations.OnDirectionsRequestedListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LocationCardFragment.OnLocationCardInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LocationCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationCardFragment extends Fragment {
	// the fragment initialization parameters
	private Location mLocation;

	//Callback for the Activity
	private OnLocationCardInteractionListener mListener;
	private OnDirectionsRequestedListener mDirectionsListener;
	//General location card view
	private View cardView;

	private TextView mNameView;
	private TextView mAddressView;
	private TextView mPhoneView;
	private TextView mDescriptionView;
	private TextView mRouteView;
	private TextView mCloseView;
	private TextView mDetailsView;

	private boolean hasDirections = false;

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment LocationCardFragment.
	 */
	public static LocationCardFragment newInstance(Location location) {
		LocationCardFragment fragment = new LocationCardFragment();
		fragment.setLocation(location);
		return fragment;
	}

	// Required empty public constructor
	public LocationCardFragment() {}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		cardView = inflater.inflate(R.layout.fragment_location_card, container, false);
		updateView();
		return cardView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnLocationCardInteractionListener) activity;
			mDirectionsListener = (OnDirectionsRequestedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(
					activity.toString() + " must implement OnLocationCardInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public void setLocation(Location location) {
		mLocation = location;
		updateView();
		if(hasDirections) {
			removeRoute();
		}
	}

	private void updateView() {
		if (cardView != null && mLocation != null) {
			mAddressView = (TextView) cardView.findViewById(R.id.location_card_address);
			mDescriptionView = (TextView) cardView.findViewById(R.id.location_card_description);
			mPhoneView = (TextView) cardView.findViewById(R.id.location_card_phone);
			mNameView = (TextView) cardView.findViewById(R.id.location_card_name);
			mRouteView = (TextView) cardView.findViewById(R.id.location_card_get_directions);
			mCloseView = (TextView) cardView.findViewById(R.id.location_card_close);
			mDetailsView = (TextView) cardView.findViewById(R.id.location_card_details);
		}
		if(mAddressView != null && mLocation != null) {
			mAddressView.setText((mLocation.mAddress == null) ? "" : mLocation.mAddress);
			mDescriptionView.setText((mLocation.mDescription == null) ? "" : mLocation.mDescription);
			mPhoneView.setText((mLocation.mPhone == null) ? "" : mLocation.mPhone);
			mNameView.setText((mLocation.mName == null) ? "" : mLocation.mName);
			mRouteView.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View view) {
					if (hasDirections) {
						removeRoute();
					} else {
						drawDirections();
					}
				}
			});
			mCloseView.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View view) {
					onCloseRequested();
				}
			});
			mDetailsView.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View view) {
					onDetailsRequested();
				}
			});
		}
	}

	private void drawDirections() {
		if (mDirectionsListener.onDirectionsRequested(mLocation)) {
			mRouteView.setText(R.string.location_card_remove_directions);
			hasDirections = true;
		}
	}

	private void removeRoute() {
		mRouteView.setText(R.string.location_card_get_directions);
		hasDirections = false;
		mDirectionsListener.onRemoveRouteRequested();
	}

	private void onCloseRequested() {
		mListener.onCardCloseRequested();
	}

	private void onDetailsRequested() {
		mListener.onCardDetailsRequested(mLocation);
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
	public interface OnLocationCardInteractionListener {
		public void onCardCloseRequested();

		public void onCardDetailsRequested(Location location);
	}

}
