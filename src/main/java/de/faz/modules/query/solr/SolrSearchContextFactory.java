package de.faz.modules.query.solr;

import com.polopoly.search.solr.QueryDecorator;
import de.faz.modules.query.DefaultSearchContext;
import de.faz.modules.query.FieldDefinitionGenerator;
import de.faz.modules.query.SearchContext;
import org.apache.solr.client.solrj.SolrServer;

import javax.annotation.Nullable;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public final class SolrSearchContextFactory {

    private SolrSearchContextFactory(){}

	public static SearchContext createSearchContext(@Nullable SolrServer server, QueryDecorator... decorators) {
		FieldDefinitionGenerator generator = new FieldDefinitionGenerator();
		SolrQueryExecutor executor = new SolrQueryExecutor(server, generator);
		if(decorators != null) {
			for(QueryDecorator decorator : decorators) {
				executor.addQueryDecorator(decorator);
			}
		}

		return new DefaultSearchContext(executor, generator);
	}
}
