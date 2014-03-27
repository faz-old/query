package de.faz.modules.query.solr;

import de.faz.modules.query.SearchDecorator;
import de.faz.modules.query.fields.FieldDefinitionGenerator;
import de.faz.modules.query.Query;
import de.faz.modules.query.QueryExecutor;
import de.faz.modules.query.SearchSettings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

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

	@Test
	public void withSettings_createSettingsWithDefaultRows() {
		SearchSettings settings = underTest.withSettings();
		assertEquals(SolrSearchContext.DEFAULT_ROWS, settings.getPageSize());
	}

	@Test
	public void withSettings_returnsNewInstanceOfSolrSearchSettings() {
		SearchSettings settings = underTest.withSettings();
		assertEquals(SolrSearchSettings.class, settings.getClass());
	}
}
