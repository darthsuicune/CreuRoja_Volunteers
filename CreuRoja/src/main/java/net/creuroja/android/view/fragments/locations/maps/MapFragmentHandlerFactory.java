package net.creuroja.android.view.fragments.locations.maps;

/**
 * Created by lapuente on 03.09.14.
 */
public class MapFragmentHandlerFactory {
	public static MapFragmentHandler getHandler() {
		GoogleMapFragment fragment = new GoogleMapFragment();
		fragment.setRetainInstance(true);
		return fragment;
	}
}
