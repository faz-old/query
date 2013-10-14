package de.faz.modules.query;

import javax.annotation.Nonnull;

import static org.mockito.Mockito.mock;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class MockedSearchContext extends DefaultSearchContext {

	public MockedSearchContext() {
		this(mock(QueryExecutor.class));
	}

	public MockedSearchContext(final QueryExecutor executor) {
		super(executor);
	}

	public MockedSearchContext(final QueryExecutor executor, final FieldDefinitionGenerator generator) {
		super(executor, generator);
	}

	@Nonnull
	@Override
	public Query createQuery() {
		return new Query(generator);
	}

	@Nonnull
	@Override
	public SearchSettings withSettings() {
		return new SearchSettings(generator);
	}
}
