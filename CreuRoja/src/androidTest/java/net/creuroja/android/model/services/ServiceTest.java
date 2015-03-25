package net.creuroja.android.model.services;

import junit.framework.TestCase;

public class ServiceTest extends TestCase {
	Service s1, s2, s3;
	@Override public void setUp() throws Exception {
		super.setUp();
		s1 = ServiceFactory.fromValues(-1, "asd", "asd", "asd", "asd", "asd", "asd", "asd", false);
		s2 = ServiceFactory
				.fromValues(-2, "asdf", "asdf", "asdf", "asdf", "asdf", "asdf", "asdf", false);
		s3 = ServiceFactory.fromValues(-1, "asd", "asd", "asd", "asd", "asd", "asd", "asd", false);
	}

	public void testEqualsMatchesEqualIds() throws Exception {
		expectEqualIdsToBeEqual();
	}

	private void expectEqualIdsToBeEqual() {
		assertTrue(s1.equals(s3));
	}

	public void testEqualsMatchesDifferentIds() throws Exception {
		expectDifferentIdsToNotBeEqual();
	}

	private void expectDifferentIdsToNotBeEqual() {
		assertFalse(s1.equals(s2));
	}

	@Override public void tearDown() throws Exception {
		super.tearDown();

	}
}