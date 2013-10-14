package de.faz.modules.query.solr.capabilities;

import de.faz.modules.query.GroupingSearchOption;
import de.faz.modules.query.Query;
import de.faz.modules.query.capabilities.EnrichQueryExecutor;
import de.faz.modules.query.fields.FieldDefinitionGenerator;
import de.faz.modules.query.solr.internal.SolrEnrichQueryExecutor;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.params.GroupParams;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class SolrGroupingSearchOption extends GroupingSearchOption {

	public SolrGroupingSearchOption(final FieldDefinitionGenerator generator) {
		super(generator);
	}

	@Override
	public EnrichQueryExecutor getQueryExecutor() {
		return new SolrEnrichQueryExecutor() {
			@Override
			public void enrich(final SolrQuery query) {
				CharSequence fieldName = getFieldName();
				query.setParam(GroupParams.GROUP, true);
				query.setParam(GroupParams.GROUP_MAIN, isMerge());
				if(StringUtils.isNotEmpty(fieldName)) {
					query.setParam(GroupParams.GROUP_FIELD, fieldName.toString());
				}

				if(getLimit() != null) {
					query.setParam(GroupParams.GROUP_LIMIT, String.valueOf(getLimit()));
				}

				for(Query groupQuery : getGroupQueries()) {
					query.add(GroupParams.GROUP_QUERY, groupQuery.toString());
				}
			}
		};
	}
}
