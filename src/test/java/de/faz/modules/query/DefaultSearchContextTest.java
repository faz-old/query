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
package de.faz.modules.query;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
@RunWith(MockitoJUnitRunner.class)
public class DefaultSearchContextTest {

	@Mock Query q;
	@Mock SearchSettings settings;

	@Mock QueryExecutor executor;
	@Mock FieldDefinitionGenerator generator;

	private DefaultSearchContext underTest;

	@Before
	public void setUp() {
		underTest = new DefaultSearchContext(executor, generator);
	}

	@Test
	public void execute_withDecorator_callsDecorator() {
		SearchDecorator decorator = mock(SearchDecorator.class);
		underTest.addSearchDecorator(decorator);
		underTest.execute(q, settings);
		verify(decorator).decorateQuery(q);
		verify(decorator).decorateSettings(settings);
	}
}
