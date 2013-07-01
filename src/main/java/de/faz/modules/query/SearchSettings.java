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

import java.util.ArrayList;
import java.util.Collection;

import org.apache.solr.client.solrj.SolrQuery;

import com.google.common.base.Optional;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class SearchSettings implements SearchOption {

    public enum Order {
        ASC,
        DESC
    }

    private Collection<SortBy> sort;

    private int pageSize;
    private Integer offset;

    private Collection<SearchOption> optionCollection;
    private SolrResponseCallbackFactory customCallbackFactory;

    protected FieldDefinitionGenerator generator;

    SearchSettings(FieldDefinitionGenerator generator) {
        sort = new ArrayList<>();
        this.generator = generator;
        optionCollection = new ArrayList<>();
    }

    public SearchSettings withPageSize(int size) {
        this.pageSize = size;
        return this;
    }

    public SearchSettings startAt(int offset) {
        this.offset = offset;
        return this;
    }

    public SearchSettings sortBy(Object fieldDefinition, Order order) {
        if(generator.isEmpty()) {
            throw new InvalidQueryException("The field description of sortBy was null.");
        }

        FieldDefinition definition = generator.pop();
        this.sort.add(new SortBy(definition.name, order));
        return this;
    }

    public SearchHighlighter addHighlighting() {
        SearchHighlighter highlighter = new SearchHighlighter(generator);
        optionCollection.add(highlighter);
        customCallbackFactory = highlighter;

        return highlighter;
    }

    public Collection<SortBy> getSort() {
        return sort;
    }

    public int getPageSize() {
        return pageSize;
    }

    public Optional<Integer> getOffset() {
        return Optional.fromNullable(offset);
    }

    public Optional<SolrResponseCallbackFactory> getCustomCallbackFactory() {
        return Optional.fromNullable(customCallbackFactory);
    }

    class SortBy {
        private CharSequence fieldName;
        private Order order;

        SortBy(final CharSequence fieldName, final Order order) {
            this.fieldName = fieldName;
            this.order = order;
        }

        CharSequence getFieldName() {
            return fieldName;
        }

        Order getOrder() {
            return order;
        }
    }

    @Override
    public EnrichQueryExecutor getQueryExecutor() {
        return new SolrEnrichQueryExecutor() {
            @Override
            public void enrich(final SolrQuery query) {
                query.setStart(getOffset().isPresent() ? getOffset().get() : 0);
                Collection<SearchSettings.SortBy> sortCollection = getSort();
                for(SearchSettings.SortBy sort : sortCollection) {
                    query.setSortField(sort.getFieldName().toString(), sort.getOrder() == SearchSettings.Order.ASC ? SolrQuery.ORDER.asc : SolrQuery.ORDER.desc);
                }

                for(SearchOption option : optionCollection) {
                    option.getQueryExecutor().enrich(query);
                }
            }
        };
    }
}
