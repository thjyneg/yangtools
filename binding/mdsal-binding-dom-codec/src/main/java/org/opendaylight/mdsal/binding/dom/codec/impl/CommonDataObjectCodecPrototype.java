/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.mdsal.binding.dom.codec.impl;

import static java.util.Objects.requireNonNull;

import org.eclipse.jdt.annotation.NonNull;
import org.opendaylight.mdsal.binding.runtime.api.CompositeRuntimeType;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier.Item;

/**
 * Common superclass for {@link DataObjectCodecPrototype} and {@link AugmentationCodecPrototype}.
 *
 * @param <R> {@link CompositeRuntimeType} type
 */
abstract sealed class CommonDataObjectCodecPrototype<R extends CompositeRuntimeType>
        extends DataContainerPrototype<CommonDataObjectCodecContext<?, R>, R>
        permits AugmentationCodecPrototype, DataObjectCodecPrototype {
    private final @NonNull Item<?> bindingArg;

    CommonDataObjectCodecPrototype(final Item<?> bindingArg, final R runtimeType, final CodecContextFactory factory) {
        super(factory, runtimeType);
        this.bindingArg = requireNonNull(bindingArg);
    }

    @Override
    final Class<? extends DataObject> javaClass() {
        return bindingArg.getType();
    }

    final @NonNull Item<?> getBindingArg() {
        return bindingArg;
    }
}
