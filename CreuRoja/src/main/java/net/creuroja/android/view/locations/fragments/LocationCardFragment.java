package net.creuroja.android.view.locations.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dlgdev.directions.Directions;

import net.creuroja.android.R;
import net.creuroja.android.model.db.CreuRojaContract;
import net.creuroja.android.model.locations.Location;
import net.creuroja.android.model.locationservices.LocationService;
import net.creuroja.android.model.services.Service;
import net.creuroja.android.model.services.ServiceFactory;
import net.creuroja.android.view.locations.OnDirectionsRequestedListener;

import java.util.List;

import static android.support.v4.app.LoaderManager.LoaderCallbacks;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LocationCardFragment.OnLocationCardInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LocationCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationCardFragment extends Fragment {
	private static final int LOADER_SERVICES = 1;
	private static final int LOADER_LOCATION_SERVICES = 2;

	// the fragment initialization parameters
	private Location location;

	//Callback for the Activity
	private OnLocationCardInteractionListener listener;
	private OnDirectionsRequestedListener directionsListener;
	//General location card view
	private View cardView;

	private TextView nameView;
	private TextView addressView;
	private TextView phoneView;
	private TextView descriptionView;
	private TextView routeView;
	private TextView closeView;
	private TextView detailsView;

	private boolean hasDirections = false;
	private TextView servicesView;

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
	public LocationCardFragment() {
	}

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
			listener = (OnLocationCardInteractionListener) activity;
			directionsListener = (OnDirectionsRequestedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(
					activity.toString() + " must implement OnLocationCardInteractionListener");
		}
		if (location != null) {
			loadServices();
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		listener = null;
	}

	public void setLocation(Location location) {
		this.location = location;
		updateView();
		if (hasDirections) {
			removeRoute();
		}
		if (isAdded()) {
			loadServices();
		}
	}

	private void loadServices() {
		getLoaderManager().restartLoader(LOADER_LOCATION_SERVICES, null, new ServicesLoaderHelper());
	}

	private void updateView() {
		if (cardView != null && location != null && addressView == null) {
			addressView = (TextView) cardView.findViewById(R.id.location_card_address);
			descriptionView = (TextView) cardView.findViewById(R.id.location_card_description);
			phoneView = (TextView) cardView.findViewById(R.id.location_card_phone);
			nameView = (TextView) cardView.findViewById(R.id.location_card_name);
			routeView = (TextView) cardView.findViewById(R.id.location_card_get_directions);
			closeView = (TextView) cardView.findViewById(R.id.location_card_close);
			detailsView = (TextView) cardView.findViewById(R.id.location_card_details);
			servicesView = (TextView) cardView.findViewById(R.id.location_card_services);
		}
		if (addressView != null && location != null) {
			addressView.setText((location.address == null) ? "" : location.address);
			descriptionView.setText((location.description == null) ? "" : location.description);
			phoneView.setText((location.phone == null) ? "" : location.phone);
			nameView.setText((location.name == null) ? "" : location.name);
			routeView.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View view) {
					if (hasDirections) {
						removeRoute();
					} else {
						drawDirections();
					}
				}
			});
			closeView.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View view) {
					onCloseRequested();
				}
			});
			detailsView.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View view) {
					onDetailsRequested();
				}
			});
			if(location.serviceList.size() > 0) {
				servicesView.setText("" + location.serviceList.size() + " services available");
			}
		}
	}

	private void drawDirections() {
		directionsListener.onDirectionsRequested(location);
	}

	private void removeRoute() {
		routeView.setText(R.string.location_card_get_directions);
		hasDirections = false;
		directionsListener.onRemoveRouteRequested();
	}

	private void onCloseRequested() {
		listener.onCardCloseRequested();
	}

	private void onDetailsRequested() {
		listener.onCardDetailsRequested(location);
	}

	public void onDirectionsDrawn(Directions directions) {
		if (directionsListener.hasDirections()) {
			routeView.setText(R.string.location_card_remove_directions);
			hasDirections = true;
		}
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

	private class ServicesLoaderHelper implements LoaderCallbacks<Cursor> {
		private static final String ARG_SERVICE_KEYS = "serviceKeys";

		@Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			Uri uri;
			String where;
			String[] whereArgs;
			switch (id) {
				case LOADER_LOCATION_SERVICES:
					uri = CreuRojaContract.LocationServices.CONTENT_URI;
					where = CreuRojaContract.LocationServices.LOCATION_ID + "=?";
					whereArgs = new String[]{Integer.toString(location.remoteId)};
					break;
				default:
					String[] keys = args.getStringArray(ARG_SERVICE_KEYS);
					uri = CreuRojaContract.Services.CONTENT_URI;
					where = CreuRojaContract.Services.REMOTE_ID + " IN (" +
							getKeyQuestionMarks(keys) + ")";
					whereArgs = keys;
					break;
			}

			return new CursorLoader(getActivity(), uri, null, where, whereArgs, null);
		}

		private String getKeyQuestionMarks(String[] keys) {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < keys.length; i++) {
				builder.append("?");
				if (i < keys.length - 1) {
					builder.append(",");
				}
			}
			return builder.toString();
		}

		@Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			switch (loader.getId()) {
				case LOADER_LOCATION_SERVICES:
					Bundle args = new Bundle();
					String[] serviceIds = LocationService.serviceIds(data);
					args.putStringArray(ARG_SERVICE_KEYS, serviceIds);
					getLoaderManager().restartLoader(LOADER_SERVICES, args, this);
					break;
				case LOADER_SERVICES:
					List<Service> serviceList = ServiceFactory.listFromCursor(data);
					for (Service service : serviceList) {
						location.addService(service);
					}
					updateView();
					break;
			}

		}

		@Override public void onLoaderReset(Loader<Cursor> loader) {

		}
	}
}
