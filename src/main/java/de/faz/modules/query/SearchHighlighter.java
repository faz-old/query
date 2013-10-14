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

import de.faz.modules.query.capabilities.EnrichQueryExecutor;
import de.faz.modules.query.capabilities.SearchOption;
import de.faz.modules.query.fields.FieldDefinitionGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class SearchHighlighter implements SearchOption {

	private final FieldDefinitionGenerator generator;
	protected final List<FieldDefinitionGenerator.FieldDefinition> definitionList;

	protected CharSequence prefix;
	protected CharSequence postfix;

	protected Query highlightingQuery;


	public SearchHighlighter(final FieldDefinitionGenerator generator) {
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

	public SearchHighlighter withQuery(final Query highlightingQuery) {
		this.highlightingQuery = highlightingQuery;
		return this;
	}

	List<FieldDefinitionGenerator.FieldDefinition> getFields() {
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
		return new EnrichQueryExecutor() {
			@Override
			public void enrich(final Object query) {
				//do nothing
			}
		};
	}


}
