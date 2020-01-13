/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.parser.rfc7950.stmt;

import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.model.api.ContainerSchemaNode;
import org.opendaylight.yangtools.yang.model.api.meta.DeclaredStatement;
import org.opendaylight.yangtools.yang.model.api.stmt.compat.ActionNodeContainerCompat;
import org.opendaylight.yangtools.yang.model.api.stmt.compat.NotificationNodeContainerCompat;
import org.opendaylight.yangtools.yang.parser.spi.meta.StmtContext;

@Deprecated
public abstract class AbstractEffectiveContainerSchemaNode<D extends DeclaredStatement<QName>>
        extends AbstractEffectiveMustConstraintAwareSimpleDataNodeContainer<D>
        implements ContainerSchemaNode, ActionNodeContainerCompat<QName, D>, NotificationNodeContainerCompat<QName, D> {
    protected AbstractEffectiveContainerSchemaNode(final StmtContext<QName, D, ?> ctx) {
        super(ctx);
    }
}
