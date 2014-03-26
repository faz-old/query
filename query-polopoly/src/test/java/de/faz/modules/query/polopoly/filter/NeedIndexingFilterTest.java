package de.faz.modules.query.polopoly.filter;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import de.faz.modules.query.MockedSearchContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.faz.modules.query.Query;
import de.faz.modules.query.QueryExecutor;
import de.faz.modules.query.SearchContext;
import de.faz.modules.query.SearchSettings;
import de.faz.modules.query.polopoly.mapping.PolopolyContentMapping;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
@RunWith(MockitoJUnitRunner.class)
public class NeedIndexingFilterTest {

	@Mock
	Query q;
	@Mock
	SearchSettings settings;

	@Mock
	QueryExecutor executor;

	private SearchContext context;
	private NeedIndexingFilter underTest;

	@Before
	public void setUp() {
		context = new MockedSearchContext(executor);
		underTest = new NeedIndexingFilter(context);
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
	public void decorateSettings_withSettings_addNeedsIndexingFilterToSettings() {
		Query q = context.createQuery();
		PolopolyContentMapping fieldDef = context.createFieldDefinitionFor(PolopolyContentMapping.class);
		q.add(q.not(q.term(fieldDef.getNeedsIndexing()).value("true")));
		underTest.decorateSettings(settings);
		ArgumentCaptor<Query> filter = ArgumentCaptor.forClass(Query.class);
		verify(settings).filterBy(filter.capture());
	}
}
