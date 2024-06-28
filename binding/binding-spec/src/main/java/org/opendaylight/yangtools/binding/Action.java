/*
 * Copyright (c) 2018 Pantheon Technologies, s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.binding;

import com.google.common.util.concurrent.ListenableFuture;
import edu.umd.cs.findbugs.annotations.CheckReturnValue;
import org.eclipse.jdt.annotation.NonNull;
import org.opendaylight.yangtools.yang.common.RpcResult;

/**
 * Interface extended by all interfaces generated for a YANG {@code action}.
 */
public non-sealed interface Action<P extends DataObjectIdentifier<?>, I extends RpcInput, O extends RpcOutput>
        extends BindingContract<Action<P, I, O>> {
    /**
     * Invoke the action.
     *
     * @param path Invocation path
     * @param input Input argument
     * @return Future result of invocation
     * @throws NullPointerException if any of the arguments are null
     */
    @CheckReturnValue
    @NonNull ListenableFuture<@NonNull RpcResult<@NonNull O>> invoke(@NonNull P path, @NonNull I input);
}
