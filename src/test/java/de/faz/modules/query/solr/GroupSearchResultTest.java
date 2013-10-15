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
package de.faz.modules.query.solr;

import de.faz.modules.query.TestMapping;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
@RunWith(MockitoJUnitRunner.class)
public class GroupSearchResultTest {
	@Mock(answer = Answers.RETURNS_DEEP_STUBS) QueryResponse response;
	@Mock SolrSearchResult result;

	private GroupSearchResult underTest;

	@Before
	public void setUp() {
		when(result.getSolrResponse()).thenReturn(response);
		underTest = new GroupSearchResult(result);
	}

	@Test
	public void getGroupCount_withoutGroups_returnsZero() {
		assertEquals(0, underTest.getGroupCount());
	}

	@Test
	public void getGroupCount_withGroups_returnCorrectGroupCount() {
		decorateResponseWithCommands(createGroup(), createGroup());
		assertEquals(2, underTest.getGroupCount());
	}

	@Test
	public void getGroup_withoutSolrResponse_returnDefaultIterator() {
		when(result.getSolrResponse()).thenReturn(null);
		Iterator<Object> defaultIterator = mock(Iterator.class);
		when(result.createDefaultIterator()).thenReturn(defaultIterator);
		assertSame(defaultIterator, underTest.getGroup(0, TestMapping.class));
	}

	@Test
	public void getGroup_WithOneGroup_returnIteratorCreatedWithGroupResults() {
		GroupCommand command = createGroup();
		SolrDocumentList results = mock(SolrDocumentList.class);
		when(command.getValues().get(0).getResult()).thenReturn(results);

		decorateResponseWithCommands(command);
		underTest.getGroup(0, TestMapping.class);
		verify(result).createIteratorFromDocumentList(response, TestMapping.class, results);
	}

	@Test
	public void getResultCount_withoutResults_returnsZero() {
		GroupCommand command = createGroup();
		SolrDocumentList results = mock(SolrDocumentList.class);
		when(command.getValues().get(0).getResult()).thenReturn(results);

		decorateResponseWithCommands(command);

		assertEquals(0, underTest.getResultCount(0));

	}

	private void decorateResponseWithCommands(GroupCommand... commands) {
		List<GroupCommand> groupCommands = Arrays.asList(commands);
		when(response.getGroupResponse().getValues()).thenReturn(groupCommands);
	}

	private GroupCommand createGroup() {
		GroupCommand command = mock(GroupCommand.class, RETURNS_DEEP_STUBS);
		Group group = mock(Group.class);
		when(command.getValues()).thenReturn(Arrays.asList(group));
		return command;
	}
}
