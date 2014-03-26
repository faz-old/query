package de.faz.modules.query.solr;

import com.polopoly.search.solr.QueryDecorator;
import de.faz.modules.query.SearchContext;
import de.faz.modules.query.fields.FieldDefinitionGenerator;
import org.apache.solr.client.solrj.SolrServer;

import javax.annotation.Nullable;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public final class SolrSearchContextFactory {

    private SolrSearchContextFactory(){
	    //empty constructor
    }

	public static SearchContext createSearchContext(@Nullable final SolrServer server, final QueryDecorator... decorators) {
		FieldDefinitionGenerator generator = new FieldDefinitionGenerator();
		SolrQueryExecutor executor = new SolrQueryExecutor(server, generator);
		if(decorators != null) {
			for(QueryDecorator decorator : decorators) {
				executor.addQueryDecorator(decorator);
			}
		}

		return new SolrSearchContext(executor, generator);
	}
}
