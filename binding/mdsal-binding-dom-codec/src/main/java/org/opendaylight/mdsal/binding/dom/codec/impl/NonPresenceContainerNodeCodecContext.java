/*
 * Copyright (c) 2022 PANTHEON.tech, s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.mdsal.binding.dom.codec.impl;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import org.eclipse.jdt.annotation.NonNull;
import org.opendaylight.mdsal.binding.runtime.api.ContainerLikeRuntimeType;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.data.impl.schema.Builders;

/**
 * A {@link ContainerNodeCodecContext} specialized for {@code container}s which do not have a presence statement.
 */
final class NonPresenceContainerNodeCodecContext<D extends DataObject> extends ContainerNodeCodecContext<D> {
    private static final VarHandle EMPTY_OBJECT;

    static {
        try {
            EMPTY_OBJECT = MethodHandles.lookup().findVarHandle(NonPresenceContainerNodeCodecContext.class,
                "emptyObject", DataObject.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @SuppressWarnings("unused")
    private volatile D emptyObject;

    NonPresenceContainerNodeCodecContext(final DataContainerCodecPrototype<ContainerLikeRuntimeType<?, ?>> prototype) {
        super(prototype);
    }

    @NonNull D emptyObject() {
        final var local = (D) EMPTY_OBJECT.getAcquire(this);
        return local != null ? local : loadEmptyObject();
    }

    private @NonNull D loadEmptyObject() {
        final var local = createBindingProxy(
            Builders.containerBuilder().withNodeIdentifier(getDomPathArgument()).build());
        final var witness = (D) EMPTY_OBJECT.compareAndExchangeRelease(this, null, local);
        return witness != null ? witness : local;
    }
}
