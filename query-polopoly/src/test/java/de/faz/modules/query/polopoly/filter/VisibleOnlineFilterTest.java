package de.faz.modules.query.polopoly.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.faz.modules.query.MockedSearchContext;
import de.faz.modules.query.Query;
import de.faz.modules.query.QueryExecutor;
import de.faz.modules.query.SearchContext;
import de.faz.modules.query.SearchSettings;
import de.faz.modules.query.polopoly.mapping.PolopolyContentMapping;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
@RunWith(MockitoJUnitRunner.class)
public class VisibleOnlineFilterTest {

	@Mock
	Query q;
	@Mock
	SearchSettings settings;
	@Mock
	QueryExecutor executor;

	private SearchContext context;

	private VisibleOnlineFilter underTest;

	@Before
	public void setUp() {
		context = new MockedSearchContext(executor);
		underTest = new VisibleOnlineFilter(context);
	}

	@Test
	public void decorateQuery_withQuery_returnsSameQuery() {
		assertSame(q, underTest.decorateQuery(q));
	}

	@Test
	public void decorateQuery_withQuery_doNotInteractWithQuery() {
		underTest.decorateQuery(q);
		verifyZeroInteractions(q);
	}

	@Test
	public void decorateSettings_withSettings_returnSameSettings() {
		assertSame(settings, underTest.decorateSettings(settings));
	}

	@Test
	public void decorateSettings_withSettings_addVisibleOnlineFilter() {
		Query expectedQuery = context.createQuery();
		PolopolyContentMapping fieldDef = context.createFieldDefinitionFor(PolopolyContentMapping.class);
		expectedQuery.add(expectedQuery.term(fieldDef.getVisibleOnline()).value("true"));
		underTest.decorateSettings(settings);
		ArgumentCaptor<Query> filter = ArgumentCaptor.forClass(Query.class);
		verify(settings).filterBy(filter.capture());
		assertEquals(expectedQuery, filter.getValue());
	}
}
