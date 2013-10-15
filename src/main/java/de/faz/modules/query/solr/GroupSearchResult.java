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
import de.faz.modules.query.fields.Mapping;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.QueryResponse;

import java.util.Iterator;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class GroupSearchResult {

	private SolrSearchResult result;
	private QueryResponse response;

	public GroupSearchResult(final SearchContext.SearchResult result) {
		this.result = (SolrSearchResult) result;
		this.response = ((SolrSearchResult) result).getSolrResponse();
	}


	public <T extends Mapping> Iterator<T> getGroup(final int index, final Class<T> mappingClass) {
		Iterator<T> it = result.createDefaultIterator();
		if(getGroupCount() > index) {
			Group group = response.getGroupResponse().getValues().get(index).getValues().get(0);
			it = result.createIteratorFromDocumentList(response, mappingClass, group.getResult());
		}
		return it;
	}

	public int getGroupCount() {
		int count = 0;
		if(response != null) {
			count = response.getGroupResponse().getValues().size();
		}

		return count;
	}

	public long getResultCount(final int index) {
		long found = 0;
		if(getGroupCount() > index) {
			found = response.getGroupResponse().getValues().get(index).getValues().get(0).getResult().getNumFound();
		}

		return found;
	}
}
