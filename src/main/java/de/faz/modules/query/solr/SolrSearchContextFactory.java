package de.faz.modules.query.solr;

import com.polopoly.search.solr.SolrSearchClient;
import de.faz.modules.query.DefaultSearchContext;
import de.faz.modules.query.SearchContext;

import javax.annotation.Nonnull;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class SolrSearchContextFactory {

    private SolrSearchContextFactory(){}

    public static SearchContext createSearchContext(@Nonnull SolrSearchClient client) {
        return new DefaultSearchContext(new SolrQueryExecutor(client));
    }
}
