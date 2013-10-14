package de.faz.modules.query;

import com.google.common.base.Optional;
import de.faz.modules.query.capabilities.ContextCapabilities;
import de.faz.modules.query.capabilities.FeatureSupport;
import de.faz.modules.query.capabilities.GroupingSupport;
import de.faz.modules.query.capabilities.HighlightingSupport;
import de.faz.modules.query.capabilities.SearchOption;
import de.faz.modules.query.capabilities.SearchOptionFactory;
import de.faz.modules.query.exception.UnsupportedFeatureException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
@RunWith(MockitoJUnitRunner.class)
public class SearchSettingsTest {

    @Mock FieldDefinitionGenerator generator;
	@Mock ContextCapabilities capabilities;

    private SearchSettings underTest;

    @Before
    public void setUp() {
        underTest = new SearchSettings(generator, capabilities);
    }

	@Test
	public void addParameter_withKeyAndValue_storesKeyValue() {
		underTest.addParameter("key", "value");
		assertEquals("value", underTest.getParameter("key").get());
	}

	@Test
	public void addParameter_withKeyAndValue_returnSameInstance() {
		assertSame(underTest, underTest.addParameter("key", "value"));
	}

	@Test
	public void getParameter_withoutStoredParameter_returnsOptionalAbsent() {
		Optional<Object> param = underTest.getParameter("key");
		assertFalse(param.isPresent());
	}

	@Test
	public void addHighlighting_returnsNewSearchHighlighterInstance() {
		SearchHighlighter highlighter = mock(SearchHighlighter.class);
		createSearchOptionFactoryForFeature(HighlightingSupport.class, highlighter);
		assertEquals(highlighter, underTest.addHighlighting());
	}


	@Test
	public void addHighlighting_addsHighlightingToOptions() {
		createSearchOptionFactoryForFeature(HighlightingSupport.class, mock(SearchHighlighter.class));
		SearchHighlighter highlighter = underTest.addHighlighting();
		assertTrue(underTest.getOptions().contains(highlighter));
	}

	@Test
	public void addGrouping_returnsNewGroupingSearchOption() {
		GroupingSearchOption option = mock(GroupingSearchOption.class);
		createSearchOptionFactoryForFeature(GroupingSupport.class, option);
		GroupingSearchOption grouping = underTest.addGrouping();
		assertSame(option, grouping);
	}

	@Test
	public void addGrouping_addsGroupingToOptions() {
		createSearchOptionFactoryForFeature(GroupingSupport.class, mock(GroupingSearchOption.class));
		GroupingSearchOption grouping = underTest.addGrouping();
		assertTrue(underTest.getOptions().contains(grouping));
	}

	@Test(expected = UnsupportedFeatureException.class)
	public void addGrouping_withoutFeatureSupport_throwsUnsupportedFeatureException() {
		when(capabilities.hasSupportFor(GroupingSupport.class)).thenReturn(false);
		underTest.addGrouping();
	}

	@Test(expected = UnsupportedFeatureException.class)
	public void addHighlighting_withoutFeatureSupport_throwsUnsupportedFeatureException() {
		when(capabilities.hasSupportFor(HighlightingSupport.class)).thenReturn(false);
		underTest.addHighlighting();
	}

	private <T extends SearchOption> SearchOptionFactory<T> createSearchOptionFactoryForFeature(Class<? extends FeatureSupport> supportClass, T option) {
		SearchOptionFactory factory = mock(SearchOptionFactory.class);
		when(capabilities.getSearchOptionFactoryFor(supportClass)).thenReturn(factory);
		when(capabilities.hasSupportFor(supportClass)).thenReturn(true);
		when(factory.createInstance(generator)).thenReturn(option);
		return factory;
	}


}
