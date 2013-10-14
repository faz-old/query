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
import de.faz.modules.query.Query;
import de.faz.modules.query.QueryExecutor;
import de.faz.modules.query.SearchContext;
import de.faz.modules.query.SearchSettings;
import de.faz.modules.query.fields.FieldDefinitionGenerator;
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

	private final FieldDefinitionGenerator fieldGenerator;

	private final SolrServer server;

	private final List<QueryDecorator> decoratorList;

	SolrQueryExecutor(final SolrServer server, final FieldDefinitionGenerator generator) {
		this.server = server;
		this.fieldGenerator = generator;
		this.decoratorList = new ArrayList<>();
	}

	public void addQueryDecorator(final QueryDecorator decorator) {
		this.decoratorList.add(decorator);
	}

	@Override
	@Nonnull
	protected SearchContext.SearchResult executeQuery(@Nonnull final Query query, @Nonnull final SearchSettings settings) {
		if(query == null) { throw new IllegalArgumentException("A query instance is required to perform a search."); }
		if(settings == null) { throw new IllegalArgumentException("Settings are required to perform a search."); }

		int numOfElementsOnPage = settings.getPageSize();
		SolrSearchResult result = createDefaultResult(settings.getPageSize());
		if(server != null && !query.isEmpty()) {
			SolrQuery solrQuery = createQuery(query, settings);
			solrQuery.setRows(numOfElementsOnPage);
			try {
				if(LOG.isDebugEnabled()) {
					LOG.debug("Executing query: {}", solrQuery.toString());
				}
				QueryResponse solrResult = server.query(solrQuery);
				int page = 0;
				if(settings.getOffset().isPresent()) {
					page = settings.getPageSize() > 0 ? settings.getOffset().get() / settings.getPageSize() : 0;
				}

				//TODO ugly but it works. refactor this instanceof to a method that handles SolrSearchSettings only
				if(settings instanceof SolrSearchSettings) {
					result =  new SolrSearchResult(
							fieldGenerator
							, solrResult
							, numOfElementsOnPage
							, page
							, ((SolrSearchSettings)settings).getCustomCallbackFactory()
					);
				}
			} catch (SolrServerException e) {
				LOG.warn("got exception when execute a search to solr", e);
			}
		}

		return result;
	}

	private SolrSearchResult createDefaultResult(final int numOfElementsOnPage) {
		return new SolrSearchResult(null, numOfElementsOnPage);
	}

	private SolrQuery createQuery(final Query q, final SearchSettings settings) {
		Query decoratedQuery = q;
		SearchSettings decoratedSettings = settings;

		SolrQuery solrQuery = new SolrQuery(decoratedQuery.toString());
		decoratedSettings.getQueryExecutor().enrich(solrQuery);
		for(QueryDecorator decorator :decoratorList) {
			decorator.decorate(solrQuery);
		}
		return solrQuery;
	}
}
