package net.creuroja.android.view.fragments.locations;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.creuroja.android.R;
import net.creuroja.android.model.locations.Location;
import net.creuroja.android.model.locations.LocationList;
import net.creuroja.android.model.locations.LocationType;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link LocationsListListener} callbacks
 * interface.
 */
public class LocationListFragment extends ListFragment implements
		LocationsHandlerFragment.OnLocationsListUpdated {
	private LocationList mLocationList;
	private LocationsListListener mListener;

	// The Adapter which will be used to populate the ListView/GridView with Views.
	private ListAdapter mAdapter;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public LocationListFragment() {
	}

	public static LocationListFragment newInstance() {
		return new LocationListFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_location_list, container, false);
		setListAdapter(mAdapter);
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (LocationsListListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(
					activity.toString() + " must implement LocationsListListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		mListener.onLocationListItemSelected(mLocationList.get(position));
	}

	public void toggleLocations(LocationType type, boolean newState) {
		mLocationList.toggleLocationType(type, newState);
		mAdapter = new LocationListAdapter(getActivity());
		setListAdapter(mAdapter);
	}

	@Override public void onLocationsListUpdated(LocationList list) {
		mLocationList = list;
		if(this.isAdded()) {
			mAdapter = new LocationListAdapter(getActivity());
			setListAdapter(mAdapter);
		}
	}

	static class ViewHolder {
		ImageView icon;
		TextView name;
		TextView description;
		TextView phone;
		TextView address;
	}

	private class LocationListAdapter extends ArrayAdapter<Location> {
		List<Location> locationList;

		public LocationListAdapter(Context context) {
			super(context, R.layout.location_list_entry, mLocationList.getLocations());
			locationList = mLocationList.getLocations();
		}

		@Override public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				LayoutInflater inflater = getActivity().getLayoutInflater();
				convertView = inflater.inflate(R.layout.location_list_entry, parent, false);
				holder = populateHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			populateView(holder, locationList.get(position));
			return convertView;
		}

		private ViewHolder populateHolder(View convertView) {
			ViewHolder holder = new ViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.location_list_entry_icon);
			holder.name = (TextView) convertView.findViewById(R.id.location_list_entry_name);
			holder.description =
					(TextView) convertView.findViewById(R.id.location_list_entry_description);
			holder.address = (TextView) convertView.findViewById(R.id.location_list_entry_address);
			holder.phone = (TextView) convertView.findViewById(R.id.location_list_entry_phone);
			return holder;
		}

		private void populateView(ViewHolder holder, Location location) {
			if (location != null) {
				holder.icon.setImageResource(location.mType.mIcon);
				holder.name.setText(location.mName);
				holder.description.setText(location.mDescription);
				holder.address.setText(location.mAddress);
				holder.phone.setText(location.mPhone);
			}
		}
	}
	public interface LocationsListListener {
		public void onLocationListItemSelected(Location location);
	}
}
