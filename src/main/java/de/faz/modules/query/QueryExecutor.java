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

import javax.annotation.Nonnull;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public abstract class QueryExecutor {

    @Nonnull
    public SearchContext.SearchResult execute(@Nonnull final Query query, @Nonnull final SearchSettings settings) {
        return executeQuery(query, settings);
    }

    @Nonnull
    protected abstract SearchContext.SearchResult executeQuery(@Nonnull final Query query, @Nonnull final SearchSettings settings);

}
