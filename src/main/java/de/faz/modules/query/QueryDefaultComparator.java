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

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
class QueryDefaultComparator extends AbstractQueryDecorator {

    private Query query;
    private Operator operator;

    QueryDefaultComparator(Query q, Operator op) {
	    super(q);
        this.query = q;
        this.operator = op;
    }

    @Override
    public String toString() {
        switch(operator) {
            case AND:
                query.modify().all().surroundWith().and();
                break;
            case OR:
                query.modify().all().surroundWith().or();
                break;
            default:
                //do nothing
                break;
        }
        return query.toString();
    }

    @Override
    public boolean equals(final Object obj) {
	    if(obj instanceof QueryDefaultComparator) {
		    QueryDefaultComparator other = (QueryDefaultComparator) obj;
		    return operator.equals(other.operator) && query.equals(other.query);
	    }
	    return super.equals(obj);
    }
}
