/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.mdsal.binding.dom.codec.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.mdsal.test.binding.rev140701.GetTopOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.mdsal.test.binding.rev140701.GetTopOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.mdsal.test.binding.rev140701.PutTopInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.mdsal.test.binding.rev140701.PutTopInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.mdsal.test.binding.rev140701.two.level.list.TopLevelListBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.mdsal.test.binding.rev140701.two.level.list.TopLevelListKey;
import org.opendaylight.yangtools.binding.DataObject;
import org.opendaylight.yangtools.binding.util.BindingMap;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.data.api.schema.ContainerNode;
import org.opendaylight.yangtools.yang.model.api.stmt.SchemaNodeIdentifier.Absolute;

public class RpcDataSerializationTest extends AbstractBindingCodecTest {
    private static final QName PUT_TOP = QName.create(PutTopInput.QNAME, "put-top");
    private static final QName GET_TOP = QName.create(GetTopOutput.QNAME, "get-top");
    private static final Absolute PUT_TOP_INPUT = Absolute.of(PUT_TOP, PutTopInput.QNAME);
    private static final Absolute GET_TOP_OUTPUT = Absolute.of(GET_TOP, GetTopOutput.QNAME);

    private static final TopLevelListKey LIST_KEY = new TopLevelListKey("test");

    @Test
    public void testRpcInputToNormalized() {
        final PutTopInput bindingOriginal = new PutTopInputBuilder()
                .setTopLevelList(BindingMap.of(new TopLevelListBuilder().withKey(LIST_KEY).build()))
                .build();
        final ContainerNode dom = codecContext.toNormalizedNodeRpcData(bindingOriginal);
        assertNotNull(dom);
        assertEquals(PutTopInput.QNAME, dom.name().getNodeType());

        final DataObject bindingDeserialized = codecContext.fromNormalizedNodeRpcData(PUT_TOP_INPUT, dom);
        assertEquals(bindingOriginal, bindingDeserialized);
    }

    @Test
    public void testRpcOutputToNormalized() {
        final GetTopOutput bindingOriginal = new GetTopOutputBuilder()
                .setTopLevelList(BindingMap.of(new TopLevelListBuilder().withKey(LIST_KEY).build()))
                .build();
        final ContainerNode dom = codecContext.toNormalizedNodeRpcData(bindingOriginal);
        assertNotNull(dom);
        assertEquals(GetTopOutput.QNAME, dom.name().getNodeType());

        final DataObject bindingDeserialized = codecContext.fromNormalizedNodeRpcData(GET_TOP_OUTPUT, dom);
        assertEquals(bindingOriginal, bindingDeserialized);
    }
}
