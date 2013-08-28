package de.faz.modules.query;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.params.GroupParams;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

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
	public void getQueryExecutor_withQuery_enableGroupingMain() {
		underTest
			.getQueryExecutor()
			.enrich(query);
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
}
