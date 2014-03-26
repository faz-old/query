package de.faz.modules.query.polopoly.filter;

import com.google.common.base.Optional;
import de.faz.modules.query.DateOption;
import de.faz.modules.query.MockedSearchContext;
import de.faz.modules.query.Query;
import de.faz.modules.query.QueryExecutor;
import de.faz.modules.query.SearchContext;
import de.faz.modules.query.SearchSettings;
import de.faz.modules.query.polopoly.mapping.PolopolyContentMapping;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
@RunWith(MockitoJUnitRunner.class)
public class TimeStateFilterTest {

	@Mock
	SearchSettings settings;

	private SearchContext context;

	private TimeStateFilter underTest;

	@Before
	public void setUp() {
		QueryExecutor executor = mock(QueryExecutor.class);
		context = new MockedSearchContext(executor);
		underTest = new TimeStateFilter(context);
		when(settings.getParameter(TimeStateFilter.SUPPRESS_TIMESTATE_FILTER)).thenReturn(Optional.<Object>absent());
	}


	@Test
	public void decorateQuery_returnAlwaysUnmodifiedQuery() {
		Query q = mock(Query.class);
		assertSame(q, underTest.decorateQuery(q));
	}

	@Test
	public void decorateSettings_withoutSearchContext_returnUnmodifiedSetting() {
		underTest = new TimeStateFilter();
		assertSame(settings, underTest.decorateSettings(settings));
	}

	@Test
	public void decorateSettings_withSearchContext_addTimeStateFilterToSettings() {
		underTest.decorateSettings(settings);
		ArgumentCaptor<Query> filter = ArgumentCaptor.forClass(Query.class);
		verify(settings).filterBy(filter.capture());
		assertEquals(createTimestateQuery(), filter.getValue());
	}

	@Test
	public void decorateSettings_withOmmitParameterSet_doNotAddFilterToSettings() {
		when(settings.getParameter(TimeStateFilter.SUPPRESS_TIMESTATE_FILTER)).thenReturn(Optional.<Object>of(true));
		underTest.decorateSettings(settings);
		verify(settings, never()).filterBy(any(Query.class));
	}

	private Query createTimestateQuery() {
		Query q = context.createQuery(Query.Operator.AND);
		PolopolyContentMapping fieldDef = context.createFieldDefinitionFor(PolopolyContentMapping.class);
		Date calculatedNow = underTest.flattenNowToMinuteInterval(5);
		q.add(
			q.or(
				q.term(fieldDef.getOnTimeDefined()).value("false")
				, q.term(fieldDef.getOnTime()).range(DateOption.WILDCARD, DateOption.from(calculatedNow))
			)
		);

		q.add(
			q.or(
				q.term(fieldDef.getOffTimeDefined()).value("false")
				, q.term(fieldDef.getOffTime()).range(DateOption.from(calculatedNow), DateOption.WILDCARD)
			)
		);

		return q;
	}
}