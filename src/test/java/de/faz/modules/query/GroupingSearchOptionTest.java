package de.faz.modules.query;

import org.apache.solr.client.solrj.SolrQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

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
		underTest = new TestableGroupingSearchOption(generator);
		mapping = generator.createFieldDefinition(TestMapping.class);
	}


	@Test(expected = IllegalArgumentException.class)
	public void groupByField_withoutFieldDefinition_throwsIllegalArgumentException() {
		underTest.groupByField(null);
	}

	@Test
	public void groupByField_withFieldDefinition_setsFieldName() {
		underTest.groupByField(mapping.getField1());
		assertEquals("field1", underTest.getFieldName());
	}

	@Test(expected = IllegalArgumentException.class)
	public void limitGroupResultsTo_withNegativeLimit_throwsIllegalArgumentException() {
		underTest.limitGroupResultsTo(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void limitGroupResultsTo_withZeroLimit_throwsIllegalArgumentException() {
		underTest.limitGroupResultsTo(0);
	}

	@Test
	public void limitGroupResultsTo_withPositiveLimit_setsLimit() {
		assertEquals(new Integer(10), underTest.limitGroupResultsTo(10).getLimit());
	}

	@Test(expected = IllegalArgumentException.class)
	public void groupBy_withoutQuery_throwsIllegalArgumentException() {
		underTest.groupBy(null);
	}

	@Test
	public void groupBy_withQuery_addQueryToGroupQueries() {
		Query q = mock(Query.class);
		assertTrue(underTest.groupBy(q).getGroupQueries().contains(q));
	}

	@Test
	public void mergeResults_activateMerging() {
		assertTrue(underTest.mergeResults().isMerge());
	}

	@Test
	public void isMerge_withoutMergeResults_returnsFalse() {
		assertFalse(underTest.isMerge());
	}
}

class TestableGroupingSearchOption extends GroupingSearchOption {
	public TestableGroupingSearchOption(final FieldDefinitionGenerator generator) {
		super(generator);
	}

	@Override
	public EnrichQueryExecutor getQueryExecutor() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}
}