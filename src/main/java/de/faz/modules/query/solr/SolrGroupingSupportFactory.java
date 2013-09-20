package de.faz.modules.query.solr;

import de.faz.modules.query.FieldDefinitionGenerator;
import de.faz.modules.query.GroupingSearchOption;
import de.faz.modules.query.capabilities.SearchOptionFactory;

import javax.annotation.Nonnull;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class SolrGroupingSupportFactory implements SearchOptionFactory<GroupingSearchOption> {

	@Nonnull
	@Override
	public GroupingSearchOption createInstance(@Nonnull final FieldDefinitionGenerator generator) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}
}
