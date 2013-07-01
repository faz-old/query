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

import de.faz.modules.query.FieldDefinition;

import java.util.Date;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class TermQueryPart {


    public enum Operator {
        AND(" AND "),
        OR(" OR ");

        private String value;

        private Operator(String opValue) {
            this.value = opValue;
        }

        @Override
        public String toString() {
            return value;
        }

    }
    private FieldDefinition definition;
    public TermQueryPart(final FieldDefinition definition) {
        this.definition = definition;
    }

    public Query.QueryItem value(final CharSequence value) {
        return value(new Query.StringValue(value));
    }
    public Query.QueryItem value(final Query.ValueItem value) {
        return new Query.TermItem(definition, value);
    }

    public Query.QueryItem values(final CharSequence... values) {
        if(values.length == 1) {
            return value(values[0]);
        }
        return new Query.TermItem(definition, new Query.OperatorValue(Operator.OR.toString(), values));
    }

    public Query.QueryItem values(final Operator operator, final String... values) {
        return new Query.TermItem(definition, new Query.OperatorValue(operator.toString(), values));
    }

    public Query.QueryItem range(final Date from, final Date to) {
        return range(DateOption.from(from), DateOption.from(to));
    }

    public Query.QueryItem range(final DateOption fromOption, final DateOption toOption) {
        return new Query.TermItem(definition, new DateValue(fromOption, toOption));
    }

    private class DateValue extends Query.ValueItem {

        private DateOption from, to;


        public DateValue(DateOption from, DateOption to) {
            this.from = from;
            this.to = to;
        }

        @Override
        CharSequence toCharSequence() {
            StringBuffer sb = new StringBuffer();
            sb.append('[').append(from.flatten()).append(" TO ").append(to.flatten()).append(']');
            return sb;
        }

        @Override
        public boolean equals(final Object obj) {
            if(obj instanceof DateValue) {
                DateValue value = (DateValue) obj;
                return from.equals(value.from) && to.equals(value.to);
            }
            return super.equals(obj);
        }
    }


}
