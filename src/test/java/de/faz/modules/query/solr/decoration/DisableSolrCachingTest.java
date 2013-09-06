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
package de.faz.modules.query.solr.decoration;

import de.faz.modules.query.Query;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
@RunWith(MockitoJUnitRunner.class)
public class DisableSolrCachingTest {

	@Mock Query q;

	private DisableSolrCaching underTest;

	@Before
	public void setUp() {
		underTest = new DisableSolrCaching(q);
	}

	@Test
	public void toString_withQueryToString_returnsToStringWithNoCachePrefix() {
		when(q.toString()).thenReturn("query");
		assertEquals("{!cache=false}query", underTest.toString());
	}

	@Test
	public void toString_withoutEmptyQueryToString_doNotAddPrefixToString() {
		when(q.toString()).thenReturn("");
		assertEquals("", underTest.toString());
	}
}
