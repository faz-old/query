package de.faz.modules.query.solr;

import de.faz.modules.query.FieldDefinitionGenerator;
import de.faz.modules.query.Query;
import de.faz.modules.query.QueryExecutor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
@RunWith(MockitoJUnitRunner.class)
public class SolrSearchContextTest {
	@Mock FieldDefinitionGenerator generator;
	@Mock QueryExecutor executor;

	private SolrSearchContext underTest;

	@Before
	public void setUp() {
		underTest = new SolrSearchContext(executor, generator);
	}

	@Test
	public void createQuery_returnsNewInstanceOfSolrQuery() {
		Query q = underTest.createQuery();
		assertEquals(SolrQuery.class, q.getClass());
	}
}
