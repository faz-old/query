package de.faz.modules.query.solr;

import javax.annotation.Nullable;

import org.apache.solr.client.solrj.SolrServer;

import de.faz.modules.query.SearchContext;
import de.faz.modules.query.fields.FieldDefinitionGenerator;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public final class SolrSearchContextFactory {

    private SolrSearchContextFactory(){
	    //empty constructor
    }

	public static SearchContext createSearchContext(@Nullable final SolrServer server) {
		FieldDefinitionGenerator generator = new FieldDefinitionGenerator();
		SolrQueryExecutor executor = new SolrQueryExecutor(server, generator);

		return new SolrSearchContext(executor, generator);
	}
}
