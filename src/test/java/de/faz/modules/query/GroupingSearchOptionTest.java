package de.faz.modules.query;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.params.GroupParams;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
@RunWith(MockitoJUnitRunner.class)
public class GroupingSearchOptionTest {

	@Mock SolrQuery query;


	private FieldDefinitionGenerator generator;
	private TestMapping mapping;

	private GroupingSearchOption underTest;

	@Before
	public void setUp() {
		generator = new FieldDefinitionGenerator();
		underTest = new GroupingSearchOption(generator);
		mapping = generator.createFieldDefinition(TestMapping.class);
	}

	@Test
	public void getQueryExecutor_withQuery_enabledGrouping() {
		underTest.getQueryExecutor().enrich(query);
		verify(query).setParam(GroupParams.GROUP, true);
	}

	@Test
	public void getQueryExecutor_withFieldName_setGroupingFieldName() {
		underTest
			.groupByField(mapping.getField1())
			.getQueryExecutor()
			.enrich(query);
		verify(query).setParam(GroupParams.GROUP_FIELD, generator.getFieldNameOf(mapping.getField1()));
	}

	@Test
	public void getQueryExecutor_withGroupLimit_setGroupingLimit() {
		underTest
			.limitGroupResultsTo(1)
			.getQueryExecutor()
			.enrich(query);
		verify(query).setParam(GroupParams.GROUP_LIMIT, "1");
	}

	@Test
	public void getQueryExecutor_withGroupQuery_addGroupQuery() {
		Query q = createQueryMockWithStringValue("testQuery");

		underTest
			.groupBy(q)
			.getQueryExecutor()
			.enrich(query);
		verify(query).add(GroupParams.GROUP_QUERY, "testQuery");
	}

	@Test
	public void getQueryExecutor_withMultipleGroupQueries_addAllGroupQueries() {
		Query q1 = createQueryMockWithStringValue("query1");
		Query q2 = createQueryMockWithStringValue("query2");

		underTest
				.groupBy(q1)
				.groupBy(q2)
				.getQueryExecutor()
				.enrich(query);
		verify(query).add(GroupParams.GROUP_QUERY, "query1");
		verify(query).add(GroupParams.GROUP_QUERY, "query2");
	}

	@Test
	public void getQueryExecutor_withMergedResults_addMergeParam() {
		underTest.mergeResults().getQueryExecutor().enrich(query);
		verify(query).setParam(GroupParams.GROUP_MAIN, true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void groupByField_withoutFieldDefinition_throwsIllegalArgumentException() {
		underTest.groupByField(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void limitGroupResultsTo_withNegativeLimit_throwsIllegalArgumentException() {
		underTest.limitGroupResultsTo(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void limitGroupResultsTo_withZeroLimit_throwsIllegalArgumentException() {
		underTest.limitGroupResultsTo(0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void groupBy_withoutQuery_throwsIllegalArgumentException() {
		underTest.groupBy(null);
	}

	private Query createQueryMockWithStringValue(String value) {
		Query q = mock(Query.class);
		when(q.toString()).thenReturn(value);
		return q;
	}
}