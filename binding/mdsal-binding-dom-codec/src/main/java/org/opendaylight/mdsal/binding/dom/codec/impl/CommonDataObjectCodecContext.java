/*
 * Copyright (c) 2023 PANTHEON.tech, s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.mdsal.binding.dom.codec.impl;

import org.opendaylight.mdsal.binding.dom.codec.api.CommonDataObjectCodecTreeNode;
import org.opendaylight.mdsal.binding.runtime.api.CompositeRuntimeType;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier.PathArgument;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier.NodeIdentifier;

/**
 * Base implementation of {@link CommonDataObjectCodecTreeNode}.
 */
abstract sealed class CommonDataObjectCodecContext<D extends DataObject, T extends CompositeRuntimeType>
        extends DataContainerCodecContext<D, T, CommonDataObjectCodecPrototype<T>>
        implements CommonDataObjectCodecTreeNode<D>
        permits AbstractDataObjectCodecContext, ChoiceCodecContext {
    CommonDataObjectCodecContext(final CommonDataObjectCodecPrototype<T> prototype) {
        super(prototype);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final Class<D> getBindingClass() {
        return Class.class.cast(prototype().javaClass());
    }

    @Override
    protected NodeIdentifier getDomPathArgument() {
        return prototype().getYangArg();
    }

    /**
     * Returns deserialized Binding Path Argument from YANG instance identifier.
     */
    protected PathArgument getBindingPathArgument(final YangInstanceIdentifier.PathArgument domArg) {
        return bindingArg();
    }

    protected final PathArgument bindingArg() {
        return prototype().getBindingArg();
    }
}
