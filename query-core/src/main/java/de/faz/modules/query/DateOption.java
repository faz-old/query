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

import org.apache.solr.common.util.DateUtil;

import java.util.Date;
import java.util.Objects;

/**
 * @author Andreas Kaubisch <a.kaubisch@faz.de>
 */
public abstract class DateOption {

    public static final DateOption WILDCARD = new PredefinedValueOption("*");
    public static final DateOption NOW = new PredefinedValueOption("NOW");

    public enum TimeUnit {
        SECOND, SECONDS, MINUTE, MINUTES, HOUR, HOURS, DAY, DAYS, MONTH, MONTHS, YEAR, YEARS;


    }
    public static DateOption from(final Date date) {
        return new NativeDateOption(date);
    }

    public static DateOption nowMinus(final int timeValue, final TimeUnit timeUnit) {
        return new PredefinedValueOption(NOW.flatten() + "-" + timeValue + timeUnit.toString());
    }

    public static DateOption nowPlus(final int timeValue, final TimeUnit timeUnit) {
        return new PredefinedValueOption(NOW.flatten() + "+" + timeValue + timeUnit.toString());
    }

    protected abstract CharSequence flatten();

    private static final class PredefinedValueOption extends DateOption {
        private final CharSequence value;
        private PredefinedValueOption(final CharSequence value) {
            this.value = value;
        }

        @Override
        protected CharSequence flatten() {
            return value;
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof PredefinedValueOption) {
                return Objects.equals(value, ((PredefinedValueOption) obj).value);
            }
            return super.equals(obj);
        }
    }

    private final static class NativeDateOption extends DateOption {
        private final Date date;
        private NativeDateOption(final Date date) {
            this.date = date;
        }

        @Override
        protected CharSequence flatten() {
            return DateUtil.getThreadLocalDateFormat().format(date);
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof NativeDateOption) {
                return Objects.equals(date, ((NativeDateOption) obj).date);
            }
            return super.equals(obj);
        }

        @Override
        public int hashCode() {
            return date.hashCode();
        }
    }
}
