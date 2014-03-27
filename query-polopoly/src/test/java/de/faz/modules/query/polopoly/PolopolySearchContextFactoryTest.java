package de.faz.modules.query.polopoly;

import com.polopoly.application.Application;
import com.polopoly.search.solr.QueryDecorator;
import com.polopoly.search.solr.SolrClient;
import com.polopoly.search.solr.SolrClientImpl;
import com.polopoly.search.solr.SolrSearchClient;
import com.sun.jersey.core.util.Closing;
import de.faz.modules.query.Query;
import de.faz.modules.query.SearchContext;
import de.faz.modules.query.SearchDecorator;
import de.faz.modules.query.SearchSettings;
import de.faz.modules.query.solr.SolrSearchContextFactory;
import junit.framework.TestCase;
import org.apache.solr.client.solrj.SolrQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

/**
 * @author Andreas Kaubisch <a.kaubisch@faz.de>
 * @since $rev$
 */
@RunWith(MockitoJUnitRunner.class)
public class PolopolySearchContextFactoryTest extends TestCase {
	@Mock
	Application application;
	@Mock
	SolrSearchClient client;

	private PolopolySearchContextFactory underTest;

	@Before
	public void setUp() {
		when(application.getApplicationComponent(SolrSearchClient.DEFAULT_COMPOUND_NAME)).thenReturn(client);
		underTest = new PolopolySearchContextFactory(application);
	}

	@Test
	public void constructor_verifyThatSolrClientIsRetrieved() {
		verify(application).getApplicationComponent(SolrSearchClient.DEFAULT_COMPOUND_NAME);
	}

	@Test
	public void createContext_createsNewSearchContext() {
		SolrClientImpl polopolySolrClient = mock(SolrClientImpl.class);
		when(client.getServiceControl()).thenReturn(polopolySolrClient);
		assertNotNull(underTest.createContext());
	}

	@Test
	public void createContext_withSolrSearchClientHasDecorators_returnContextWithDecorators() {
		SolrClientImpl polopolySolrClient = mock(SolrClientImpl.class);
		when(client.getServiceControl()).thenReturn(polopolySolrClient);
		QueryDecorator queryDecorator = new TestSearchDecorator(null);
		when(polopolySolrClient.getQueryDecorators()).thenReturn(Arrays.asList(queryDecorator));
		SearchContext context = underTest.createContext(client);
		assertEquals(2, context.getSearchDecorators().size());

		List<Class<SearchDecorator>> decoratorClasses = new ArrayList<>();
		for (SearchDecorator searchDecorator : context.getSearchDecorators()) {
			decoratorClasses.add((Class<SearchDecorator>) searchDecorator.getClass());
		}
		assertTrue(decoratorClasses.contains(TestSearchDecorator.class));
	}
}

class TestSearchDecorator implements QueryDecorator, SearchDecorator {

	public TestSearchDecorator(final SearchContext context) {

	}

	@Override
	public SolrQuery decorate(final SolrQuery solrQuery) {
		return null;
	}

	@Nonnull
	@Override
	public Query decorateQuery(@Nonnull final Query q) {
		return null;
	}

	@Nonnull
	@Override
	public SearchSettings decorateSettings(@Nonnull final SearchSettings settings) {
		return null;
	}
}


