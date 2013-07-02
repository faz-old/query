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

import java.util.Iterator;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public interface SearchContext {

    Query createQuery();
    Query createQuery(Query.Operator operator);
    PreparedQuery createPreparedQuery();
    <T extends Mapping> T createFieldDefinitionFor(Class<T> mappingClass);
    SearchResult execute(Query query);
    SearchResult execute(Query query, SearchSettings settings);
    SearchSettings withSettings();

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

        protected Optional<T> getImplementationResult() {
            return implementedSearchResult;
        }

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
