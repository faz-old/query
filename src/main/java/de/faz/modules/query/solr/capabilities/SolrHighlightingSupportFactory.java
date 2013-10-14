package de.faz.modules.query.solr.capabilities;

import de.faz.modules.query.FieldDefinitionGenerator;
import de.faz.modules.query.SearchHighlighter;
import de.faz.modules.query.capabilities.SearchOptionFactory;

import javax.annotation.Nonnull;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class SolrHighlightingSupportFactory implements SearchOptionFactory<SearchHighlighter> {

	@Nonnull
	@Override
	public SearchHighlighter createInstance(@Nonnull final FieldDefinitionGenerator generator) {
		return new SolrSearchHighlighter(generator);
	}
}
