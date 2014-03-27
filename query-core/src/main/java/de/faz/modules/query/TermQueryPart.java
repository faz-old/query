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

import de.faz.modules.query.fields.FieldDefinitionGenerator;

import javax.annotation.Nonnull;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class TermQueryPart {

    public enum Operator {
        AND(" AND "),
        OR(" OR ");

        private String value;

        private Operator(final String opValue) {
            this.value = opValue;
        }

        @Override
        public String toString() {
            return value;
        }

    }
    private final FieldDefinitionGenerator.FieldDefinition definition;

    public TermQueryPart(@Nonnull final FieldDefinitionGenerator.FieldDefinition definition) {
        this.definition = definition;
    }

	@Nonnull
    public QueryItem value(@Nonnull final CharSequence value) {
        return value(new StringValue(value));
    }

	@Nonnull
    public QueryItem value(@Nonnull final ValueItem value) {
        return new TermItem(definition, value);
    }

	@Nonnull
	public QueryItem values(@Nonnull final CharSequence... values) {
        return values(Operator.OR, values);
    }

	@Nonnull
	public QueryItem values(@Nonnull final Operator operator, @Nonnull final CharSequence... values) {
	    if(values.length == 1) {
		    return value(values[0]);
	    }
        return new TermItem(definition, new OperatorValue(operator.toString(), values));
    }

	@Nonnull
    public QueryItem range(@Nonnull final Date from, @Nonnull final Date to) {
        return range(DateOption.from(from), DateOption.from(to));
    }

	@Nonnull
    public QueryItem range(@Nonnull final DateOption fromOption, @Nonnull final DateOption toOption) {
        return new TermItem(definition, new DateValue(fromOption, toOption));
    }

	@Nonnull
	public QueryItem range(@Nonnull final Calendar startDate, @Nonnull final Calendar endDate) {
		return range(startDate.getTime(), endDate.getTime());
	}

    private class DateValue extends ValueItem {

        private DateOption from, to;


        public DateValue(final DateOption from, final DateOption to) {
            this.from = from;
            this.to = to;
        }

        @Override
        CharSequence toCharSequence() {
            final StringBuffer sb = new StringBuffer();
            sb.append('[').append(from.flatten()).append(" TO ").append(to.flatten()).append(']');
            return sb;
        }

        @Override
        public boolean equals(final Object obj) {
            if(obj instanceof DateValue) {
                final DateValue value = (DateValue) obj;
                return Objects.equals(from, value.from)
					&& Objects.equals(to, value.to);
            }
            return super.equals(obj);
        }

	    @Override
	    public int hashCode() {
		    return Objects.hash(from, to);
	    }
    }
}
