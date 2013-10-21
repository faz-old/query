package de.faz.modules.query.solr;

import de.faz.modules.query.SearchContext;
import org.apache.solr.client.solrj.SolrServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
@RunWith(MockitoJUnitRunner.class)
public class SolrSearchContextFactoryTest {

	@Mock SolrServer server;

	@Test
	public void createSearchContext_returnsNewInstanceOfSolrSearchContext() {
		SearchContext context = SolrSearchContextFactory.createSearchContext(server, null);
		assertEquals(SolrSearchContext.class, context.getClass());
	}
}
