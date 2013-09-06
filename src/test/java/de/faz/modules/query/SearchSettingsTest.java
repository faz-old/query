package de.faz.modules.query;

import com.google.common.base.Optional;
import org.apache.solr.client.solrj.SolrQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
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

	@Test
	public void addParameter_withKeyAndValue_storesKeyValue() {
		underTest.addParameter("key", "value");
		assertEquals("value", underTest.getParameter("key").get());
	}

	@Test
	public void addParameter_withKeyAndValue_returnSameInstance() {
		assertSame(underTest, underTest.addParameter("key", "value"));
	}

	@Test
	public void getParameter_withoutStoredParameter_returnsOptionalAbsent() {
		Optional<Object> param = underTest.getParameter("key");
		assertFalse(param.isPresent());
	}
}
