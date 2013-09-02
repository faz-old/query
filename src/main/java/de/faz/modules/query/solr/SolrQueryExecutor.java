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

import com.polopoly.search.solr.QueryDecorator;
import de.faz.modules.query.FieldDefinitionGenerator;
import de.faz.modules.query.Query;
import de.faz.modules.query.QueryExecutor;
import de.faz.modules.query.SearchContext;
import de.faz.modules.query.SearchSettings;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
class SolrQueryExecutor extends QueryExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(SolrQueryExecutor.class);

    private final FieldDefinitionGenerator generator;

	private final SolrServer server;

	private List<QueryDecorator> decoratorList;

//    SolrQueryExecutor(SolrSearchClient client) {
//        this(client, new FieldDefinitionGenerator());
//    }
//
//    SolrQueryExecutor(SolrSearchClient client, FieldDefinitionGenerator generator) {
//        this(((SolrClientImpl)client.getServiceControl()).getSolrServer(), generator);
//    }

	SolrQueryExecutor(SolrServer server) {
		this(server, new FieldDefinitionGenerator());
	}

	SolrQueryExecutor(SolrServer server, FieldDefinitionGenerator generator) {
		this.server = server;
		this.generator = generator;
		this.decoratorList = new ArrayList<>();
	}

	public void addQueryDecorator(QueryDecorator decorator) {
		this.decoratorList.add(decorator);
	}

    @Override
    @Nonnull
    protected SearchContext.SearchResult executeQuery(@Nonnull final Query query, @Nonnull final SearchSettings settings) {
        if(query == null) { throw new IllegalArgumentException("A query instance is required to perform a search."); }
        if(settings == null) { throw new IllegalArgumentException("Settings are required to perform a search."); }
        if(server == null) { return createDefaultResult(settings.getPageSize()); }

        int numOfElementsOnPage = settings.getPageSize();
        SolrSearchResult result;
        if(!query.isEmpty()) {
            SolrQuery solrQuery = createQuery(query, settings);
			solrQuery.setRows(numOfElementsOnPage);
            try {
	            QueryResponse solrResult = server.query(solrQuery);
	            int page = 0;
                if(settings.getOffset().isPresent()) {
                    page = settings.getPageSize() > 0 ? settings.getOffset().get() / settings.getPageSize() : 0;
                }

                result =  new SolrSearchResult(
		                generator
                        , solrResult
                        , numOfElementsOnPage
                        , page
                        , settings.getCustomCallbackFactory()
                );
            } catch (SolrServerException e) {
                LOG.warn("got exception when execute a search to solr", e);
                result = createDefaultResult(numOfElementsOnPage);
//            } catch (ServiceNotAvailableException e) {
//                LOG.warn("solr service isn't available", e);
//                result = createDefaultResult(numOfElementsOnPage);
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
	    for(QueryDecorator decorator :decoratorList) {
		    decorator.decorate(solrQuery);
	    }
        return solrQuery;
    }
}
