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

import de.faz.modules.query.SearchContext;

import javax.management.Query;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class DefaultSearchContext implements SearchContext {

    private FieldDefinitionGenerator generator;
    private QueryExecutor executor;


    public DefaultSearchContext(QueryExecutor executor) {
        this(executor, new FieldDefinitionGenerator());
    }

    DefaultSearchContext(QueryExecutor executor, FieldDefinitionGenerator generator) {
        this.generator = generator;
        this.executor = executor;

    }

    @Override
    public Query createQuery() {
        return new Query(generator);
    }

    @Override
    public PreparedQuery createPreparedQuery() {
        return new PreparedQuery(generator);
    }

    @Override
    public <T extends Mapping> T createFieldDefinitionFor(Class<T> mappingClass) {
        if(mappingClass == null) {
            throw new IllegalArgumentException("a mapping class is required to create a field definition object.");
        }
        return generator.createFieldDefinition(mappingClass);
    }

    public SearchResult execute(Query query) {
        if(query == null) {
            throw new IllegalArgumentException("A Query instance is required.");
        }
        return executor.execute(query, withSettings());
    }

    public SearchResult execute(Query query, SearchSettings settings) {
        return executor.execute(query, settings);
    }

    public SearchSettings withSettings() {
        return createDefaultSettings();
    }

    private SearchSettings createDefaultSettings() {
        return new SearchSettings(generator)
                .withPageSize(10)
                .startAt(0);
    }

}
