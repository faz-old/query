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
package de.faz.modules.query.solr.decoration;

import de.faz.modules.query.AbstractQueryDecorator;
import de.faz.modules.query.Query;
import org.apache.commons.lang3.StringUtils;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class DisableSolrCaching extends AbstractQueryDecorator {

	public DisableSolrCaching(final Query q) {
		super(q);
	}

	@Override
	public String toString() {
		String delegateString = super.toString();
		return StringUtils.isEmpty(delegateString) ? delegateString : "{!cache=false}" + delegateString;
	}


}
