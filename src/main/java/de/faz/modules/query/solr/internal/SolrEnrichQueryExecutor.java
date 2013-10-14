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

package de.faz.modules.query.solr.internal;

import de.faz.modules.query.EnrichQueryExecutor;
import org.apache.solr.client.solrj.SolrQuery;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public abstract class SolrEnrichQueryExecutor implements EnrichQueryExecutor<SolrQuery> {
    public  abstract void enrich(final SolrQuery query);
}
