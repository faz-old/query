package de.faz.modules.query.capabilities;

import de.faz.modules.query.fields.FieldDefinitionGenerator;

import javax.annotation.Nonnull;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public interface SearchOptionFactory<T extends SearchOption> {
	@Nonnull
	T createInstance(@Nonnull FieldDefinitionGenerator generator);
}
