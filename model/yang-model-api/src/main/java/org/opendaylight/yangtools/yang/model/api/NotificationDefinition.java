/*
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.model.api;

import org.eclipse.jdt.annotation.NonNull;
import org.opendaylight.yangtools.yang.model.api.stmt.NotificationEffectiveStatement;

/**
 * Interface describing YANG 'notification' statement. The notification statement is used to define a NETCONF
 * notification. Note that this interface is not a {@link DataSchemaNode}, which renders compatibility problematic. Use
 * {@link #toContainerLike()} to get a {@link ContainerLike}, which can serve as a bridge.
 */
public interface NotificationDefinition extends SchemaNode, DataNodeContainer, AugmentationTarget, CopyableNode,
        MustConstraintAware, EffectiveStatementEquivalent<NotificationEffectiveStatement> {
    /**
     * Return a {@link ContainerLike} backed by this definition's {@link #getChildNodes()}.
     *
     * @return A compatibility {@link ContainerLike}
     */
    default @NonNull ContainerLikeCompat toContainerLike() {
        return new NotificationAsContainer(this);
    }
}
