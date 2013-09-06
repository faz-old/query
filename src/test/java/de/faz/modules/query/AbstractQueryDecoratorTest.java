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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
@RunWith(MockitoJUnitRunner.class)
public class AbstractQueryDecoratorTest {

	@Mock Query delegate;

	@InjectMocks
	private QueryDecorator underTest;


	@Test
	public void ToString_callDelegate() throws Exception {
		when(delegate.toString()).thenReturn("toString");
		assertEquals("toString", underTest.toString());
	}

	@Test
	public void modify_callDelegate() throws Exception {
		underTest.modify();
		verify(delegate).modify();
	}

	@Test
	public void and_callDelegate() throws Exception {
		underTest.and(null, null);
		verify(delegate).and(null, null);
	}

	@Test
	public void or_callDelegate() throws Exception {
		underTest.or(null, null);
		verify(delegate).or(null, null);
	}

	@Test
	public void not_CallDelegate() throws Exception {
		underTest.not(null);
		verify(delegate).not(null);
	}

	@Test
	public void add_callDelegate() throws Exception {
		underTest.add(null);
		verify(delegate).add(null);
	}

	@Test
	public void addItemsOf_callDelegate() throws Exception {
		underTest.addItemsOf(null);
		verify(delegate).addItemsOf(null);
	}

	@Test
	public void term_callDelegate() throws Exception {
		underTest.term(null);
		verify(delegate).term(null);
	}

	@Test
	public void isEmpty_callDelegate() throws Exception {
		underTest.isEmpty();
		verify(delegate).isEmpty();
	}

	@Test
	public void contains_callDelegate() throws Exception {
		underTest.contains(null);
		verify(delegate).contains(null);
	}
}

class QueryDecorator extends AbstractQueryDecorator {
	QueryDecorator(final Query q) {
		super(q);
	}
}

