/*
 * Copyright (c) 2021 PANTHEON.tech, s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.mdsal.binding.runtime.api;

import com.google.common.annotations.Beta;
import java.util.List;
import org.eclipse.jdt.annotation.NonNull;

/**
 * A {@link RuntimeType} which is also a {@link RuntimeTypeContainer}.
 */
@Beta
public interface CompositeRuntimeType extends GeneratedRuntimeType, RuntimeTypeContainer {
    /**
     * Return the {@link AugmentRuntimeType}s extending this type, matching the underlying {@link #statement()}.
     *
     * @return {@link AugmentRuntimeType}s extending this type.
     */
    @NonNull List<AugmentRuntimeType> augments();

    /**
     * Return the {@link AugmentRuntimeType}s extending extending a namesake of this type.
     *
     * @return {@link AugmentRuntimeType}s extending a namesake of this type.
     */
    @NonNull List<AugmentRuntimeType> mismatchedAugments();
}
