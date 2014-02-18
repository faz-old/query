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

import com.google.common.collect.Lists;
import de.faz.modules.query.SearchContext;
import de.faz.modules.query.fields.Mapping;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.QueryResponse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
			Group group = getMergedGroupList(response.getGroupResponse().getValues()).get(index);
			it = result.createIteratorFromDocumentList(response, mappingClass, group.getResult());
		}
		return it;
	}

	public int getGroupCount() {
		int count = 0;
		if(response != null) {
			count = getMergedGroupList(response.getGroupResponse().getValues()).size();
		}

		return count;
	}

    private List<Group> getMergedGroupList(List<GroupCommand> groupCommandList) {
        List<Group> groupList = Lists.newArrayList();
        for (GroupCommand groupCommand : groupCommandList) {
            groupList.addAll(groupCommand.getValues());
        }
        return groupList;
    }

	public long getResultCount(final int index) {
		long found = 0;
		if(getGroupCount() > index) {
			found = getMergedGroupList(response.getGroupResponse().getValues()).get(index).getResult().getNumFound();
		}

		return found;
	}
}
