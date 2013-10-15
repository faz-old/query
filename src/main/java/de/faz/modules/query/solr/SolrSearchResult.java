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
package de.faz.modules.query.solr;

import de.faz.modules.query.SearchContext;
import de.faz.modules.query.fields.FieldDefinitionGenerator;
import de.faz.modules.query.fields.Mapping;
import de.faz.modules.query.solr.internal.SolrResponseCallbackFactory;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.util.Iterator;
import java.util.NoSuchElementException;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
class SolrSearchResult extends SearchContext.SearchResult {

	private SolrResponseCallbackFactory callbackFactory;
	private FieldDefinitionGenerator fieldGenerator;

	private final QueryResponse response;


	SolrSearchResult(final QueryResponse result, final int pageSize) {
		super(pageSize);
		response = result;
	}

	SolrSearchResult(final FieldDefinitionGenerator generator, final QueryResponse result, final int pageSize, final int offset, final SolrResponseCallbackFactory factory) {
		super(pageSize, offset);
		this.callbackFactory = factory;
		this.fieldGenerator = generator;
		this.response = result;
	}

	public long getNumCount() {
		long numCount = 0;
		if(response != null) {
			numCount = response.getResults().getNumFound();
		}

		return numCount;
	}

	public long getNumberOfPages() {
		int numPages = 0;
		if(response != null) {
			numPages = (int) Math.ceil(response.getResults().getNumFound() / (double)pageSize);
		}
		return numPages;
	}

	@Override
	public <S extends Mapping> Iterator<S> getResultsForMapping(final Class<S> mapping) {
		if(response != null) {
			return createIteratorFromDocumentList(response, mapping, response.getResults());
		} else {
			return createDefaultIterator();
		}
	}

	protected <S extends Mapping> Iterator<S> createIteratorFromDocumentList(final QueryResponse response, final Class<S> mappingClass, final SolrDocumentList list) {
		final Iterator<SolrDocument> solrIt = list.iterator();
		return new Iterator<S>() {
			@Override
			public boolean hasNext() {
				return solrIt.hasNext();
			}

			@Override
			public S next() {
				final SolrDocument doc = solrIt.next();
				S result = fieldGenerator.enhanceWithInterceptor(mappingClass, callbackFactory.createCallbackForDocument(response, doc));
				return result;
			}

			@Override
			public void remove() {
				//do not enable feature to remove an entry from result
			}
		};
	}

	protected <S> Iterator<S> createDefaultIterator() {
		return new Iterator<S>() {
			@Override
			public boolean hasNext() {
				return false;
			}

			@Override
			public S next() {
				throw new NoSuchElementException();
			}

			@Override
			public void remove() {
				//there are no result so removing makes no sense
			}
		};
	}

	protected QueryResponse getSolrResponse() {
		return response;
	}
}