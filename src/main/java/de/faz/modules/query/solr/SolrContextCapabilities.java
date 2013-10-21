package de.faz.modules.query.solr;

import de.faz.modules.query.capabilities.ContextCapabilities;
import de.faz.modules.query.capabilities.FeatureSupport;
import de.faz.modules.query.capabilities.GroupingSupport;
import de.faz.modules.query.capabilities.HighlightingSupport;
import de.faz.modules.query.capabilities.SearchOption;
import de.faz.modules.query.capabilities.SearchOptionFactory;
import de.faz.modules.query.exception.UnsupportedFeatureException;
import de.faz.modules.query.solr.capabilities.SolrGroupingSupportFactory;
import de.faz.modules.query.solr.capabilities.SolrHighlightingSupportFactory;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class SolrContextCapabilities implements ContextCapabilities {

	private Map<Class<? extends FeatureSupport>, SearchOptionFactory> supportMap;

	public SolrContextCapabilities() {
		supportMap = new HashMap<>();
		supportMap.put(GroupingSupport.class, new SolrGroupingSupportFactory());
		supportMap.put(HighlightingSupport.class, new SolrHighlightingSupportFactory());
	}

	@Override
	public boolean hasSupportFor(@Nonnull final Class<? extends FeatureSupport> feature) {
		return supportMap.keySet().contains(feature);
	}

	@Nonnull
	@Override
	public <T extends SearchOption> SearchOptionFactory<T> getSearchOptionFactoryFor(@Nonnull final Class<? extends FeatureSupport> feature) throws UnsupportedFeatureException {
		if(!hasSupportFor(feature)) {
			throw new UnsupportedFeatureException("This Context has no support for feature "+ feature.getName());
		}
		return (SearchOptionFactory<T>)supportMap.get(feature);
	}
}
