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
public class QueryModification {

    private final Query q;

    QueryModification(final Query q) {
        this.q = q;
    }

    public ModificationSelection all() {
        return createSelection(null, null);
    }

    public ModificationSelection last() {
        return createSelection(null, 0);
    }

    public ModificationSelection first() {
        return createSelection(0, null);
    }

    public ModificationSelection range(final int from, final int to) {
        if(from < 0) { throw new IllegalArgumentException("the lower limit must be greater equal 0"); }
        if(from > to) { throw new IllegalArgumentException("the lower limit must be lower equal than the upper limit"); }
        return createSelection(from, to);
    }

    public ModificationSelection item(final int index) {
        if(index < 0) { throw new IllegalArgumentException("the index must be greater equal 0"); }
        return range(index, index);
    }

    public ModificationSelection last(final int numElements) {
        if(numElements < 0) { throw new IllegalArgumentException("the number of elements must be greater equal 0"); }

        return createSelection(null, numElements);
    }

    public ModificationSelection first(final int numElements) {
        if(numElements < 0) { throw new IllegalArgumentException("the number of elements must be greater equal 0"); }
        return createSelection(numElements, null);
    }

    private ModificationSelection createSelection(final Integer lower, final Integer upper) {
        return new ModificationSelection(q, lower, upper);
    }
}
