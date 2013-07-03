package de.faz.modules.query.solr;

import com.polopoly.management.ServiceNotAvailableException;
import com.polopoly.search.solr.SearchResult;
import com.polopoly.search.solr.SolrSearchClient;
import de.faz.modules.query.FieldDefinitionGenerator;
import de.faz.modules.query.Query;
import de.faz.modules.query.SearchContext;
import de.faz.modules.query.SearchSettings;
import de.faz.modules.query.TestMapping;
import de.faz.modules.query.solr.SolrQueryExecutor;
import net.sf.cglib.proxy.Callback;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
@RunWith(MockitoJUnitRunner.class)
public class SolrQueryExecutorTest {

    @Mock SolrSearchClient searchClient;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS) SearchResult polopolyResult;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS) QueryResponse solrResponse;

    @Mock
    Query q;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    SearchSettings settings;
    @Mock
    FieldDefinitionGenerator generator;
    private SolrQueryExecutor executor;

    @Before
    public void setUp() throws ServiceNotAvailableException, SolrServerException {
        when(searchClient.search(any(SolrQuery.class), anyInt())).thenReturn(polopolyResult);
        when(polopolyResult.getPage(0).getQueryResponses().get(0)).thenReturn(solrResponse);

        executor = new SolrQueryExecutor(searchClient, generator);
    }

    @Test
    public void executeQuery_withoutSearchClient_returnsDefaultResult() {
        executor = new SolrQueryExecutor(null);
        when(settings.getPageSize()).thenReturn(10);
        SearchContext.SearchResult result = executor.executeQuery(q, settings);
        Iterator it = result.getResultsForMapping(Object.class);
        assertFalse(it.hasNext());
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeQuery_withoutQuery_throwsIllegalArgumentException() {
        executor.executeQuery(null, settings);
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeQuery_withoutSettings_throwsIllegalArgumentException() {
        executor.executeQuery(q, null);
    }

    @Test
    public void executeQuery_withQueryAndSettings_callSearchClient() throws ServiceNotAvailableException, SolrServerException {
        executor.executeQuery(q, settings);
        verify(searchClient).search(any(SolrQuery.class), anyInt());
    }

    @Test
    public void executeQuery_withQueryAndSettings_verifySolrQueryHasQueryString() {
        executor.executeQuery(q, settings);
        ArgumentCaptor<SolrQuery> queryArg = ArgumentCaptor.forClass(SolrQuery.class);
        verify(searchClient).search(queryArg.capture(), anyInt());
        Assert.assertEquals(queryArg.getValue().getQuery(), q.toString());
    }

    @Test
    public void executeQuery_withSettings_verifyEnrichisCalled() {
        executor.executeQuery(q, settings);
        ArgumentCaptor<SolrQuery> queryArg = ArgumentCaptor.forClass(SolrQuery.class);
        verify(searchClient).search(queryArg.capture(), anyInt());
        verify(settings.getQueryExecutor()).enrich(queryArg.getValue());
    }

    @Test
    public void executeQuery_withResult_createIteratorWithEnhancement() {
        SearchContext.SearchResult searchResult = executor.executeQuery(q, settings);
        SolrDocument doc = mock(SolrDocument.class);
        when(solrResponse.getResults().iterator().hasNext()).thenReturn(true);
        when(solrResponse.getResults().iterator().next()).thenReturn(doc);

        searchResult.getResultsForMapping(TestMapping.class).next();
        verify(generator).enhanceWithInterceptor(eq(TestMapping.class), any(Callback.class));
    }

    @Test
    public void executeQuery_withResult_verifyFactoryIsGetFromSettings() {
        executor.executeQuery(q, settings);
        verify(settings).getCustomCallbackFactory();
    }

    @Test
    public void executeQuery_withServiceNotAvailableException_returnsStandardResult() throws ServiceNotAvailableException, SolrServerException {
        when(polopolyResult.getPage(anyInt())).thenThrow(new ServiceNotAvailableException("message"));
        SearchContext.SearchResult result = executor.executeQuery(q, settings);
        assertEquals(0, result.getNumCount());
    }

    @Test
    public void executeQuery_withSolrServerException_returnStandardResult() throws ServiceNotAvailableException, SolrServerException {
        when(polopolyResult.getPage(anyInt())).thenThrow(new SolrServerException("message"));
        SearchContext.SearchResult result = executor.executeQuery(q, settings);
        assertEquals(0, result.getNumCount());
    }
}