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

import com.polopoly.management.ServiceNotAvailableException;
import com.polopoly.search.solr.SolrSearchClient;
import de.faz.modules.query.FieldDefinitionGenerator;
import de.faz.modules.query.Mapping;
import de.faz.modules.query.Query;
import de.faz.modules.query.QueryExecutor;
import de.faz.modules.query.SearchContext;
import de.faz.modules.query.SearchSettings;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Iterator;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
class SolrQueryExecutor extends QueryExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(SolrQueryExecutor.class);

    private final SolrSearchClient client;
    private final FieldDefinitionGenerator generator;

    SolrQueryExecutor(SolrSearchClient client) {
        this(client, new FieldDefinitionGenerator());
    }

    SolrQueryExecutor(SolrSearchClient client, FieldDefinitionGenerator generator) {
        this.client = client;
        this.generator = generator;
    }

    @Override
    @Nonnull
    protected SearchContext.SearchResult executeQuery(@Nonnull final Query query, @Nonnull final SearchSettings settings) {
        if(query == null) { throw new IllegalArgumentException("A query instance is required to perform a search."); }
        if(settings == null) { throw new IllegalArgumentException("Settings are required to perform a search."); }
        if(client == null) { return createDefaultResult(settings.getPageSize()); }

        int numOfElementsOnPage = settings.getPageSize();
        SolrSearchResult result;
        if(!query.isEmpty()) {
            SolrQuery solrQuery = createQuery(query, settings);
            com.polopoly.search.solr.SearchResult solrResult = client.search(solrQuery, numOfElementsOnPage);
            try {
                int page = 0;
                if(settings.getOffset().isPresent()) {
                    page = settings.getPageSize() > 0 ? settings.getOffset().get() / settings.getPageSize() : 0;
                }

                result =  new SolrSearchResult(
                        solrResult.getPage(page).getQueryResponses().get(0)
                        , numOfElementsOnPage
                        , page
                        , settings.getCustomCallbackFactory()
                );
            } catch (SolrServerException e) {
                LOG.warn("got exception when execute a search to solr", e);
                result = createDefaultResult(numOfElementsOnPage);
            } catch (ServiceNotAvailableException e) {
                LOG.warn("solr service isn't available", e);
                result = createDefaultResult(numOfElementsOnPage);
            }
        } else {
            result = createDefaultResult(numOfElementsOnPage);
        }

        return result;
    }

    private SolrSearchResult createDefaultResult(final int numOfElementsOnPage) {
        return new SolrSearchResult(null, numOfElementsOnPage);
    }

    private SolrQuery createQuery(Query q, SearchSettings settings) {
        SolrQuery solrQuery = new SolrQuery(q.toString());
        settings.getQueryExecutor().enrich(solrQuery);
        return solrQuery;
    }

    private class SolrSearchResult extends SearchContext.SearchResult<QueryResponse> {

        private SolrResponseCallbackFactory callbackFactory;

        public SolrSearchResult(final QueryResponse result, int pageSize) {
            super(result, pageSize);
        }

        private SolrSearchResult(final QueryResponse result, final int pageSize, final int offset, final SolrResponseCallbackFactory callbackFactory) {
            super(result, pageSize, offset);
            this.callbackFactory = callbackFactory;
        }

        public long getNumCount() {
            long numCount = 0;
            if(implementedSearchResult.isPresent()) {
                numCount = implementedSearchResult.get().getResults().getNumFound();
            }

            return numCount;
        }

        public long getNumberOfPages() {
            int numPages = 0;
            if(implementedSearchResult.isPresent()) {
                numPages = (int) Math.ceil(implementedSearchResult.get().getResults().getNumFound() / pageSize);
            }
            return numPages;
        }

        @Override
        public <S extends Mapping> Iterator<S> getResultsForMapping(final Class<S> mapping) {
            if(implementedSearchResult.isPresent()) {
                final QueryResponse response = implementedSearchResult.get();
                final Iterator<SolrDocument> solrIt = response.getResults().iterator();
                return new Iterator<S>() {
                    @Override
                    public boolean hasNext() {
                        return solrIt.hasNext();
                    }

                    @Override
                    public S next() {
                        final SolrDocument doc = solrIt.next();
                        S result = generator.enhanceWithInterceptor(mapping, callbackFactory.createCallbackForDocument(response, doc));
                        return result;
                    }

                    @Override
                    public void remove() {

                    }
                };
            } else {
                return new Iterator<S>() {
                    @Override
                    public boolean hasNext() {
                        return false;
                    }

                    @Override
                    public S next() {
                        return null;
                    }

                    @Override
                    public void remove() {
                    }
                };
            }
        }
    }
}
