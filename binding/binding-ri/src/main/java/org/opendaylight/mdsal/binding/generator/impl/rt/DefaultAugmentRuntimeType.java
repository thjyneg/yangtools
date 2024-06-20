/*
 * Copyright (c) 2021 PANTHEON.tech, s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.mdsal.binding.generator.impl.rt;

import java.util.List;
import org.opendaylight.mdsal.binding.runtime.api.AugmentRuntimeType;
import org.opendaylight.mdsal.binding.runtime.api.RuntimeType;
import org.opendaylight.yangtools.binding.model.api.GeneratedType;
import org.opendaylight.yangtools.yang.model.api.stmt.AugmentEffectiveStatement;

public final class DefaultAugmentRuntimeType extends AbstractCompositeRuntimeType<AugmentEffectiveStatement>
        implements AugmentRuntimeType {
    public DefaultAugmentRuntimeType(final GeneratedType bindingType, final AugmentEffectiveStatement statement,
            final List<RuntimeType> children) {
        super(bindingType, statement, children);
    }
}
