package de.faz.modules.query.polopoly;

import com.polopoly.application.Application;
import com.polopoly.search.solr.SolrClientImpl;
import com.polopoly.search.solr.SolrSearchClient;
import de.faz.modules.query.solr.SolrSearchContextFactory;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
	}}
