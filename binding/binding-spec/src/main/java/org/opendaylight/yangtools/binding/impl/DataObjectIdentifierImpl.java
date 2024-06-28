/*
 * Copyright (c) 2024 PANTHEON.tech, s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.binding.impl;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import org.eclipse.jdt.annotation.NonNull;
import org.opendaylight.yangtools.binding.DataObject;
import org.opendaylight.yangtools.binding.DataObjectIdentifier;
import org.opendaylight.yangtools.binding.ExactDataObjectStep;

// FIXME: YANGTOOLS-1577: non-abstract
public abstract sealed class DataObjectIdentifierImpl<T extends DataObject>
        extends AbstractDataObjectReference<T, ExactDataObjectStep<?>> implements DataObjectIdentifier<T>
        permits DataObjectIdentifierWithKey {
    @java.io.Serial
    private static final long serialVersionUID = 1L;

    DataObjectIdentifierImpl(final Iterable<? extends @NonNull ExactDataObjectStep<?>> steps) {
        super(steps);
    }

    public static final @NonNull DataObjectIdentifierImpl<?> ofUnsafeSteps(
            final ImmutableList<? extends @NonNull ExactDataObjectStep<?>> steps) {
        // FIXME: YANGTOOLS-1577: implement this
        throw new UnsupportedOperationException();
    }

    @Override
    protected final Class<?> contract() {
        return DataObjectIdentifier.class;
    }

    @java.io.Serial
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        throwNSE();
    }

    @java.io.Serial
    private void readObjectNoData() throws ObjectStreamException {
        throwNSE();
    }

    @java.io.Serial
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        throwNSE();
    }
}
