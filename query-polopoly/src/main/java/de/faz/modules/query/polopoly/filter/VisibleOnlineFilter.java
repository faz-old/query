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
package de.faz.modules.query.polopoly.filter;

import javax.annotation.Nonnull;

import org.apache.solr.client.solrj.SolrQuery;

import com.polopoly.search.solr.QueryDecorator;
import com.polopoly.search.solr.schema.IndexFields;

import de.faz.modules.query.Query;
import de.faz.modules.query.SearchContext;
import de.faz.modules.query.SearchDecorator;
import de.faz.modules.query.SearchSettings;
import de.faz.modules.query.polopoly.mapping.PolopolyContentMapping;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class VisibleOnlineFilter implements QueryDecorator, SearchDecorator {

	private final SearchContext context;

	public VisibleOnlineFilter() {
		context = null;
	}

	public VisibleOnlineFilter(SearchContext context) {
		this.context = context;
	}

	@Nonnull
	@Override
	public Query decorateQuery(@Nonnull final Query q) {
		return q;
	}

	@Nonnull
	@Override
	public SearchSettings decorateSettings(@Nonnull final SearchSettings settings) {
		Query q = context.createQuery();
		PolopolyContentMapping fieldDef = context.createFieldDefinitionFor(PolopolyContentMapping.class);
		q.add(q.term(fieldDef.getVisibleOnline()).value("true"));
		settings.filterBy(q);

		return settings;
	}

	@Override
	public SolrQuery decorate(final SolrQuery solrQuery) {
		return solrQuery.addFilterQuery(IndexFields.VISIBLE_ONLINE + ":true");
	}
}
