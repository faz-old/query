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

import de.faz.modules.query.decoration.SearchDecorator;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class DefaultSearchContext implements SearchContext {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultSearchContext.class);

	private static final int DEFAULT_ROWS = 10;
	private final FieldDefinitionGenerator generator;
    private final QueryExecutor executor;

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

	@Override
    public Query createQuery() {
        return new Query(generator);
    }

    @Override
	public Query createQuery(final Query.Operator operator) {
        return new QueryDefaultComparator(createQuery(), operator);
    }

    @Override
    public PreparedQuery createPreparedQuery() {
        return new PreparedQuery(generator);
    }

    @Override
    public <T extends Mapping> T createFieldDefinitionFor(final Class<T> mappingClass) {
        if(mappingClass == null) {
            throw new IllegalArgumentException("a mapping class is required to create a field definition object.");
        }
        return generator.createFieldDefinition(mappingClass);
    }

    @Override
	public SearchResult execute(final Query query) {
        if(query == null) {
            throw new IllegalArgumentException("A Query instance is required.");
        }
		LOG.warn("No Pagesize has been set. Default value is set to {}.", DEFAULT_ROWS);
        return executor.execute(query, withSettings());
    }

    @Override
	public SearchResult execute(final Query query, final SearchSettings settings) {
	    Query decoratedQuery = query;
	    SearchSettings decoratedSettings = settings;
	    for(final SearchDecorator decorator : decoratorList) {
		    decoratedQuery = decorator.decorateQuery(decoratedQuery);
		    decoratedSettings = decorator.decorateSettings(decoratedSettings);
	    }
        return executor.execute(query, settings);
    }

    @Override
	public SearchSettings withSettings() {
        return createDefaultSettings();
    }

	private SearchSettings createDefaultSettings() {
		return new SearchSettings(generator).withPageSize(DEFAULT_ROWS).startAt(0);
	}

}
