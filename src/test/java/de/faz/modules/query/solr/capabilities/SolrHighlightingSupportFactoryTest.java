package de.faz.modules.query.solr.capabilities;

import de.faz.modules.query.FieldDefinitionGenerator;
import de.faz.modules.query.SearchHighlighter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
@RunWith(MockitoJUnitRunner.class)
public class SolrHighlightingSupportFactoryTest {

	@Mock FieldDefinitionGenerator generator;

	private SolrHighlightingSupportFactory underTest;

	@Before
	public void setUp() {
		underTest = new SolrHighlightingSupportFactory();
	}

	@Test
	public void createInstance_returnsNewSolrSearchHighlighterInstance() {
		SearchHighlighter highlighter = underTest.createInstance(generator);
		assertEquals(SolrSearchHighlighter.class, highlighter.getClass());
	}
}
