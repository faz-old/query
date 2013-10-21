package de.faz.modules.query.solr;

import de.faz.modules.query.DefaultSearchContext;
import de.faz.modules.query.Query;
import de.faz.modules.query.QueryExecutor;
import de.faz.modules.query.SearchSettings;
import de.faz.modules.query.fields.FieldDefinitionGenerator;

import javax.annotation.Nonnull;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class SolrSearchContext extends DefaultSearchContext {

	public static final int DEFAULT_ROWS = 10;

	public SolrSearchContext(final QueryExecutor executor) {
		super(executor);
	}

	public SolrSearchContext(final QueryExecutor executor, final FieldDefinitionGenerator generator) {
		super(executor, generator);
	}

	@Nonnull
	@Override
	public Query createQuery() {
		return new SolrQuery(generator);
	}

	@Nonnull
	@Override
	public SearchSettings withSettings() {
		return createDefaultSettings();
	}

	@Nonnull
	private SearchSettings createDefaultSettings() {
		return new SolrSearchSettings(generator).withPageSize(DEFAULT_ROWS).startAt(0);
	}
}
