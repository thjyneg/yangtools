/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.data.codec.gson;

import com.google.common.base.Preconditions;
import org.opendaylight.yangtools.yang.data.impl.codec.DataStringCodec;
import org.opendaylight.yangtools.yang.data.impl.codec.TypeDefinitionAwareCodec;

/**
 * Abstract base implementation of {@link JSONCodec}, which wraps a {@link TypeDefinitionAwareCodec}.
 *
 * @param <T> Deserialized object type
 */
abstract class AbstractJSONCodec<T> implements JSONCodec<T> {
    private final DataStringCodec<T> codec;

    AbstractJSONCodec(final DataStringCodec<T> codec) {
        this.codec = Preconditions.checkNotNull(codec);
    }

    @Override
    public final Class<T> getDataClass() {
        return codec.getInputClass();
    }

    @Override
    public final T deserializeString(final String input) {
        return codec.deserialize(input);
    }

    final String serialize(final T input) {
        return codec.serialize(input);
    }
}
