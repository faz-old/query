package de.faz.modules.query.capabilities;

import de.faz.modules.query.exception.UnsupportedFeatureException;

import javax.annotation.Nonnull;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class DefaultContextCapabilities implements ContextCapabilities {
	@Override
	public boolean hasSupportFor(@Nonnull final Class<? extends FeatureSupport> feature) {
		return false;
	}

	@Nonnull
	@Override
	public <T extends SearchOption> SearchOptionFactory<T> getSearchOptionFactoryFor(@Nonnull final Class<? extends FeatureSupport> featureClass) throws UnsupportedFeatureException {
		throw new UnsupportedFeatureException("this feature is not supported. Use a correct SearchEngine implementation.");
	}
}
