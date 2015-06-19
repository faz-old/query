package de.faz.modules.query.solr;

import javax.annotation.Nullable;

import de.faz.modules.query.SearchContext;
import de.faz.modules.query.fields.FieldDefinitionGenerator;
import org.apache.solr.client.solrj.impl.HttpSolrServer;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public final class SolrSearchContextFactory {

    private SolrSearchContextFactory(){
	    //empty constructor
    }

	public static SearchContext createSearchContext(@Nullable final HttpSolrServer httpSolrServer) {
		FieldDefinitionGenerator generator = new FieldDefinitionGenerator();
		SolrQueryExecutor executor = new SolrQueryExecutor(httpSolrServer, generator);

		return new SolrSearchContext(executor, generator);
	}
}
