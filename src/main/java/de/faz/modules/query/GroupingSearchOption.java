package de.faz.modules.query;

import de.faz.modules.query.solr.SolrEnrichQueryExecutor;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.params.GroupParams;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class GroupingSearchOption implements SearchOption {

	private final FieldDefinitionGenerator generator;

	private CharSequence fieldName;
	private Integer limit;
	private boolean merge;

	public GroupingSearchOption(FieldDefinitionGenerator generator) {
		this.generator = generator;
	}

	@Override
	public EnrichQueryExecutor getQueryExecutor() {
		return new SolrEnrichQueryExecutor() {
			@Override
			public void enrich(final SolrQuery query) {
				query.setParam(GroupParams.GROUP, true);
				query.setParam(GroupParams.GROUP_MAIN, true);
				if(StringUtils.isNotEmpty(fieldName)) {
					query.setParam(GroupParams.GROUP_FIELD, fieldName.toString());
				}

				if(limit != null) {
					query.setParam(GroupParams.GROUP_LIMIT, String.valueOf(limit));
				}
			}
		};
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

//	public GroupingSearchOption mergeGroupToResults() {
//		this.merge = true;
//		return this;
//	}
}
