package de.faz.modules.query.solr;

import com.google.common.base.Optional;
import de.faz.modules.query.EnrichQueryExecutor;
import de.faz.modules.query.FieldDefinitionGenerator;
import de.faz.modules.query.Query;
import de.faz.modules.query.SearchHighlighter;
import de.faz.modules.query.SearchSettings;
import de.faz.modules.query.capabilities.SearchOption;
import de.faz.modules.query.solr.internal.SolrEnrichQueryExecutor;
import de.faz.modules.query.solr.internal.SolrResponseCallbackFactory;
import org.apache.solr.client.solrj.SolrQuery;

import java.util.Collection;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class SolrSearchSettings extends SearchSettings {

	private static final int DEFAULT_OFFSET = 0;

	private Optional<SolrResponseCallbackFactory> customCallbackFactory = Optional.absent();


	public SolrSearchSettings(final FieldDefinitionGenerator generator) {
		super(generator, new SolrContextCapabilities());
	}

	@Override
	public EnrichQueryExecutor getQueryExecutor() {
		return new SolrEnrichQueryExecutor() {
			@Override
			public void enrich(final org.apache.solr.client.solrj.SolrQuery query) {
				enrichQuery(query);
			}
		};
	}

	@Override
	public SearchHighlighter addHighlighting() {
		SearchHighlighter highlighter = super.addHighlighting();
		if(highlighter instanceof SolrResponseCallbackFactory) {
			customCallbackFactory = Optional.of((SolrResponseCallbackFactory) highlighter);
		}
		return highlighter;
	}

	public SolrResponseCallbackFactory getCustomCallbackFactory() {
		return customCallbackFactory.or(new StandardCallbackFactory());
	}

	void enrichQuery(final SolrQuery query) {
		query.setStart(offset.or(DEFAULT_OFFSET));
		query.setRows(pageSize.or(DEFAULT_ROWS));
		Collection<SearchSettings.SortBy> sortCollection = getSort();

		for (SearchSettings.SortBy sortBy : sortCollection) {
			query.addSortField(sortBy.getFieldName().toString(), new SolrOrderByMapper(sortBy).toSolrOrder());
		}

		for (SearchOption option : optionCollection) {
			option.getQueryExecutor().enrich(query);
		}

		for (Query filter : filterList) {
			query.addFilterQuery(filter.toString());
		}

		for (String field : fieldList) {
			query.addField(field);
		}

	}

	private class SolrOrderByMapper {
		private final SortBy sort;

		public SolrOrderByMapper(SortBy sort) {
			this.sort = sort;
		}

		public SolrQuery.ORDER toSolrOrder() {
			switch (sort.getOrder()) {
				case DESC:
					return SolrQuery.ORDER.desc;
				case ASC:
				default:
					return SolrQuery.ORDER.asc;
			}
		}
	}
}
