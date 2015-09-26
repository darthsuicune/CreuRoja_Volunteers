package net.creuroja.android.dagger;

import android.content.SharedPreferences;

import com.google.android.gms.common.api.GoogleApiClient;

import net.creuroja.android.model.webservice.auth.AccountsHelper;
import net.creuroja.android.view.locations.activities.LocationsIndexActivity;

import dagger.Component;

@Component(modules = LocationsActivityModule.class)
public interface LocationsActivityComponent {
    void inject(LocationsIndexActivity activity);
}
