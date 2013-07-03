package de.faz.modules.query;

import org.apache.solr.client.solrj.SolrQuery;
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
public class SearchSettingsTest {

    @Mock FieldDefinitionGenerator generator;

    private SearchSettings underTest;

    @Before
    public void setUp() {
        underTest = new SearchSettings(generator);
    }

    @Test
    public void filterBy_withQuery_addQueryToSolrQuery() {
        Query filter = mock(Query.class);
        when(filter.toString()).thenReturn("filterToString");
        underTest.filterBy(filter);

        SolrQuery query = mock(SolrQuery.class);
        underTest.enrichQuery(query);
        verify(query).addFilterQuery("filterToString");
    }

    @Test
    public void sortBy_withSortAscending_addSortToSolrQuery() {
        when(generator.pop()).thenReturn(new FieldDefinition("fieldName", 1));
        when(generator.isEmpty()).thenReturn(false);
        underTest.sortBy(null, SearchSettings.Order.ASC);

        SolrQuery query = mock(SolrQuery.class);
        underTest.enrichQuery(query);
        verify(query).addSortField("fieldName", SolrQuery.ORDER.asc);
    }

    @Test
    public void withPageSize_withSize_addRowsToSolrQuery() {
        underTest.withPageSize(50);

        SolrQuery query = mock(SolrQuery.class);
        underTest.enrichQuery(query);
        verify(query).setRows(50);
    }

    @Test
    public void enrich_withoutPageSize_addDefaultRowsToSolrQuery() {
        SolrQuery query = mock(SolrQuery.class);
        underTest.enrichQuery(query);
        verify(query).setRows(SearchSettings.DEFAULT_ROWS);
    }
}
