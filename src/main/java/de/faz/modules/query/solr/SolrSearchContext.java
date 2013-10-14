package de.faz.modules.query.solr;

import de.faz.modules.query.DefaultSearchContext;
import de.faz.modules.query.FieldDefinitionGenerator;
import de.faz.modules.query.Query;
import de.faz.modules.query.QueryExecutor;
import de.faz.modules.query.SearchSettings;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class SolrSearchContext extends DefaultSearchContext {

	public SolrSearchContext(final QueryExecutor executor) {
		super(executor);
	}

	public SolrSearchContext(final QueryExecutor executor, final FieldDefinitionGenerator generator) {
		super(executor, generator);
	}

	@Override
	public Query createQuery() {
		return new SolrQuery(generator);
	}

	@Override
	public SearchSettings withSettings() {
		return new SearchSettings(generator, new SolrContextCapabilities());
	}
}
