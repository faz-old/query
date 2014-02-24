package de.faz.modules.query.solr;

import de.faz.modules.query.fields.FieldDefinitionGenerator;
import de.faz.modules.query.Query;
import de.faz.modules.query.SearchHighlighter;
import de.faz.modules.query.SearchSettings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
@RunWith(MockitoJUnitRunner.class)
public class SolrSearchSettingsTest {

	@Mock FieldDefinitionGenerator generator;

	private SolrSearchSettings underTest;

	@Before
	public void setUp() {
		underTest = new SolrSearchSettings(generator);
	}

	@Test
	public void filterBy_withQuery_addQueryToSolrQuery() {
		Query filter = mock(Query.class);
		when(filter.toString()).thenReturn("filterToString");
		underTest.filterBy(filter);

		org.apache.solr.client.solrj.SolrQuery query = mock(org.apache.solr.client.solrj.SolrQuery.class);
		underTest.enrichQuery(query);
		verify(query).addFilterQuery("filterToString");
	}

	@Test
	public void sortBy_withSortAscending_addSortToSolrQuery() {
		when(generator.pop()).thenReturn(new FieldDefinitionGenerator.FieldDefinition("fieldName", 1));
		when(generator.isEmpty()).thenReturn(false);
		underTest.sortBy(null, SearchSettings.Order.ASC);

		org.apache.solr.client.solrj.SolrQuery query = mock(org.apache.solr.client.solrj.SolrQuery.class);
		underTest.enrichQuery(query);
		verify(query).addSortField("fieldName", org.apache.solr.client.solrj.SolrQuery.ORDER.asc);
	}

	@Test
	public void restrictByField_withFieldDefinition_addFieldToSolrQuery() {
		when(generator.pop()).thenReturn(new FieldDefinitionGenerator.FieldDefinition("fieldName", 1));
		when(generator.isEmpty()).thenReturn(false);
		underTest.retainOnlyIncludedFieldsInResult(null);

		org.apache.solr.client.solrj.SolrQuery query = mock(org.apache.solr.client.solrj.SolrQuery.class);
		underTest.enrichQuery(query);
		verify(query).addField("fieldName");
	}

	@Test
	public void withPageSize_withSize_addRowsToSolrQuery() {
		underTest.withPageSize(50);

		org.apache.solr.client.solrj.SolrQuery query = mock(org.apache.solr.client.solrj.SolrQuery.class);
		underTest.enrichQuery(query);
		verify(query).setRows(50);
	}

	@Test
	public void enrich_withoutPageSize_addDefaultRowsToSolrQuery() {
		org.apache.solr.client.solrj.SolrQuery query = mock(org.apache.solr.client.solrj.SolrQuery.class);
		underTest.enrichQuery(query);
		verify(query).setRows(SearchSettings.DEFAULT_ROWS);
	}

	@Test
	public void addHighlighting_addHighlighterAsCallbackFactory() {
		SearchHighlighter highlighter = underTest.addHighlighting();
		assertSame(highlighter, underTest.getCustomCallbackFactory());
	}

}
