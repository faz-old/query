package de.faz.modules.query.capabilities;

import de.faz.modules.query.exception.UnsupportedFeatureException;

import javax.annotation.Nonnull;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public interface ContextCapabilities {
	boolean hasSupportFor(@Nonnull Class<? extends FeatureSupport> feature);

	@Nonnull
	<T extends SearchOption> SearchOptionFactory getSearchOptionFactoryFor(@Nonnull Class<? extends FeatureSupport> featureClass) throws UnsupportedFeatureException;
}
