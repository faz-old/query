package de.faz.modules.query.solr.capabilities;

import de.faz.modules.query.FieldDefinitionGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
@RunWith(MockitoJUnitRunner.class)
public class SolrGroupingSupportFactoryTest {
	@Mock FieldDefinitionGenerator generator;

	private SolrGroupingSupportFactory underTest;

	@Before
	public void setUp() {
		underTest = new SolrGroupingSupportFactory();
	}

	@Test
	public void createInstance_withGenerator_returnNewInstance() {
		assertNotNull(underTest.createInstance(generator));
	}

	@Test(expected = IllegalArgumentException.class)
	public void createInstance_withoutGenerator_throwsIllegalArgumentException() {
		underTest.createInstance(null);
	}
}
