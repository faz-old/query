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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Nonnull;

import org.apache.solr.client.solrj.SolrQuery;

import com.polopoly.cm.app.util.impl.PreviewUtil;
import com.polopoly.search.solr.QueryDecorator;
import com.polopoly.search.solr.schema.IndexFields;

import de.faz.modules.query.DateOption;
import de.faz.modules.query.Query;
import de.faz.modules.query.SearchContext;
import de.faz.modules.query.SearchDecorator;
import de.faz.modules.query.SearchSettings;
import de.faz.modules.query.polopoly.mapping.PolopolyContentMapping;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class TimeStateWithPreviewTimeFilter implements QueryDecorator,SearchDecorator {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	private final SearchContext context;
	private final TimeStateFilter filter;

	public TimeStateWithPreviewTimeFilter() {
		this(null, new TimeStateFilter());
	}

	public TimeStateWithPreviewTimeFilter(SearchContext context) {
		this(context, new TimeStateFilter(context));
	}

	public TimeStateWithPreviewTimeFilter(final SearchContext context, final TimeStateFilter filter) {
		this.context = context;
		this.filter  = filter;
	}

	@Override
	public SolrQuery decorate(final SolrQuery solrQuery) {
		Date date = PreviewUtil.getPreviewTimeFromThreadLocal();
		if(date == null) {
			return filter.decorate(solrQuery);
		}
		String formattedDate = DATE_FORMAT.format(date);
		return solrQuery.addFilterQuery("(" + IndexFields.ON_TIME_DEFINED + ":false OR " + IndexFields.ON_TIME + ":[* TO " + formattedDate + "]) AND (" + IndexFields.OFF_TIME_DEFINED + ":false OR " + IndexFields.OFF_TIME + ":[" + formattedDate + " TO *])");
	}

	@Nonnull
	@Override
	public Query decorateQuery(@Nonnull final Query q) {
		return q;
	}

	@Nonnull
	@Override
	public SearchSettings decorateSettings(@Nonnull final SearchSettings settings) {
		Date date = PreviewUtil.getPreviewTimeFromThreadLocal();
		if(date == null || context == null) {
			return filter.decorateSettings(settings);
		}
		return settings.filterBy(createPreviewTimeFilter(date));
	}

	private Query createPreviewTimeFilter(final Date date) {
		PolopolyContentMapping fieldDef = context.createFieldDefinitionFor(PolopolyContentMapping.class);
		Query q = context.createQuery(Query.Operator.AND);
		q.add(
			q.or(
				q.term(fieldDef.getOnTimeDefined()).value("false")
				, q.term(fieldDef.getOnTime()).range(DateOption.WILDCARD, DateOption.from(date))
			)
		);
		q.add(
			q.or(
				q.term(fieldDef.getOffTimeDefined()).value("false")
				, q.term(fieldDef.getOffTime()).range(DateOption.from(date), DateOption.WILDCARD)
			)
		);
		return q;
	}
}
