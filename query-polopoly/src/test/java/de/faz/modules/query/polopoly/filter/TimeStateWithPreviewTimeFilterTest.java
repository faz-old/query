/*
 * Copyright (c) 2013. F.A.Z. Electronic Media GmbH
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of F.A.Z. Electronic Media GmbH and its suppliers,
 * if any. The intellectual and technical concepts contained
 * herein are proprietary to F.A.Z. Electronic Media GmbH
 * and its suppliers and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from F.A.Z. Electronic Media GmbH.
 */
package de.faz.modules.query.polopoly.filter;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.polopoly.cm.app.util.impl.PreviewUtil;

import de.faz.modules.query.DateOption;
import de.faz.modules.query.MockedSearchContext;
import de.faz.modules.query.Query;
import de.faz.modules.query.QueryExecutor;
import de.faz.modules.query.SearchContext;
import de.faz.modules.query.SearchSettings;
import de.faz.modules.query.polopoly.mapping.PolopolyContentMapping;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PreviewUtil.class)
public class TimeStateWithPreviewTimeFilterTest {

	@Mock TimeStateFilter filter;
	@Mock
	SearchSettings settings;

	private SearchContext context;

	private TimeStateWithPreviewTimeFilter underTest;

	@Before
	public void setUp() {
		QueryExecutor executor = mock(QueryExecutor.class);
		context = new MockedSearchContext(executor);
		underTest = new TimeStateWithPreviewTimeFilter(context, filter);
	}

	@Test
	public void decorateQuery_alwaysReturnQueryFromArguments() {
		Query q = mock(Query.class);
		assertSame(q, underTest.decorateQuery(q));
	}

	@Test
	public void decorateSettings_withoutSpecificPreviewTime_delegateToTimeStateFilter() throws Exception {
		mockStatic(PreviewUtil.class);
		when(PreviewUtil.getPreviewTimeFromThreadLocal()).thenReturn(null);
		underTest.decorateSettings(settings);
		verify(filter).decorateSettings(settings);
	}

	@Test
	public void decorateSettings_withSpecificPreviewTime_addPreviewTimeFilterToSettings() {
		mockStatic(PreviewUtil.class);
		Date now = new Date();
		when(PreviewUtil.getPreviewTimeFromThreadLocal()).thenReturn(now);
		underTest.decorateSettings(settings);
		ArgumentCaptor<Query> filter = ArgumentCaptor.forClass(Query.class);
		verify(settings).filterBy(filter.capture());
		assertEquals(createPreviewTimeFilter(now), filter.getValue());
	}

	private Query createPreviewTimeFilter(final Date date) {
		PolopolyContentMapping fieldDef = context.createFieldDefinitionFor(PolopolyContentMapping.class);
		Query q = context.createQuery(Query.Operator.AND);
		q.add(
			q.or(
				q.term(fieldDef.getOnTimeDefined()).value("false")
				, q.term(fieldDef.getOnTime()).range(DateOption.WILDCARD, DateOption.from(date))
			)
		);
		q.add(
			q.or(
				q.term(fieldDef.getOffTimeDefined()).value("false")
				, q.term(fieldDef.getOffTime()).range(DateOption.from(date), DateOption.WILDCARD)
			)
		);

		return q;
	}
}
