/*
 * Copyright (c) 2021 PANTHEON.tech, s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.mdsal.binding.generator.impl.reactor;

import static com.google.common.base.Verify.verify;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.opendaylight.mdsal.binding.generator.impl.rt.DefaultChoiceRuntimeType;
import org.opendaylight.mdsal.binding.model.api.GeneratedType;
import org.opendaylight.mdsal.binding.model.api.Type;
import org.opendaylight.mdsal.binding.model.api.type.builder.GeneratedTypeBuilder;
import org.opendaylight.mdsal.binding.model.ri.BindingTypes;
import org.opendaylight.mdsal.binding.runtime.api.AugmentRuntimeType;
import org.opendaylight.mdsal.binding.runtime.api.CaseRuntimeType;
import org.opendaylight.mdsal.binding.runtime.api.ChoiceRuntimeType;
import org.opendaylight.mdsal.binding.runtime.api.RuntimeType;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.model.api.stmt.ChoiceEffectiveStatement;
import org.opendaylight.yangtools.yang.model.util.SchemaInferenceStack;

/**
 * Generator corresponding to a {@code choice} statement.
 */
final class ChoiceGenerator extends CompositeSchemaTreeGenerator<ChoiceEffectiveStatement, ChoiceRuntimeType> {
    static final class ChoiceBuilder extends CompositeRuntimeTypeBuilder<ChoiceEffectiveStatement, ChoiceRuntimeType> {
        private ImmutableList<CaseRuntimeType> augmentedCases;

        ChoiceBuilder(final ChoiceEffectiveStatement statement) {
            super(statement);
        }

        @Override
        CompositeRuntimeTypeBuilder<ChoiceEffectiveStatement, ChoiceRuntimeType> fillTypes(
                final ChildLookup lookup,
                final AbstractCompositeGenerator<ChoiceEffectiveStatement, ChoiceRuntimeType> generator) {
            fillAugmentedCases(lookup, generator.augments());
            return super.fillTypes(lookup, generator);
        }

        @Override
        boolean isAugmentedChild(final ChildLookup lookup, final QName qname) {
            for (var augmented : augmentedCases) {
                if (qname.equals(augmented.statement().argument())) {
                    return true;
                }
            }
            return super.isAugmentedChild(lookup, qname);
        }

        @Override
        ChoiceRuntimeType build(final GeneratedType type, final ChoiceEffectiveStatement statement,
                final List<RuntimeType> children, final List<AugmentRuntimeType> augments) {
            verify(augments.isEmpty(), "Unexpected augments %s", augments);
            children.addAll(augmentedCases);
            return new DefaultChoiceRuntimeType(type, statement, children);
        }

        private void fillAugmentedCases(final ChildLookup lookup, final List<AbstractAugmentGenerator> augments) {
            final var builder = ImmutableList.<CaseRuntimeType>builder();
            for (var augment : augments) {
                builder.addAll(augment.augmentedCasesIn(lookup, statement()));
            }
            augmentedCases = builder.build();
        }
    }

    ChoiceGenerator(final ChoiceEffectiveStatement statement, final AbstractCompositeGenerator<?, ?> parent) {
        super(statement, parent);
    }

    @Override
    void pushToInference(final SchemaInferenceStack dataTree) {
        // No-op
    }

    @Override
    GeneratedType createTypeImpl(final TypeBuilderFactory builderFactory) {
        final GeneratedTypeBuilder builder = builderFactory.newGeneratedTypeBuilder(typeName());
        builder.addImplementsType(BindingTypes.choiceIn(Type.of(getParent().typeName())));

        final ModuleGenerator module = currentModule();
        module.addQNameConstant(builder, localName());

        annotateDeprecatedIfNecessary(builder);
        builderFactory.addCodegenInformation(module, statement(), builder);
//      newType.setSchemaPath(schemaNode.getPath());
        builder.setModuleName(module.statement().argument().getLocalName());

        return builder.build();
    }

    @Override
    ChoiceBuilder createBuilder(final ChoiceEffectiveStatement statement) {
        return new ChoiceBuilder(statement);
    }
}
