/*
 * Copyright (c) 2023 PANTHEON.tech, s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
module org.opendaylight.yangtools.openconfig.parser.support {
    exports org.opendaylight.yangtools.openconfig.parser;

    requires transitive com.google.common;
    requires transitive org.opendaylight.yangtools.yang.parser.api;
    requires transitive org.opendaylight.yangtools.openconfig.model.api;
    requires org.opendaylight.yangtools.yang.common;
    requires org.opendaylight.yangtools.yang.model.api;
    requires org.opendaylight.yangtools.yang.model.spi;
    requires org.opendaylight.yangtools.yang.parser.spi;
    requires org.slf4j;

    // Annotations
    requires static transitive org.eclipse.jdt.annotation;
}

