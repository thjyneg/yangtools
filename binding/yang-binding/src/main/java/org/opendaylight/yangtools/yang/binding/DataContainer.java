/*
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.binding;

/**
 * Data Container - object contains structured data. Marker interface which must be implemented by all interfaces
 * generated for YANG:
 * <ul>
 *   <li>Rpc Input</li>
 *   <li>Output</li>
 *   <li>Notification</li>
 *   <li>Container</li>
 *   <li>List</li>
 *   <li>Case</li>
 * </ul>
 */
public sealed interface DataContainer extends BindingContract<DataContainer>
    permits BaseNotification, ChoiceIn, DataObject, OpaqueObject {

}
