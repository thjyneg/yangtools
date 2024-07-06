/*
 * Copyright (c) 2024 PANTHEON.tech, s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.binding;

import static java.util.Objects.requireNonNull;

import org.eclipse.jdt.annotation.NonNull;
import org.opendaylight.yangtools.yang.common.UnresolvedQName.Unqualified;

/**
 * An {@link ExactPropertyStep} for a property generated for a {@code leaf-list} statement. It matches a particular
 * item in the list.
 *
 * @param <C> containing {@link DataContainer} type
 * @param <V> value type
 */
public record LeafListPropertyStep<C extends DataContainer, V>(
        @NonNull Class<C> containerType,
        @NonNull Class<V> valueType,
        @NonNull Unqualified yangIdentifier,
        @NonNull V value) implements ExactPropertyStep<C, V> {
    public LeafListPropertyStep {
        requireNonNull(containerType);
        requireNonNull(valueType);
        requireNonNull(yangIdentifier);
        requireNonNull(value);
    }
}
