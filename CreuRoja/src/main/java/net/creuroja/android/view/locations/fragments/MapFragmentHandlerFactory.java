package net.creuroja.android.view.locations.fragments;

import net.creuroja.android.view.locations.fragments.gmaps.ClusteredGoogleMapFragment;
import net.creuroja.android.view.locations.fragments.gmaps.UnclusteredGoogleMapFragment;

/**
 * Created by lapuente on 03.09.14.
 */
public enum MapFragmentHandlerFactory {
    CLUSTERED {
        public MapFragmentHandler build() {
            MapFragmentHandler handler = new ClusteredGoogleMapFragment();
            handler.fragment().setRetainInstance(true);
            return handler;
        }
    }, UNCLUSTERED {
        public MapFragmentHandler build() {
            MapFragmentHandler handler = new UnclusteredGoogleMapFragment();
            handler.fragment().setRetainInstance(true);
            return handler;
        }
    };

    public abstract MapFragmentHandler build();
}
