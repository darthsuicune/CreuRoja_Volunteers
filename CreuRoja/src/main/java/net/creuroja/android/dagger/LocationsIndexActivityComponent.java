package net.creuroja.android.dagger;

import net.creuroja.android.view.locations.LocationsIndexActivity;

import dagger.Component;

@Component(modules = LocationsIndexActivityModule.class)
public interface LocationsIndexActivityComponent {
	void inject(LocationsIndexActivity activity);
}
