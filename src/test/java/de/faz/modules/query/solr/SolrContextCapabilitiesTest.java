package de.faz.modules.query.solr;

import de.faz.modules.query.GroupingSearchOption;
import de.faz.modules.query.capabilities.FeatureSupport;
import de.faz.modules.query.capabilities.GroupingSupport;
import de.faz.modules.query.capabilities.HighlightingSupport;
import de.faz.modules.query.capabilities.SearchOptionFactory;
import de.faz.modules.query.exception.UnsupportedFeatureException;
import de.faz.modules.query.solr.capabilities.SolrGroupingSupportFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class SolrContextCapabilitiesTest {

	SolrContextCapabilities underTest;

	@Before
	public void setUp() {
		underTest = new SolrContextCapabilities();
	}

	@Test
	public void hasSupportFor_withGroupingSupport_returnsTrue() {
		assertTrue("solr context should have grouping support.", underTest.hasSupportFor(GroupingSupport.class));
	}

	@Test
	public void hasSupportFor_withHighlightingSupport_returnsTrue() {
		assertTrue("solr context should have highlighting support.", underTest.hasSupportFor(HighlightingSupport.class));
	}

	@Test
	public void getSearchOptionFactoryFor_withGroupingSupport_returnsSolrGroupingSupportFactory() {
		SearchOptionFactory<GroupingSearchOption> factory = underTest.getSearchOptionFactoryFor(GroupingSupport.class);
		assertTrue(SolrGroupingSupportFactory.class.isAssignableFrom(factory.getClass()));
	}

	@Test(expected = UnsupportedFeatureException.class)
	public void getSearchOptionFactoryFor_withUnsupportedFeature_throwsUnsupportedFeatureException() {
		underTest.getSearchOptionFactoryFor(UnsupportedFeature.class);
	}
}

interface UnsupportedFeature extends FeatureSupport {}
