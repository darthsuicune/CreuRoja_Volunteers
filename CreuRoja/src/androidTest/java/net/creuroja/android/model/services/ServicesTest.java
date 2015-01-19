package net.creuroja.android.model.services;

import android.content.ContentResolver;
import android.test.AndroidTestCase;

import net.creuroja.android.model.db.CreuRojaContract;

public class ServicesTest extends AndroidTestCase {
	Service s1, s2;
	ContentResolver cr;
	int id, initialCount;

	public void setUp() throws Exception {
		super.setUp();
		cr = getContext().getContentResolver();
	}

	public void testCount() throws Exception {
		initialCount = Services.count(cr, id);
		addACoupleServices();
		expectACoupleServicesToBePresent();
		removeTheAddedServices();
	}

	private void addACoupleServices() {
		s1 = ServiceFactory.fromValues(-1, "asd", "asd", "asd", "asd", "asd", "asd", "asd", false);
		s2 = ServiceFactory
				.fromValues(-2, "asdf", "asdf", "asdf", "asdf", "asdf", "asdf", "asdf", false);
		cr.insert(CreuRojaContract.Services.CONTENT_URI, s1.asValues());
		cr.insert(CreuRojaContract.Services.CONTENT_URI, s2.asValues());
	}

	private void expectACoupleServicesToBePresent() {
		int count = Services.count(cr, id);
		assertTrue(count == (initialCount + 2));
	}

	@Override public void tearDown() throws Exception {
		super.tearDown();
	}

	private void removeTheAddedServices() {
		s1.delete(cr);
		s2.delete(cr);
	}
}