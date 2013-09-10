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
public class ModificationSelection {
    private Query query;
    private Range range;

    ModificationSelection(Query q, Integer lowerLimit, Integer upperLimit) {
        this.query = q;
        this.range = new Range(lowerLimit, upperLimit);
    }

    Integer getLowerLimit() {
        return range.from;
    }

    Integer getUpperLimit() {
        return range.to;
    }

    public void remove() {
        if(range.isAll()) {
            query.getItemStack().removeAllElements();
        }


    }

    public SurroundAction surroundWith() {
        return new SurroundAction(this);
    }

    public static class SurroundAction {
        private ModificationSelection selection;

        private SurroundAction(ModificationSelection selection) {
            this.selection = selection;
        }

        public void and() {
            Query q = selection.query;
            if(selection.range.isAll() && q.getItemStack().size() > 1) {
                Stack<Query.QueryItem> savedItems = (Stack<Query.QueryItem>)q.getItemStack().clone();
                selection.remove();
                q.getItemStack().push(q.and(savedItems.pop(), savedItems.toArray(new Query.QueryItem[savedItems.size() - 1])));
            }
        }

        public void or() {
            Query q = selection.query;
            if(selection.range.isAll()) {
                Stack<Query.QueryItem> savedItems = (Stack<Query.QueryItem>)q.getItemStack().clone();
                selection.remove();
                q.getItemStack().push(q.or(savedItems.pop(), savedItems.toArray(new Query.QueryItem[savedItems.size() - 1])));
            }
        }
    }

    private static class Range {
        Integer from;
        Integer to;

        private Range(final Integer from, final Integer to) {
            this.from = from;
            this.to = to;
        }

        private boolean isAll() {
            return from == null && to == null;
        }
    }
}
