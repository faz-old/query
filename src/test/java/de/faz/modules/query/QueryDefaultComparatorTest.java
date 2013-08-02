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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
@RunWith(MockitoJUnitRunner.class)
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
    public void term_delegateCallToQuery() {
        underTest.term(null);
        verify(originalQuery).term(null);
    }

    @Test
    public void and_delegateCallToQuery() {
        Query.QueryItem item = mock(Query.QueryItem.class);
        underTest.and(item, item);
        verify(originalQuery).and(item, item);
    }

    @Test
    public void or_withQueryItems_delegateCallToQuery() {
        Query.QueryItem item = mock(Query.QueryItem.class);
        underTest.or(item, item);
        verify(originalQuery).or(item, item);
    }

    @Test
    public void modify_delegateCallToQuery() {
        underTest.modify();
        verify(originalQuery).modify();
    }

    @Test
    public void getItemStack_delegateCallToQuery() {
        underTest.getItemStack();
        verify(originalQuery).getItemStack();
    }

    @Test
    public void not_delegateCallToQuery() {
        Query.QueryItem item = mock(Query.QueryItem.class);
        underTest.not(item);
        verify(originalQuery).not(item);
    }

    @Test
    public void toString_callModifyAllWithAnd() {
        underTest.toString();
        verify(originalQuery.modify().all().surroundWith()).and();
    }

    @Test
    public void add_delegateCallToQuery() {
        Query.QueryItem item = mock(Query.QueryItem.class);
        underTest.add(item);
        verify(originalQuery).add(item);
    }

    @Test
    public void contains_delegateCallToQuery() {
        Query.QueryItem item = mock(Query.QueryItem.class);
        underTest.contains(item);
        verify(originalQuery).contains(item);
    }

}
