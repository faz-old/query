package de.faz.modules.query.solr.capabilities;

import de.faz.modules.query.EnrichQueryExecutor;
import de.faz.modules.query.FieldDefinitionGenerator;
import de.faz.modules.query.MapToField;
import de.faz.modules.query.SearchHighlighter;
import de.faz.modules.query.solr.internal.SolrResponseCallbackFactory;
import de.faz.modules.query.solr.internal.SolrEnrichQueryExecutor;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.HighlightParams;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class SolrSearchHighlighter extends SearchHighlighter implements SolrResponseCallbackFactory {

	public SolrSearchHighlighter(final FieldDefinitionGenerator generator) {
		super(generator);
	}

	@Override
	public EnrichQueryExecutor getQueryExecutor() {
		return new SolrEnrichQueryExecutor() {
			@Override
			public void enrich(final SolrQuery query) {
				query.setHighlight(true);
				query.setHighlightFragsize(Integer.MAX_VALUE);
				query.setHighlightSimplePre(prefix.toString());
				query.setHighlightSimplePost(postfix.toString());

				for(final FieldDefinitionGenerator.FieldDefinition definition : definitionList) {
					query.addHighlightField(definition.getName().toString());
				}

				if(highlightingQuery != null) {
					query.setParam(HighlightParams.Q, highlightingQuery.toString());
				}
			}
		};
	}

	@Override
	public Callback createCallbackForDocument(final QueryResponse response, final SolrDocument document) {
		return new MethodInterceptor() {
			@Override
			public Object intercept(final Object o, final Method method, final Object[] objects, final MethodProxy methodProxy) throws Throwable {
				final MapToField mapping = method.getAnnotation(MapToField.class);
				final String docId = (String)document.getFieldValue("contentId");
				if(mapping != null) {
					final Map<String, List<String>> highlightingResult = response.getHighlighting().get(docId);
					if(hasHighlightingForField(mapping.value(), highlightingResult)) {
						return highlightingResult.get(mapping.value()).get(0);
					} else {
						return document.getFieldValue(mapping.value());
					}
				}

				return null;
			}
		};
	}

	private boolean hasHighlightingForField(final String fieldName, final Map<String, List<String>> highlightingMap) {
		return highlightingMap != null && highlightingMap.get(fieldName) != null && highlightingMap.get(fieldName).size() > 0;
	}
}
