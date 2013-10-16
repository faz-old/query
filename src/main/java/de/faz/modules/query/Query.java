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

import de.faz.modules.query.exception.InvalidQueryException;
import de.faz.modules.query.fields.FieldDefinitionGenerator;

import java.lang.reflect.Array;
import java.util.Stack;

/**
 * This Query class represents a possibility to construct
 * dynamic queries for a search database (i.e. Solr or Lucene)
 * and produce readable source code.
 *
 * This class throws an {@link de.faz.modules.query.exception.InvalidQueryException} when you use this
 * class in a wrong way. Please keep in mind that you need a prepared
 * {@link de.faz.modules.query.fields.Mapping} object before you can use that with the query syntax.
 * When you forget to call {@link de.faz.modules.query.DefaultSearchContext#createFieldDefinitionFor(Class)} or
 * you don't use that returned object your queries doesn't work and you
 * will get an {@link de.faz.modules.query.exception.InvalidQueryException}.
 *
 * @author Andreas Kaubisch <a.kaubisch@faz.de>
 */
public class Query {
    private FieldDefinitionGenerator definitionGenerator;

    protected final Stack<QueryItem> queryElementStack;


    public enum Operator {
        AND("AND", true),
        OR("OR", true),
        NOT("NOT", false);

        private String representation;
        private boolean bothSides;

        private Operator(final String stringRepresentation, final boolean separateOnBothSides) {
            this.representation = stringRepresentation;
            this.bothSides = separateOnBothSides;
        }

        public String getRepresentation() {
            StringBuffer sb = new StringBuffer();
            if(bothSides) {
                sb.append(' ');
            }
            return sb.append(representation).append(' ').toString();
        }
    }

    protected Query(final FieldDefinitionGenerator generator) {
	    queryElementStack = new Stack<>();
        definitionGenerator = generator;
    }

    @Override
    public String toString() {
        String flattenItems = "";
        QueryItem[] items = queryElementStack.toArray(new QueryItem[0]);

        if(items.length > 1) {
            flattenItems = new ItemChain(items).toCharSequence().toString();
        } else if(items.length == 1) {
            flattenItems = items[0].toCharSequence().toString();
        }

        return flattenItems;
    }

    public QueryModification modify() {
        return new QueryModification(this);
    }

    Stack<QueryItem> getItemStack() {
        return queryElementStack;
    }


    //Query 2.0 methods

    /**
     * This method takes an element and an array of {@link QueryItem} objects that will be combined
     * together with an 'and' operator.
     *
     * @param item the first {@link QueryItem} object that will be combined with items
     * @param items additional {@link QueryItem} objects
     * @return a new {@link QueryItem} that combines item and items with an 'and' operator.
     */
    public QueryItem and(final QueryItem item, final QueryItem... items) {
        return new ItemChain(Operator.AND.getRepresentation(), mergeItems(item, items));
    }

    /**
     * This method take an element and an array of {@link QueryItem} objects that will be combined
     * together with an 'or' operator.
     *
     * @param item the first {@link QueryItem} object that will be combined with items
     * @param items additional {@link QueryItem} objects
     * @return a new {@link QueryItem} that combines item and items with an 'or' operator.
     */
    public QueryItem or(final QueryItem item, final QueryItem... items) {
        return new ItemChain(Operator.OR.getRepresentation(), mergeItems(item, items));
    }

    /**
     * This method requires a {@link QueryItem} object that will be used to create
     * a negation of this. It returns a new instance of {@link QueryItem} that represents
     * the negation of the given {@code item} parameter.
     * @param item a required{@link QueryItem} object that will be negated
     * @return a new instance of {@link QueryItem} object that negates the {@code item} param
     */
    public QueryItem not(final QueryItem item) {
        return new OperatorItem(Operator.NOT.getRepresentation(), item);
    }

    /**
     * This method add a new {@link QueryItem} object to the current query instance. This method is
     * required when you want to add some query items to your current query. When you forget to add
     * a created {@link QueryItem} to this query your final query will not contain your creation.
     *
     * @param item a desired part of your query
     * @return the current {@link Query} instance
     */
    public Query add(final QueryItem item) {
        queryElementStack.push(item);
        return this;
    }

    /**
     * This method adds all elements of the given {@link Query} instance. This method is required
     * when you have an existing query and you want to combine your existing query with this.
     * @param query an existing query you want to add
     * @return the current {@link Query} instance
     */
    public Query addItemsOf(final Query query) {
        for(QueryItem item : query.queryElementStack) {
            queryElementStack.push(item);
        }
        return this;
    }


    /**
     * This method creates a new instance of {@link de.faz.modules.query.TermQueryPart} with the given {@code fieldDefinition}.
     * To use this method in the defined way you must create a field definition first. This is done by calling
     * {@link de.faz.modules.query.SearchContext#createFieldDefinitionFor(Class)}. This method will throw a {@link de.faz.modules.query.exception.InvalidQueryException}
     * when you don't use the field definition created with the {@link de.faz.modules.query.SearchContext}
     *
     * @param fieldDefinition a Method call of the field definition defined with {@link de.faz.modules.query.SearchContext}
     * @return a new instance of {@link de.faz.modules.query.TermQueryPart} that represents the previously called field definition
     *         method.
     * @throws de.faz.modules.query.exception.InvalidQueryException when you don't called a field definition first
     */
    public TermQueryPart term(final Object fieldDefinition) {
        if(definitionGenerator.isEmpty()) {
            throw new InvalidQueryException("The field description of method term was null.");
        }
        return new TermQueryPart(definitionGenerator.pop());
    }

    /**
     * This method returns a boolean whether this query has elements or not. It returns true when you doesn't add
     * a {@link QueryItem} to this instance.
     *
     * @return true when this query has element. Otherwise it returns false.
     */
    public boolean isEmpty() {
        return queryElementStack != null && queryElementStack.isEmpty();
    }


    @Override
    public int hashCode() {
        int result = definitionGenerator != null ? definitionGenerator.hashCode() : 0;
        result = 31 * result + (queryElementStack != null ? queryElementStack.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if(obj instanceof Query) {
            return ((Query) obj).getItemStack().containsAll(queryElementStack);
        }
        return super.equals(obj);
    }

    public boolean contains(final QueryItem item) {
        boolean found = false;
        for(int i=0, size= queryElementStack.size(); i < size && !found; i++) {
            QueryItem stackItem = queryElementStack.get(i);
            if(stackItem.equals(item)) {
                found = true;
            } else {
                found = stackItem.contains(item);
            }
        }
        return found;
    }

    //end Query 2.0 methods

    private <T> T[] mergeItems(final T item, final T[] items) {
        T[] newArray = (T[]) Array.newInstance(items.getClass().getComponentType(), items.length + 1);
        newArray[0] = item;
        System.arraycopy(items, 0, newArray, 1, items.length);
        return newArray;
    }

}
