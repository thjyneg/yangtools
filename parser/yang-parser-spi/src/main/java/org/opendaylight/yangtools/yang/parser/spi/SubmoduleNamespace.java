/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.parser.spi;

import org.eclipse.jdt.annotation.NonNull;
import org.opendaylight.yangtools.yang.model.api.stmt.SubmoduleEffectiveStatement;
import org.opendaylight.yangtools.yang.model.api.stmt.SubmoduleStatement;
import org.opendaylight.yangtools.yang.model.repo.api.SourceIdentifier;
import org.opendaylight.yangtools.yang.parser.spi.meta.NamespaceBehaviour;
import org.opendaylight.yangtools.yang.parser.spi.meta.StatementNamespace;

/**
 * Submodule equivalent of ModuleNamespace.
 */
// FIXME: describe scoping of this namespace
public final class SubmoduleNamespace
        extends StatementNamespace<SourceIdentifier, SubmoduleStatement, SubmoduleEffectiveStatement> {
    public static final @NonNull NamespaceBehaviour<?, ?, ?> BEHAVIOUR =
        NamespaceBehaviour.global(SubmoduleNamespace.class);

    private SubmoduleNamespace() {
        // Hidden on purpose
    }
}
