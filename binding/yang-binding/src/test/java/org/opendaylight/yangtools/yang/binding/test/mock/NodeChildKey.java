/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.binding.test.mock;

import java.io.Serial;
import org.opendaylight.yangtools.yang.binding.Identifier;

public class NodeChildKey implements Identifier<NodeChild> {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int id;

    public NodeChildKey(final int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public boolean equals(final Object obj) {
        return this == obj || obj != null && getClass() == obj.getClass() && id == ((NodeChildKey) obj).id;
    }
}
