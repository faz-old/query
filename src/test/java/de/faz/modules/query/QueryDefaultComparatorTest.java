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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
@RunWith(MockitoJUnitRunner.class)
@Ignore
public class QueryDefaultComparatorTest {
    @Mock(answer = Answers.RETURNS_DEEP_STUBS) Query originalQuery;
    Query.Operator operator;
    @InjectMocks private QueryDefaultComparator underTest;

    @Before
    public void setUp() {
        operator = Query.Operator.AND;
        underTest = new QueryDefaultComparator(originalQuery, operator);
    }

    @Test
    public void toString_callModifyAllWithAnd() {
        underTest.toString();
        verify(originalQuery.modify().all().surroundWith()).and();
    }

	@Test
	public void equals_withOtherInstance_callsQueryEqualsWithOtherContainingQuery() {
		QueryDefaultComparator otherInstance = new QueryDefaultComparator(originalQuery, operator);
		assertTrue(underTest.equals(otherInstance));
	}
}
