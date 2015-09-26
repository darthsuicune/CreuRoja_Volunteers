package net.creuroja.android.view.locations.activities;

import android.accounts.Account;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ListView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;

import net.creuroja.android.R;
import net.creuroja.android.view.locations.fragments.gmaps.ClusteredGoogleMapFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static net.creuroja.android.view.MoreViewActions.openDrawer;
import static net.creuroja.android.view.locations.ViewMode.MAP;
import static net.creuroja.android.view.locations.fragments.MapFragmentHandler.DEFAULT_LATITUDE;
import static net.creuroja.android.view.locations.fragments.MapFragmentHandler.DEFAULT_LONGITUDE;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(AndroidJUnit4.class)
public class LocationsIndexActivityTest {
    LocationsIndexActivity activity;
    Account account;

    @Rule public ActivityTestRule<LocationsIndexActivity> rule =
            new ActivityTestRule<>(LocationsIndexActivity.class);

    @Before public void setUp() throws Exception {
        createAccount();
        activity = rule.getActivity();
    }

    private void createAccount() {

    }

    @After public void tearDown() throws Exception {
        destroyAccount();
    }

    private void destroyAccount() {

    }

    @Test public void testItShowsTheLocationItems() throws Exception {
        whenWeOpenTheList();
        itShouldLoadTheListFragmentCorrectly();
    }

    private void whenWeOpenTheList() throws Exception {
        afterWeOpenTheNavigationDrawer();
        whenWeClickOnThe(R.id.locations_drawer_see_as_list);
    }

    private void afterWeOpenTheNavigationDrawer() throws Exception {
        onView(withId(R.id.drawer_layout)).perform(openDrawer());
        Thread.sleep(500);
    }

    private void whenWeClickOnThe(int resId) {
        onView(withId(resId)).perform(click());
    }

    private void itShouldLoadTheListFragmentCorrectly() {
        ListView view = activity.listFragment.getListView();
        assertTrue((view.getAdapter() != null) && view.getAdapter().getCount() > 0);
    }

    @Test public void testChangingViewModesUpdatesTheMainFragment() throws Exception {
        afterWeOpenTheNavigationDrawer();
        if (activity.currentViewMode == MAP) {
            whenWeClickOnThe(R.id.locations_drawer_see_as_list);
            itShouldLoadTheListFragmentCorrectly();
        } else {
            whenWeClickOnThe(R.id.locations_drawer_see_as_map);
            itShouldLoadTheMapFragmentCorrectly();
        }
    }

    private void itShouldLoadTheMapFragmentCorrectly() {
        assertTrue(activity.mapFragmentHandler.fragment().isResumed() &&
                activity.mapFragmentHandler.fragment().isAdded());
    }

    // Bug: map will reset and not show points after going map - list - map
    @Test public void testMapDoesntResetAfterMapListMap() throws Throwable {
        whenWeOpenTheMap();
        whenWeOpenTheList();
        whenWeOpenTheMap(); //Again
        itShouldntResetTheMap();
    }

    private void whenWeOpenTheMap() throws Exception {
        afterWeOpenTheNavigationDrawer();
        whenWeClickOnThe(R.id.locations_drawer_see_as_map);
    }

    private void itShouldntResetTheMap() throws Throwable {
        ClusteredGoogleMapFragment fragment =
                (ClusteredGoogleMapFragment) activity.mapFragmentHandler.fragment();
        final GoogleMap map = fragment.getMap();
        rule.runOnUiThread(new Runnable() {
            @Override public void run() {
                CameraPosition position = map.getCameraPosition();
                assertTrue((int) position.zoom == 12);
                assertTrue(areNearEnough(position.target.latitude, DEFAULT_LATITUDE));
                assertTrue(areNearEnough(position.target.latitude, DEFAULT_LONGITUDE));
            }
        });
    }

    private boolean areNearEnough(double current, double expected) {
        return current - 0.01 < expected && current + 0.01 > expected;
    }
}