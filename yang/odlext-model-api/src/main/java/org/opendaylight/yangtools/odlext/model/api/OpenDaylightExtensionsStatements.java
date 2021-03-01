/*
 * Copyright (c) 2017 Pantheon Technologies, s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.odlext.model.api;

import static java.util.Objects.requireNonNull;

import com.google.common.annotations.Beta;
import java.util.Optional;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.model.api.meta.ArgumentDefinition;
import org.opendaylight.yangtools.yang.model.api.meta.DeclaredStatement;
import org.opendaylight.yangtools.yang.model.api.meta.EffectiveStatement;
import org.opendaylight.yangtools.yang.model.api.meta.StatementDefinition;

@Beta
@NonNullByDefault
public enum OpenDaylightExtensionsStatements implements StatementDefinition {
    // FIXME: this extension is not present in yang-ext.yang as published by mdsal
    ANYXML_SCHEMA_LOCATION(QName.create(OpenDaylightExtensionsConstants.ORIGINAL_MODULE, "anyxml-schema-location"),
        "target-node", AnyxmlSchemaLocationStatement.class, AnyxmlSchemaLocationEffectiveStatement.class),
    // Binding codegen support
    AUGMENT_IDENTIFIER(QName.create(OpenDaylightExtensionsConstants.ORIGINAL_MODULE, "augment-identifier"),
        "identifier", AugmentIdentifierStatement.class, AugmentIdentifierEffectiveStatement.class),

    // Context-aware RPCs
    CONTEXT_INSTANCE(QName.create(OpenDaylightExtensionsConstants.ORIGINAL_MODULE, "context-instance"),
        "context-type", ContextInstanceStatement.class, ContextInstanceEffectiveStatement.class),
    CONTEXT_REFERENCE(QName.create(OpenDaylightExtensionsConstants.ORIGINAL_MODULE, "context-reference"),
        "context-type", ContextReferenceStatement.class, ContextReferenceEffectiveStatement.class),
    INSTANCE_TARGET(QName.create(OpenDaylightExtensionsConstants.ORIGINAL_MODULE, "instance-target"),
        "path", InstanceTargetStatement.class, InstanceTargetEffectiveStatement.class),
    RPC_CONTEXT_REFERENCE(QName.create(OpenDaylightExtensionsConstants.ORIGINAL_MODULE, "rpc-context-reference"),
        "context-type", RpcContextReferenceStatement.class, RpcContextReferenceEffectiveStatement.class);

    private final Class<? extends EffectiveStatement<?, ?>> effectiveRepresentation;
    private final Class<? extends DeclaredStatement<?>> declaredRepresentation;
    private final QName statementName;
    private final ArgumentDefinition argumentDef;

    OpenDaylightExtensionsStatements(final QName statementName, final String argumentName,
            final Class<? extends DeclaredStatement<?>> declaredRepresentation,
            final Class<? extends EffectiveStatement<?, ?>> effectiveRepresentation) {
        this.statementName = statementName.intern();
        this.argumentDef = ArgumentDefinition.of(QName.create(statementName, argumentName).intern(), false);
        this.declaredRepresentation = requireNonNull(declaredRepresentation);
        this.effectiveRepresentation = requireNonNull(effectiveRepresentation);
    }

    @Override
    public QName getStatementName() {
        return statementName;
    }

    @Override
    public Optional<ArgumentDefinition> getArgumentDefinition() {
        return Optional.of(argumentDef);
    }

    @Override
    public Class<? extends DeclaredStatement<?>> getDeclaredRepresentationClass() {
        return declaredRepresentation;
    }

    @Override
    public Class<? extends EffectiveStatement<?, ?>> getEffectiveRepresentationClass() {
        return effectiveRepresentation;
    }
}
