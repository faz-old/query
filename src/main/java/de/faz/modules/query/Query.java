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

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

import org.apache.solr.client.solrj.util.ClientUtils;

import com.google.common.base.Joiner;

/**
 * This Query class represents a possibility to construct
 * dynamic queries for a search database (i.e. Solr or Lucene)
 * and produce readable source code.
 *
 * This class throws an {@link de.faz.modules.query.InvalidQueryException} when you use this
 * class in a wrong way. Please keep in mind that you need a prepared
 * {@link de.faz.modules.query.Mapping} object before you can use that with the query syntax.
 * When you forget to call {@link de.faz.modules.query.DefaultSearchContext#createFieldDefinitionFor(Class)} or
 * you don't use that returned object your queries doesn't work and you
 * will get an {@link de.faz.modules.query.InvalidQueryException}.
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

        private Operator(String stringRepresentation, boolean separateOnBothSides) {
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

    protected Query() {
        queryElementStack = new Stack<>();
    }

    Query(FieldDefinitionGenerator generator) {
        this();
        definitionGenerator = generator;
    }

    /**
     *
     * @param fieldDescription
     * @param value
     * @return
     * @deprecated use {@link Query#term(Object)} to get a {@link de.faz.modules.query.TermQueryPart} object and use method {@link de.faz.modules.query.TermQueryPart#value(CharSequence)}
     */
    @Deprecated
    public Query term(Object fieldDescription, CharSequence value) {
        if(definitionGenerator.isEmpty()) {
            throw new InvalidQueryException("The field description of method term was null.");
        }
        FieldDefinition fieldDefinition = definitionGenerator.pop();
        if(fieldDefinition != null && value != null) {
            pushTermItemWithDefinitionAndValue(fieldDefinition, new DeprecatedStringValue(value));
        }

        return this;
    }

    /**
     *
     * @param element
     * @param elements
     * @return
     * @deprecated  use {@link Query#and(Query.QueryItem, Query.QueryItem...)} to get a new {@link QueryItem} where all items are combined with AND and add it to
     *              the query with {@link Query#add(Query.QueryItem)}
     */
    @Deprecated
    public Query and(Query element, Query... elements) {
        if(element == null || elements == null || elements.length == 0) {
            throw new IllegalArgumentException("you need a minimum of two elements for an 'or' query");
        }
        QueryItem[] items = getLastItemsOf(elements.length + 1);
        pushItemsWithSeparatorWhenHasItems(items, Operator.AND.getRepresentation());
        return this;
    }

    /**
     *
     * @param element
     * @param elements
     * @return
     * @deprecated  use {@link Query#or(Query.QueryItem, Query.QueryItem...)} to get a new {@link QueryItem} where all items are combined with OR and add it to
     *              the query with {@link Query#add(Query.QueryItem)}
     */
    @Deprecated
    public Query or(Query element, Query... elements) {
        if(element == null || elements == null || elements.length == 0) {
            throw new IllegalArgumentException("you need a minimum of two elements for an 'or' query");
        }
        pushItemsWithSeparatorWhenHasItems(getLastItemsOf(elements.length + 1), Operator.OR.getRepresentation());

        return this;
    }

    /**
     *
     * @param element
     * @deprecated use {@link Query#not(Query.QueryItem)} instead
     */
    @Deprecated
    public Query not(Query element) {
        if(element == null) {
//            throw new IllegalArgumentException("You need an element for this function.");
            return null;
        }

        QueryItem elem = queryElementStack.pop();
        queryElementStack.push(new OperatorItem(Operator.NOT.getRepresentation(), elem));
        return this;
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

    private void pushItemsWithSeparatorWhenHasItems(QueryItem[] items, String separator) {
        if(items.length > 0) {
            queryElementStack.push(new ItemChain(separator, items));
        }
    }

    private void pushTermItemWithDefinitionAndValue(final FieldDefinition fieldDefinition, final ValueItem value) {
        queryElementStack.push(new TermItem(fieldDefinition, value));
    }

    private QueryItem[] getLastItemsOf(int length) {
        QueryItem[] items = new QueryItem[length];
        for(int i = length -1; i > -1; i--) {
            if(!queryElementStack.empty()) {
                items[i] = queryElementStack.pop();
            }
        }

        return items;
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
    public QueryItem and(QueryItem item, QueryItem... items) {
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
    public QueryItem or(QueryItem item, QueryItem... items) {
        return new ItemChain(Operator.OR.getRepresentation(), mergeItems(item, items));
    }

    /**
     * This method requires a {@link QueryItem} object that will be used to create
     * a negation of this. It returns a new instance of {@link QueryItem} that represents
     * the negation of the given {@code item} parameter.
     * @param item a required{@link QueryItem} object that will be negated
     * @return a new instance of {@link QueryItem} object that negates the {@code item} param
     */
    public QueryItem not(QueryItem item) {
        return new OperatorItem(Operator.NOT.getRepresentation(), item);
    }

    /**
     * This method add a new {@link QueryItem} object to the current query instance. This method is
     * required when you want to add some query items to your current query. When you forget to add
     * a created {@link QueryItem} to this querys your final query will not contain your creation.
     *
     * @param item a desired part of your query
     * @return the current {@link Query} instance
     */
    public Query add(final QueryItem item) {
        queryElementStack.push(item);
        return this;
    }

    /**
     * This method creates a new instance of {@link de.faz.modules.query.TermQueryPart} with the given {@code fieldDefinition}.
     * To use this method in the defined way you must create a field definition first. This is done by calling
     * {@link de.faz.modules.query.SearchContext#createFieldDefinitionFor(Class)}. This method will throw a {@link InvalidQueryException}
     * when you don't use the field definition created with the {@link de.faz.modules.query.SearchContext}
     *
     * @param fieldDefinition a Method call of the field definition defined with {@link de.faz.modules.query.SearchContext}
     * @return a new instance of {@link de.faz.modules.query.TermQueryPart} that represents the previously called field definition
     *         method.
     * @throws InvalidQueryException when you don't called a field definition first
     */
    public TermQueryPart term(final Object fieldDefinition) {
        if(definitionGenerator.isEmpty()) {
            throw new InvalidQueryException("The field description of method term was null.");
        }
        return new TermQueryPart(definitionGenerator.pop());
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
            return ((Query) obj).queryElementStack.containsAll(queryElementStack);
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

    protected static class TermItem extends QueryItem {

        private final FieldDefinition field;
        private final ValueItem value;

        public TermItem(final FieldDefinition fieldDefinition, final ValueItem value) {
            this.field = fieldDefinition;
            this.value = value;
        }

        @Override
        public CharSequence toCharSequence() {
            StringBuffer sb = new StringBuffer();
            sb.append(field.name).append(':').append(value.toCharSequence());
            if(field.boost != 1) {
                sb.append('^').append(field.boost);
            }
            return sb;
        }

        @Override
        public int hashCode() {
            int result = field.hashCode();
            result = 31 * result + value.hashCode();
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if(obj instanceof TermItem) {
                TermItem termItem = (TermItem)obj;
                return field.equals(termItem.field) && value.equals(termItem.value);
            }
            return super.equals(obj);
        }

        @Override
        public boolean contains(final QueryItem item) {
            return false;
        }
    }

    private class OperatorItem extends QueryItem {
        private final String operator;
        private final QueryItem value;

        public OperatorItem(final String operator, final QueryItem value) {
            this.operator = operator;
            this.value = value;
        }

        @Override
        public CharSequence toCharSequence() {
            StringBuffer sb = new StringBuffer();
            CharSequence valueSequence= value.toCharSequence();
            if(valueSequence.length() > 0) {
                boolean hasBracket = valueSequence.charAt(0) == '(';
                sb.append(operator);
                if(!hasBracket) sb.append('(');
                sb.append(valueSequence);
                if(!hasBracket) sb.append(')');
            }
            return sb;
        }

        @Override
        public int hashCode() {
            int result = operator.hashCode();
            result = 31 * result + value.hashCode();
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if(obj instanceof OperatorItem) {
                OperatorItem objItem = (OperatorItem)obj;
                return operator.equals(objItem.operator) && value.equals(objItem.value);
            }
            return super.equals(obj);
        }

        @Override
        public boolean contains(final QueryItem item) {
            return value.equals(item);
        }
    }

    private class ItemChain extends QueryItem {
        private final String separator;
        private final QueryItem[] items;

        public ItemChain(final QueryItem... items) {
            this.separator = " ";
            this.items = items;
        }

        public ItemChain(final String separator, final QueryItem... items) {
            this.separator = separator;
            List<QueryItem> itemList = new ArrayList<>(Arrays.asList(items));
            itemList.removeAll(Collections.singleton(null));
            this.items = itemList.toArray(new QueryItem[0]);
        }

        @Override
        public CharSequence toCharSequence() {
            StringBuffer sb = new StringBuffer();
            sb.append('(');
            for(int i = 0; i < items.length; i++) {
                if(i > 0) {
                    sb.append(separator);
                }
                sb.append(items[i].toCharSequence());
            }
            sb.append(')');
            return sb;
        }

        @Override
        public int hashCode() {
            int result = separator.hashCode();
            result = 31 * result + Arrays.hashCode(items);
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if(obj instanceof ItemChain) {
                ItemChain chain = (ItemChain) obj;
                boolean containsAllItems = true;
                List<QueryItem> itemList = Arrays.asList(chain.items);
                for(QueryItem item : items) {
                    containsAllItems &= itemList.contains(item);
                }
                return containsAllItems && separator.equals(chain.separator);
            }
            return super.equals(obj);
        }

        @Override
        public boolean contains(final QueryItem item) {
            boolean found = false;
            for(int i = 0, size = items.length; i < size && !found; i++) {
                QueryItem child = items[i];
                if(child.equals(item)) {
                    found = true;
                } else {
                    found = child.contains(item);
                }
            }
            return found;
        }
    }

    protected static class StringValue extends ValueItem {

        private final static String[] ILLEGAL_CHARACTERS = new String [] {


                "+"
                , "-"
                , "&&"
                , "||"
                , "!"
                , "("
                , ")"
                , "{"
                , "}"
                , "["
                , "]"
                , "^"
                , " "
                , "~"
                , ":"
                , "\""
        };

        final CharSequence value;

        public StringValue(final CharSequence value) {
            this.value = value;
        }

        @Override
        public CharSequence toCharSequence() {
            String stringValue = value.toString();
            for(String character : ILLEGAL_CHARACTERS) {
                stringValue = stringValue.replaceAll(Pattern.quote(character), "\\\\"+ character);
            }
            return stringValue;
        }
    }
    protected static class DeprecatedStringValue extends ValueItem {

        final CharSequence value;

        public DeprecatedStringValue(final CharSequence value) {
            this.value = value;
        }

        @Override
        public CharSequence toCharSequence() {
            return value;
        }
    }

    protected static class OperatorValue extends ValueItem {

        private final String operator;
        private final CharSequence[] values;

        public OperatorValue(final String operator, final CharSequence[] values) {
            this.operator = operator;
            List<CharSequence> valueList = new ArrayList<>(Arrays.asList(values));
            valueList.removeAll(Collections.singleton(null));
            this.values = valueList.toArray(new CharSequence[0]);
        }

        @Override
        public CharSequence toCharSequence() {
            StringBuffer sb = new StringBuffer();
            sb.append('(');
            try {
                Joiner.on(operator).appendTo(sb, values);
            } catch (IOException e) {
                return "";
            }
            sb.append(')');
            return sb;
        }

        @Override
        public int hashCode() {
            int result = operator.hashCode();
            result = 31 * result + Arrays.hashCode(values);
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if(obj instanceof OperatorValue) {
                OperatorValue opValue = (OperatorValue) obj;
                boolean containsAllValues = Arrays.asList(values).containsAll(Arrays.asList(opValue.values));
                return containsAllValues && operator.equals(opValue.operator);
            }
            return super.equals(obj);
        }
    }

    public abstract static class QueryItem {
        abstract CharSequence toCharSequence();

        @Override
        public String toString() {
            return toCharSequence().toString();
        }

        @Override
        public boolean equals(final Object obj) {
            if(obj instanceof QueryItem) {
                return ((QueryItem) obj).toCharSequence().equals(toCharSequence());
            }
            return super.equals(obj);
        }

        public abstract boolean contains(QueryItem item);
    }

    public abstract static class ValueItem {
        abstract CharSequence toCharSequence();

        @Override
        public String toString() {
            return toCharSequence().toString();
        }

        @Override
        public boolean equals(final Object obj) {
            if(obj instanceof ValueItem) {
                return toCharSequence().equals(((ValueItem) obj).toCharSequence());
            }
            return super.equals(obj);
        }
    }
}
