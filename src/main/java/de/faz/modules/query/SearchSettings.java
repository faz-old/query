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
import de.faz.modules.query.solr.SolrEnrichQueryExecutor;
import de.faz.modules.query.solr.SolrResponseCallbackFactory;
import de.faz.modules.query.solr.StandardCallbackFactory;
import org.apache.solr.client.solrj.SolrQuery;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class SearchSettings implements SearchOption {

    public static final int DEFAULT_ROWS = 10;
    private static final int DEFAULT_OFFSET = 0;

	public enum Order {
        ASC,
        DESC;
	}

    private Collection<SearchOption> optionCollection;
    private Collection<SortBy> sort;
    private Optional<SolrResponseCallbackFactory> customCallbackFactory = Optional.absent();
	private Optional<Integer> pageSize = Optional.absent();
	private Optional<Integer> offset = Optional.absent();
    private List<Query> filterList;
	private Map<String, Object> parameterMap;

    protected FieldDefinitionGenerator generator;

    SearchSettings(FieldDefinitionGenerator generator) {
	    sort = new ArrayList<>();
	    filterList = new ArrayList<>();
	    optionCollection = new ArrayList<>();
	    parameterMap = new HashMap<>();
	    this.generator = generator;
    }

    public SearchSettings withPageSize(int size) {
        this.pageSize = Optional.of(size);
        return this;
    }

    public SearchSettings startAt(int offset) {
        this.offset = Optional.of(offset);
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

    public SearchSettings filterBy(@Nonnull Query filter) {
        filterList.add(filter);
        return this;
    }

    public SearchHighlighter addHighlighting() {
        SearchHighlighter highlighter = new SearchHighlighter(generator);
        optionCollection.add(highlighter);
        customCallbackFactory = Optional.<SolrResponseCallbackFactory>of(highlighter);

        return highlighter;
    }

	public GroupingSearchOption addGrouping() {
		GroupingSearchOption option = new GroupingSearchOption(generator);
		optionCollection.add(option);
		return option;
	}

	Collection<SearchOption> getOptions() {
		return optionCollection;
	}

    public Collection<SortBy> getSort() {
        return sort;
    }

    public SolrResponseCallbackFactory getCustomCallbackFactory() {
        return customCallbackFactory.or(new StandardCallbackFactory());
    }

	public SearchSettings addParameter(final String key, final Object value) {
		parameterMap.put(key, value);
		return this;
	}

	public <T> Optional<T> getParameter(final String key) {
		return (Optional<T>) Optional.fromNullable(parameterMap.get(key));
	}

	public int getPageSize() {
		return pageSize.or(DEFAULT_ROWS);
	}

	public Optional<Integer> getOffset() {
		return offset;
	}

	void enrichQuery(SolrQuery query) {
        query.setStart(offset.or(DEFAULT_OFFSET));
        query.setRows(pageSize.or(DEFAULT_ROWS));
        Collection<SearchSettings.SortBy> sortCollection = getSort();
        for(SearchSettings.SortBy sortBy : sortCollection) {
            query.addSortField(sortBy.getFieldName().toString(), sortBy.getSolrOrder());
        }

        for(SearchOption option : optionCollection) {
            option.getQueryExecutor().enrich(query);
        }

        for(Query filter : filterList) {
            query.addFilterQuery(filter.toString());
        }

    }
	static class SortBy {
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

        SolrQuery.ORDER getSolrOrder() {
            switch(order) {
	            case DESC:
                    return SolrQuery.ORDER.desc;
	            case ASC:
	            default:
		            return SolrQuery.ORDER.asc;
            }
        }

    }

    @Override
    public EnrichQueryExecutor getQueryExecutor() {
        return new SolrEnrichQueryExecutor() {
            @Override
            public void enrich(final SolrQuery query) {
                enrichQuery(query);
            }
        };
    }
}
