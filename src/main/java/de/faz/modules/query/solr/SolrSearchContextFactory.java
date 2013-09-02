package de.faz.modules.query.solr;

import com.polopoly.search.solr.QueryDecorator;
import com.polopoly.search.solr.SolrClientImpl;
import com.polopoly.search.solr.SolrSearchClient;
import de.faz.modules.query.DefaultSearchContext;
import de.faz.modules.query.QueryExecutor;
import de.faz.modules.query.SearchContext;
import org.apache.solr.client.solrj.SolrServer;

import javax.annotation.Nonnull;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class SolrSearchContextFactory {

    private SolrSearchContextFactory(){}

    public static SearchContext createSearchContext(@Nonnull SolrSearchClient client) {
	    QueryExecutor executor = null;
	    if(client != null && client.getServiceControl() instanceof SolrClientImpl) {
		    executor = new SolrQueryExecutor(((SolrClientImpl)client.getServiceControl()).getSolrServer());
	    } else {
		    executor = new SolrQueryExecutor(null);
	    }
        return new DefaultSearchContext(executor);
    }

	public static SearchContext createSearchContext(@Nonnull SolrServer server, QueryDecorator... decorators) {
		SolrQueryExecutor executor = new SolrQueryExecutor(server);
		if(decorators != null) {
			for(QueryDecorator decorator : decorators) {
				executor.addQueryDecorator(decorator);
			}
		}

		return new DefaultSearchContext(executor);
	}
}
