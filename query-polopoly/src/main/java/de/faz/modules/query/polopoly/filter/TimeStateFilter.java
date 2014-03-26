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

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nonnull;

import org.apache.solr.client.solrj.SolrQuery;

import com.google.common.base.Optional;
import com.polopoly.search.solr.QueryDecorator;
import com.polopoly.search.solr.schema.IndexFields;

import de.faz.modules.query.DateOption;
import de.faz.modules.query.Query;
import de.faz.modules.query.SearchContext;
import de.faz.modules.query.SearchDecorator;
import de.faz.modules.query.SearchSettings;
import de.faz.modules.query.polopoly.mapping.PolopolyContentMapping;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class TimeStateFilter implements QueryDecorator, SearchDecorator {

	public static String SUPPRESS_TIMESTATE_FILTER = "suppressTSFilter";
	private static final String QUERY = "{!cache=false}(" + IndexFields.ON_TIME_DEFINED + ":false OR " + IndexFields.ON_TIME + ":[* TO NOW]) AND (" + IndexFields.OFF_TIME_DEFINED + ":false OR " + IndexFields.OFF_TIME + ":[NOW TO *])";

	private final SearchContext context;

	public TimeStateFilter() {
		this.context = null;
	}

	public TimeStateFilter(@Nonnull final SearchContext context) {
		this.context = context;
	}

	@Override
	public SolrQuery decorate(final SolrQuery solrQuery) {
		if(!"true".equals(solrQuery.get(SUPPRESS_TIMESTATE_FILTER, ""))) {
			return solrQuery.addFilterQuery(QUERY);
		}

		return solrQuery;
	}

	@Nonnull
	@Override
	public Query decorateQuery(@Nonnull final Query q) {
		return q;
	}

	@Nonnull
	@Override
	public SearchSettings decorateSettings(@Nonnull final SearchSettings settings) {
		Optional<Boolean> suppressFilter = settings.getParameter (SUPPRESS_TIMESTATE_FILTER);
		if(context != null && !suppressFilter.or(false)) {
			Date calculatedNow = flattenNowToMinuteInterval(5);
			PolopolyContentMapping fieldDef = context.createFieldDefinitionFor(PolopolyContentMapping.class);
			Query timeStateQuery = context.createQuery(Query.Operator.AND);

			timeStateQuery.add(
					timeStateQuery.or(
							timeStateQuery.term(fieldDef.getOnTimeDefined()).value("false")
							, timeStateQuery.term(fieldDef.getOnTime()).range(DateOption.WILDCARD, DateOption.from(calculatedNow))
					)
			);

			timeStateQuery.add(
					timeStateQuery.or(
							timeStateQuery.term(fieldDef.getOffTimeDefined()).value("false")
							, timeStateQuery.term(fieldDef.getOffTime()).range(DateOption.from(calculatedNow), DateOption.WILDCARD)
					)
			);

			settings.filterBy(timeStateQuery);

		}
		return settings;
	}

	Date flattenNowToMinuteInterval(int minutes) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.add(Calendar.MINUTE, cal.get(Calendar.MINUTE) % minutes * -1);

		return cal.getTime();
	}
}
