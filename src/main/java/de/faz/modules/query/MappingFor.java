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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation represents a link between
 * a {@link de.faz.modules.query.Mapping} and a Policy that is mapped
 * by the annotated {@link de.faz.modules.query.Mapping}. It's only for
 * documentation so you don't need to annotate your
 * mapping class with this. It's just for other
 * developers to read your code easily.
 *
 * @author Andreas Kaubisch <a.kaubisch@faz.de>
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface MappingFor {
    Class<?> value();
}
