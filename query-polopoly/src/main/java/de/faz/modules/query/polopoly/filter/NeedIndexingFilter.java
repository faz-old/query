package de.faz.modules.query.polopoly.filter;

import de.faz.modules.query.Query;
import de.faz.modules.query.SearchContext;
import de.faz.modules.query.SearchDecorator;
import de.faz.modules.query.SearchSettings;
import de.faz.modules.query.polopoly.mapping.PolopolyContentMapping;

import javax.annotation.Nonnull;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class NeedIndexingFilter implements SearchDecorator {

	private final SearchContext context;

	public NeedIndexingFilter(final SearchContext context) {
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
		q.add(q.not(q.term(fieldDef.getNeedsIndexing()).value("true")));
		settings.filterBy(q);
		return settings;
	}
}