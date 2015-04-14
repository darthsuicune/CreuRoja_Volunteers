package net.creuroja.android.view.locations.fragments;

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
import net.creuroja.android.model.locations.LocationType;
import net.creuroja.android.model.locations.Locations;

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
    private Locations locations;
    private LocationsListListener listener;

    // The Adapter which will be used to populate the ListView/GridView with Views.
    private ListAdapter adapter;

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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (LocationsListListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    activity.toString() + " must implement LocationsListListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_list, container, false);
        setAdapter();
        return view;
    }

    @Override public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        listener.onLocationListItemSelected((Location) l.getAdapter().getItem(position));
    }

    public void activateLocations(LocationType type) {
        locations.toggleLocationType(type, true);
        setAdapter();
    }

    private void setAdapter() {
        if (locations != null) {
            int listSize = locations.locations().size();
            Location[] list = locations.locations().toArray(new Location[listSize]);
            adapter = new LocationListAdapter(getActivity(), list);
        }
        setListAdapter(adapter);
    }

    public void deactivateLocations(LocationType type) {
        locations.toggleLocationType(type, false);
        setAdapter();
    }

    @Override public void onLocationsListUpdated(Locations list) {
        locations = list;
        if(isAdded()) {
            setAdapter();
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
        Location[] locations;

        public LocationListAdapter(Context context, Location[] locations) {
            super(context, R.layout.location_list_entry, locations);
            this.locations = locations;
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
            populateView(holder, locations[position]);
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
                holder.icon.setImageResource(location.type.icon);
                holder.name.setText(location.name);
                holder.description.setText(location.description);
                holder.address.setText(location.address);
                holder.phone.setText(location.phone);
            }
        }
    }

    public interface LocationsListListener {
        void onLocationListItemSelected(Location location);
    }
}
