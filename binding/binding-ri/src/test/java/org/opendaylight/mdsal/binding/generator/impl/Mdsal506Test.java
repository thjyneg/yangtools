/*
 * Copyright (c) 2019 PANTHEON.tech, s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.mdsal.binding.generator.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import org.junit.Test;
import org.opendaylight.yangtools.binding.model.api.GeneratedType;
import org.opendaylight.yangtools.yang.test.util.YangParserTestUtils;

public class Mdsal506Test extends AbstractOpaqueTest {
    @Test
    public void generateAnydataTest() {
        final List<GeneratedType> types = DefaultBindingGenerator.generateFor(
            YangParserTestUtils.parseYangResource("/mdsal506.yang"));
        assertNotNull(types);
        assertEquals(4, types.size());

        assertOpaqueNode(types, "mdsal506", ".lst", "TestAnydata");
        assertOpaqueNode(types, "mdsal506", ".lst", "TestAnyxml");
    }
}
