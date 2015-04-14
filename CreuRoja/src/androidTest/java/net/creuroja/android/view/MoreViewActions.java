package net.creuroja.android.view;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import org.hamcrest.Matcher;

import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;

/**
 * https://groups.google.com/forum/#!topic/android-test-kit-discuss/bmLQUlcI5U4
 */
public class MoreViewActions {
	public static ViewAction openDrawer() {
		return new ViewAction() {
			@Override
			public Matcher<View> getConstraints() {
				return isAssignableFrom(DrawerLayout.class);
			}

			@Override
			public String getDescription() {
				return "open drawer";
			}

			@Override
			public void perform(UiController uiController, View view) {
				((DrawerLayout) view).openDrawer(GravityCompat.START);
			}
		};
	}
	public static ViewAction closeDrawer() {
		return new ViewAction() {
			@Override
			public Matcher<View> getConstraints() {
				return isAssignableFrom(DrawerLayout.class);
			}

			@Override
			public String getDescription() {
				return "close drawer";
			}

			@Override
			public void perform(UiController uiController, View view) {
				((DrawerLayout) view).closeDrawer(GravityCompat.START);
			}
		};
	}
}
