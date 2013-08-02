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

import java.util.Stack;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
class QueryDefaultComparator extends Query {

    private Query query;
    private Operator operator;

    QueryDefaultComparator(Query q, Operator op) {
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
    public QueryModification modify() {
        return query.modify();
    }

    @Override
    Stack<QueryItem> getItemStack() {
        return query.getItemStack();
    }

    @Override
    public QueryItem and(final QueryItem item, final QueryItem... items) {
        return query.and(item, items);
    }

    @Override
    public QueryItem or(final QueryItem item, final QueryItem... items) {
        return query.or(item, items);
    }

    @Override
    public QueryItem not(final QueryItem item) {
        return query.not(item);
    }

    @Override
    public Query add(final QueryItem item) {
        return query.add(item);
    }

    @Override
    public TermQueryPart term(final Object fieldDefinition) {
        return query.term(fieldDefinition);
    }

    @Override
    public int hashCode() {
        return query.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return query.equals(obj);
    }

    @Override
    public boolean contains(final QueryItem item) {
        return query.contains(item);
    }
}
