package de.faz.modules.query;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class MockedSearchContext extends DefaultSearchContext {
	public MockedSearchContext(final QueryExecutor executor) {
		super(executor);
	}

	public MockedSearchContext(final QueryExecutor executor, final FieldDefinitionGenerator generator) {
		super(executor, generator);
	}
}
