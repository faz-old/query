package de.faz.modules.query.solr;

import de.faz.modules.query.SearchContext;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

/**
 * @author Andreas Kaubisch <a.kaubisch@faz.de>
 */
@RunWith(MockitoJUnitRunner.class)
public class SolrSearchContextFactoryTest {

    @Mock
    HttpSolrServer httpSolrServer;

    @Test
    public void createSearchContext_returnsNewInstanceOfSolrSearchContext() {
        SearchContext context = SolrSearchContextFactory.createSearchContext(httpSolrServer);
        assertEquals(SolrSearchContext.class, context.getClass());
    }
}
