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

import com.google.common.base.Optional;
import de.faz.modules.query.fields.Mapping;

import javax.annotation.Nonnull;
import java.util.Iterator;

/**
 * This interface provides all functionality to create and
 * execute queries with the new query framework.
 * With an implementation of this interface you can create
 * field definition classes that do the field magic behind this
 * framework.
 * You must call {@link SearchContext#createFieldDefinitionFor(Class)}
 * first to use all functionalities of this framework. When you
 * don't use this function you'll get exceptions.
 * You can also create {@link SearchSettings} which provide some extra
 * functionality in this framework.
 *
 * @author Andreas Kaubisch <a.kaubisch@faz.de>
 */
public interface SearchContext {

	/**
	 * This function adds a decorator to the created context that can add
	 * additional functionality to a {@link Query} or {@link SearchSettings}
	 * object when you call execute.
	 *
	 * @param decorator
	 */
	void addSearchDecorator(@Nonnull SearchDecorator decorator);

    /**
     * This function creates a new Query instance an return this.
     * You must this factorial function because the SearchContext
     * initializes the {@link Query} instance with some required
     * objects.
     *
     * @return a new {@link Query} instance
     */
    @Nonnull Query createQuery();

    /**
     * This function works like {@link de.faz.modules.query.SearchContext#createQuery()}
     * but it will initialize the {@link Query} instance to combine
     * all query items with a specific operator
     *
     * @param operator a {@link de.faz.modules.query.Query.Operator} which is used
     *                 to combine all query items
     * @return a new {@link Query} instance
     */
    @Nonnull Query createQuery(@Nonnull Query.Operator operator);

    /**
     * This function creates a new {@link PreparedQuery} instance that
     * provides some extra function to specify some value placeholders
     * at runtime that can be replaced with some real values.
     *
     * @return a new {@link PreparedQuery} instance
     */
    @Nonnull PreparedQuery createPreparedQuery();

    /**
     * This function is one of the primary function you will use when you
     * work with this framework. It generates a new instance of a given
     * {@link de.faz.modules.query.fields.Mapping} class and decorates this instance that the framework
     * get the field definition from this {@link de.faz.modules.query.fields.Mapping} instead of any
     * values defined in the getter methods.
     * You need to annotate that function with {@link de.faz.modules.query.fields.MapToField} annotation
     * that this function works correctly.
     *
     * @param mappingClass a class that implement {@link de.faz.modules.query.fields.Mapping}
     * @param <T> a class that implements {@link de.faz.modules.query.fields.Mapping}
     * @return  a new class of type T that is decorated to be used
     *          in other framework classes.
     */
    @Nonnull <T extends Mapping> T createFieldDefinitionFor(@Nonnull Class<T> mappingClass);

    /**
     * This function takes a {@link Query} instance an call a execute this
     * query with a remote service.
     * You don't need to specify a {@link SearchSettings} object because
     * this function uses a standard {@link SearchSettings} instance with
     * default values.
     *
     * Beware of that function because this function will be do a expensive
     * call to the backend. Please make sure that you call this function once.
     *
     * @param query a {@link Query} instance
     * @return a new {@link SearchResult} that contains the search results
     */
    @Nonnull SearchResult execute(@Nonnull Query query);

    /**
     * This function works similar to {@link SearchContext#execute(Query)}
     * expect that you can handover your own {@link SearchSettings}.
     * When you need some sorting oder paging you must use this function.
     *
     * Beware of that function because this function will be do a expensive
     * call to the backend. Please make sure that you call this function once.
     *
     * @param query a {@link Query} instance
     * @param settings a custom {@link SearchSettings} instance
     * @return a new {@link SearchResult} that contains the search results
     */
    @Nonnull SearchResult execute(@Nonnull Query query, @Nonnull SearchSettings settings);

    /**
     * This function creates a new instance of {@link SearchSettings}.
     * Use this function when you want to create a custom
     * {@link SearchSettings} instance with your own configurations.
     * Use always this creatio method to create new instances of
     * {@link SearchSettings} because the context will be initialize
     * this new created instance to work properly with this framework.
     *
     * @return a new {@link SearchSettings} instance
     */
    @Nonnull SearchSettings withSettings();

    abstract class SearchResult<T> {
        protected Optional<T> implementedSearchResult;

        protected int pageSize;
        protected int offset;


        public SearchResult(T result, int pageSize) {
            this(result, pageSize, 0);
        }

        public SearchResult(T result, int pageSize, int offset) {
            this.implementedSearchResult = Optional.fromNullable(result);
            this.pageSize = pageSize;
            this.offset = offset;
        }

	    protected SearchResult() {}

	    public int getPageSize() {
            return pageSize;
        }

        public int getOffset() {
            return offset;
        }

        public abstract <S extends Mapping> Iterator<S> getResultsForMapping(Class<S> mapping);
        public abstract long getNumCount();
        public abstract long getNumberOfPages();
    }
}