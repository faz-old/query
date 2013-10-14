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

package de.faz.modules.query.fields;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * With that annotation you define your search mapping
 * and link your get-Method to a field in the search index.
 *
 * This annotation have two behaviours. Number one is the documentation
 * of course and the second one is the mapping at runtime.
 * A {@link de.faz.modules.query.Query} object uses this annotation to build
 * the query syntax. So this annotation is required when
 * you want to use the {@link de.faz.modules.query.Query} class.
 *
 * @author Andreas Kaubisch <a.kaubisch@faz.de>
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MapToField {
    String value();
}
