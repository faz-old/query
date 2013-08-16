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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.faz.modules.query.solr.SolrEnrichQueryExecutor;
import de.faz.modules.query.solr.SolrResponseCallbackFactory;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class SearchHighlighter implements SearchOption,SolrResponseCallbackFactory {

	private final FieldDefinitionGenerator generator;
	private final List<FieldDefinition> definitionList;

	private CharSequence prefix;
	private CharSequence postfix;


	SearchHighlighter(final FieldDefinitionGenerator generator) {
		this.generator = generator;
		this.prefix = "";
		this.postfix = "";
		definitionList = new ArrayList<>();
	}

	public SearchHighlighter withField(final Object fieldDefinition) {
		if(generator.isEmpty()) {
			throw new IllegalArgumentException("The field definition of method 'withField' was null.");
		}

		definitionList.add(generator.pop());
		return this;
	}

	public SearchHighlighter surroundWith(final CharSequence prefix, final CharSequence postfix) {
		if(prefix == null) { throw new IllegalArgumentException("A prefix is required for custom highlighting."); }
		if(postfix == null) { throw new IllegalArgumentException("A postfix is required for custom highlighting."); }

		this.prefix = prefix;
		this.postfix = postfix;
		return this;
	}

	List<FieldDefinition> getFields() {
		return Collections.unmodifiableList(definitionList);
	}

	CharSequence getPrefix() {
		return prefix;
	}

	CharSequence getPostfix() {
		return postfix;
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

				for(final FieldDefinition definition : definitionList) {
					query.addHighlightField(definition.name.toString());
				}
			}
		};
	}

	@Override
	//TODO: Change it to use new query framework
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
