/*
 * Copyright (c) 2013. F.A.Z. Electronic Media GmbH
 * All Rights Reserved.
 * 
 * NOTICE: All information contained herein is, and remains
 * the property of F.A.Z. Electronic Media GmbH and its suppliers,
 * if any. The intellectual and technical concepts contained
 * herein are proprietary to F.A.Z. Electronic Media GmbH
 * and its suppliers and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from F.A.Z. Electronic Media GmbH.
 */

package de.faz.modules.query.solr;

import java.util.Objects;

import javax.annotation.Nonnull;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.faz.modules.query.Query;
import de.faz.modules.query.QueryExecutor;
import de.faz.modules.query.SearchContext;
import de.faz.modules.query.SearchSettings;
import de.faz.modules.query.fields.FieldDefinitionGenerator;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
class SolrQueryExecutor extends QueryExecutor {

	private static final Logger LOG = LoggerFactory.getLogger(SolrQueryExecutor.class);

	private final FieldDefinitionGenerator fieldGenerator;

	private final SolrServer solrServer;

	SolrQueryExecutor(final SolrServer server, final FieldDefinitionGenerator generator) {
		super();
		this.solrServer = server;
		this.fieldGenerator = generator;
	}

	@Override
	@Nonnull
	protected SearchContext.SearchResult executeQuery(@Nonnull final Query query, @Nonnull final SearchSettings settings) {
		Objects.requireNonNull(query, "A query instance is required to perform a search.");
		Objects.requireNonNull(settings, "Settings are required to perform a search.");

		int numOfElementsOnPage = settings.getPageSize();
		SolrSearchResult result = createDefaultResult(numOfElementsOnPage);
		if (canProcessQuery(query)) {
			result = processQuery(query, settings);
		}

		return result;
	}

	@Nonnull
	private SolrSearchResult processQuery(final Query query, final SearchSettings settings) {
		SolrSearchResult result = createDefaultResult(settings.getPageSize());
		try {
			QueryResponse solrResult = solrServer.query(createQuery(query, settings));

			result = mapSolrQueryToDomainResult(settings, solrResult);
		} catch (SolrServerException e) {
			LOG.warn("got exception when execute a search to solr", e);
		}
		return result;
	}

	@Nonnull
	private SolrSearchResult mapSolrQueryToDomainResult(final SearchSettings settings, final QueryResponse solrResult) {
		SolrSearchResult result = createDefaultResult(settings.getPageSize());
		//TODO ugly but it works. refactor this instanceof to a method that handles SolrSearchSettings only
		if (settings instanceof SolrSearchSettings) {
			result = new SolrSearchResult(fieldGenerator, solrResult, settings.getPageSize(), getCurrentPage(settings), ((SolrSearchSettings) settings).getCustomCallbackFactory());
		}
		return result;
	}

	private int getCurrentPage(final SearchSettings settings) {
		int page = 0;
		int offset = settings.getOffset().or(0);
		if (settings.getPageSize() > 0) {
			page = offset / settings.getPageSize();
		}
		return page;
	}

	private boolean canProcessQuery(final Query query) {
		return solrServer != null && !query.isEmpty();
	}

	private SolrSearchResult createDefaultResult(final int numOfElementsOnPage) {
		return new SolrSearchResult(null, numOfElementsOnPage);
	}

	private SolrQuery createQuery(final Query q, final SearchSettings settings) {
		SolrQuery solrQuery = new SolrQuery(q.toString());
		solrQuery.setRows(settings.getPageSize());
		settings.getQueryExecutor().enrich(solrQuery);

		LOG.debug("Executing query: {}", solrQuery);
		return solrQuery;
	}
}
