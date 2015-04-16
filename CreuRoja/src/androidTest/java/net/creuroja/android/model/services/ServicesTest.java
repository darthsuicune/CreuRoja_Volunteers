package net.creuroja.android.model.services;

import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

import net.creuroja.android.model.db.CreuRojaContract;
import net.creuroja.android.model.db.CreuRojaProvider;

public class ServicesTest extends ProviderTestCase2<CreuRojaProvider> {
	Service s1, s2;
	MockContentResolver cr;
	int id, initialCount;

	public ServicesTest() {
		super(CreuRojaProvider.class, CreuRojaProvider.CONTENT_NAME);
	}

	@Override public void setUp() throws Exception {
		super.setUp();
		cr = getMockContentResolver();
	}

	@Override public void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCountForGeneralServices() throws Exception {
		initialCount = Services.count(cr);
		addTwoServices();
		expectServicesToBeAdded(2);
		removeTheAddedServices();
	}

	private void addTwoServices() {
		s1 = addAServiceWithValues(-1, "asd", false);
		s2 = addAServiceWithValues(-2, "asdf", false);
	}

	private Service addAServiceWithValues(int id, String string, boolean bool) {
		Service s = ServiceFactory
				.fromValues(id, string, string, string, string, string, string, string, bool);
		cr.insert(CreuRojaContract.Services.CONTENT_URI, s.asValues());
		return s;
	}

	private void expectServicesToBeAdded(int newCount) {
		int count = Services.count(cr);
		assertTrue(count == (initialCount + newCount));
	}

	private void removeTheAddedServices() {
		s1.delete(cr);
		s2.delete(cr);
	}

	public void testCountForSpecificServices() throws Exception {
		id = 1;
		initialCount = Services.count(cr, id);
		addTwoServicesWithId(id);
		expectServicesToBeAdded(2);
		removeTheAddedServices();
	}

	private void addTwoServicesWithId(int id) {
		s1 = addAServiceWithValues(id, "asd", false);
		s2 = addAServiceWithValues(id, "asdf", false);
	}


}