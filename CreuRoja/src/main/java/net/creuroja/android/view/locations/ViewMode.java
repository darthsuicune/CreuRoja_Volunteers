package net.creuroja.android.view.locations;

import android.view.View;

public enum ViewMode {
	MAP(0, View.GONE), LIST(1, View.VISIBLE);

	final int value;
	final int detailsBlockVisibility;

	ViewMode(final int value, final int detailsBlockVisibility) {
		this.value = value;
		this.detailsBlockVisibility = detailsBlockVisibility;
	}

	public static ViewMode getViewMode(int mode) {
		switch (mode) {
			case 1:
				return LIST;
			case 0:
			default:
				return MAP;
		}
	}

	public int getValue() {
		return value;
	}

	public int getDetailsBlockVisibility() {
		return detailsBlockVisibility;
	}
}
