/*
 * Copyright (c) 2013. F.A.Z. Electronic Media GmbH
 * All Rights Reserved.
 * 
 * NOTICE: All information contained herein is, and remains
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
import de.faz.modules.query.capabilities.ContextCapabilities;
import de.faz.modules.query.capabilities.DefaultContextCapabilities;
import de.faz.modules.query.capabilities.GroupingSupport;
import de.faz.modules.query.capabilities.HighlightingSupport;
import de.faz.modules.query.capabilities.SearchOption;
import de.faz.modules.query.capabilities.SearchOptionFactory;
import de.faz.modules.query.exception.InvalidQueryException;
import de.faz.modules.query.exception.UnsupportedFeatureException;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class SearchSettings implements SearchOption {

	public static final int DEFAULT_ROWS = 10;

	public enum Order {
		ASC, DESC;
	}

	protected Collection<SearchOption> optionCollection;

	private Collection<SortBy> sort;
	protected Optional<Integer> pageSize = Optional.absent();
	protected Optional<Integer> offset = Optional.absent();
	protected List<Query> filterList;
	protected Collection<String> fieldList;
	private Map<String, Object> parameterMap;

	protected FieldDefinitionGenerator generator;

	private final ContextCapabilities capabilities;

	{
		sort = new ArrayList<>();
		filterList = new ArrayList<>();
		optionCollection = new ArrayList<>();
		parameterMap = new HashMap<>();
		fieldList = new ArrayList<>();
	}

	public SearchSettings(final FieldDefinitionGenerator generator, final ContextCapabilities capabilities) {
		this.capabilities = capabilities;
		this.generator = generator;
	}

	SearchSettings(final FieldDefinitionGenerator generator) {
		this(generator, new DefaultContextCapabilities());
	}

	public SearchSettings withPageSize(final int size) {
		this.pageSize = Optional.of(size);
		return this;
	}

	public SearchSettings startAt(final int offset) {
		this.offset = Optional.of(offset);
		return this;
	}

	public SearchSettings sortBy(final Object fieldDefinition, final Order order) {
		if (generator.isEmpty()) {
			throw new InvalidQueryException("The field description of sortBy was null.");
		}
		FieldDefinitionGenerator.FieldDefinition definition = generator.pop();
		this.sort.add(new SortBy(definition.name, order));
		return this;
	}

	public SearchSettings filterBy(@Nonnull final Query filter) {
		filterList.add(filter);
		return this;
	}

	public SearchHighlighter addHighlighting() {
		if(!capabilities.hasSupportFor(HighlightingSupport.class)) {
			throw new UnsupportedFeatureException("highlighting is not supported with the selected SearchEngine.");
		}
		SearchOptionFactory<SearchHighlighter> optionFactory = capabilities.getSearchOptionFactoryFor(HighlightingSupport.class);
		SearchHighlighter highlighter = optionFactory.createInstance(generator);
		optionCollection.add(highlighter);
		return highlighter;
	}

	public GroupingSearchOption addGrouping() {
		if(!capabilities.hasSupportFor(GroupingSupport.class)) {
			throw new UnsupportedFeatureException("grouping is not supported with the selected SearchEngine.");
		}

		SearchOptionFactory<GroupingSearchOption> optionFactory = capabilities.getSearchOptionFactoryFor(GroupingSupport.class);
		GroupingSearchOption option = optionFactory.createInstance(generator);
		optionCollection.add(option);
		return option;
	}

	Collection<SearchOption> getOptions() {
		return optionCollection;
	}

	public Collection<SortBy> getSort() {
		return sort;
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

	public SearchSettings restrictByField(final Object fieldDefinition) {
		if (!generator.isEmpty()) {
			fieldList.add(generator.pop().getName().toString());
		}
		return this;
	}

	protected static class SortBy {
		private CharSequence fieldName;

		private Order order;

		SortBy(final CharSequence fieldName, final Order order) {
			this.fieldName = fieldName;
			this.order = order;
		}

		public CharSequence getFieldName() {
			return fieldName;
		}

		public Order getOrder() {
			return order;
		}
	}

	@Override
	public EnrichQueryExecutor getQueryExecutor() {
		return new EnrichQueryExecutor() {
			@Override
			public void enrich(final Object query) {
				//DO NOTHING
			}
		};
	}
}
