package net.creuroja.android.view.locations.fragments.gmaps;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * Created by lapuente on 09.04.15.
 */
public class IconizedClusterRenderer extends DefaultClusterRenderer<ClusterMarker> {
	public IconizedClusterRenderer(Context context, GoogleMap map,
								   ClusterManager<ClusterMarker> clusterManager) {
		super(context, map, clusterManager);
	}

	@Override protected void onBeforeClusterItemRendered(ClusterMarker item,
														 MarkerOptions markerOptions) {
		super.onBeforeClusterItemRendered(item, markerOptions);
		markerOptions.icon(BitmapDescriptorFactory.fromResource(item.icon()));
	}
}
