/*
 * Copyright (c) 2013. F.A.Z. Electronic Media GmbH
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of F.A.Z. Electronic Media GmbH and its suppliers,
 * if any. The intellectual and technical concepts contained
 * herein are proprietary to F.A.Z. Electronic Media GmbH
 * and its suppliers and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from F.A.Z. Electronic Media GmbH.
 */

package de.faz.modules.query;

import de.faz.modules.query.fields.FieldDefinitionGenerator;
import de.faz.modules.query.fields.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public abstract class DefaultSearchContext implements SearchContext {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultSearchContext.class);


	protected final FieldDefinitionGenerator generator;
	protected final QueryExecutor executor;

	private final List<SearchDecorator> decoratorList;


    public DefaultSearchContext(final QueryExecutor executor) {
        this(executor, new FieldDefinitionGenerator());
    }

    public DefaultSearchContext(final QueryExecutor executor, final FieldDefinitionGenerator generator) {
        this.generator = generator;
        this.executor = executor;
	    this.decoratorList = new ArrayList<>();
    }

	@Override
	public void addSearchDecorator(@Nonnull final SearchDecorator decorator) {
		this.decoratorList.add(decorator);
	}

	@Nonnull
    @Override
	public Query createQuery(@Nonnull final Query.Operator operator) {
        return new QueryDefaultComparator(createQuery(), operator);
    }

	@Nonnull
    @Override
    public PreparedQuery createPreparedQuery() {
        return new PreparedQuery(generator);
    }

	@Nonnull
    @Override
    public <T extends Mapping> T createFieldDefinitionFor(@Nonnull final Class<T> mappingClass) {
        if(mappingClass == null) {
            throw new IllegalArgumentException("a mapping class is required to create a field definition object.");
        }
        return generator.createFieldDefinition(mappingClass);
    }

	@Nonnull
    @Override
	public SearchResult execute(@Nonnull final Query query) {
        if(query == null) {
            throw new IllegalArgumentException("A Query instance is required.");
        }
		LOG.info("No Pagesize has been set.");
        return executor.execute(query, withSettings());
    }

	@Nonnull
    @Override
	public SearchResult execute(@Nonnull final Query query, @Nonnull final SearchSettings settings) {
	    Query decoratedQuery = query;
	    SearchSettings decoratedSettings = settings;
	    for(final SearchDecorator decorator : decoratorList) {
		    decoratedQuery = decorator.decorateQuery(decoratedQuery);
		    decoratedSettings = decorator.decorateSettings(decoratedSettings);
	    }
        return executor.execute(query, settings);
    }
}
