/*
 * Copyright (c) 2021 PANTHEON.tech, s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.odlext.parser;

import com.google.common.collect.ImmutableList;
import org.opendaylight.yangtools.odlext.model.api.AugmentIdentifierStatement;
import org.opendaylight.yangtools.yang.common.UnqualifiedQName;
import org.opendaylight.yangtools.yang.model.api.meta.DeclaredStatement;
import org.opendaylight.yangtools.yang.model.spi.meta.AbstractDeclaredStatement.WithArgument.WithSubstatements;

final class AugmentIdentifierStatementImpl extends WithSubstatements<UnqualifiedQName>
        implements AugmentIdentifierStatement {
    AugmentIdentifierStatementImpl(final UnqualifiedQName argument,
            final ImmutableList<? extends DeclaredStatement<?>> substatements) {
        super(argument.getLocalName(), argument, substatements);
    }
}
