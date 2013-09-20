package de.faz.modules.query;

import de.faz.modules.query.capabilities.SearchOption;

import java.util.ArrayList;
import java.util.List;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public abstract class GroupingSearchOption implements SearchOption {

	private final FieldDefinitionGenerator generator;

	private CharSequence fieldName;
	private Integer limit;
	private final List<Query> groupQueries;

	private boolean merge;

	public GroupingSearchOption(FieldDefinitionGenerator generator) {
		this.generator = generator;
		groupQueries = new ArrayList<>();
	}

	public GroupingSearchOption groupByField(final Object fieldDefinition) {
		if(generator.isEmpty()) { throw new IllegalArgumentException("a field definition is required to group by fieldname."); }

		FieldDefinition definition = generator.pop();
		fieldName = definition.getName();
		return this;
	}

	public GroupingSearchOption limitGroupResultsTo(final int limit) {
		if(limit <= 0) { throw new IllegalArgumentException("a positive limit value is required."); }

		this.limit = limit;
		return this;
	}

	public GroupingSearchOption groupBy(final Query groupQuery) {
		if(groupQuery == null) { throw new IllegalArgumentException("a group query is required when you want to group with a query."); }

		groupQueries.add(groupQuery);
		return this;
	}

	public GroupingSearchOption mergeResults() {
		merge = true;
		return this;
	}

	public CharSequence getFieldName() {
		return fieldName;
	}

	public Integer getLimit() {
		return limit;
	}

	public List<Query> getGroupQueries() {
		return groupQueries;
	}

	public boolean isMerge() {
		return merge;
	}
}
