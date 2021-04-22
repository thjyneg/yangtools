/*
 * Copyright (c) 2020 PANTHEON.tech, s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang2sources.plugin;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import org.apache.maven.project.MavenProject;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.opendaylight.yangtools.plugin.generator.api.FileGenerator.ImportResolutionMode;
import org.opendaylight.yangtools.yang.parser.api.YangParserConfiguration;

@NonNullByDefault
abstract class GeneratorTaskFactory extends ParserConfigAware {
    private static final YangParserConfiguration SEMVER_CONFIG = YangParserConfiguration.builder()
        .importResolutionMode(org.opendaylight.yangtools.yang.parser.api.ImportResolutionMode.OPENCONFIG_SEMVER)
        .build();

    private final YangParserConfiguration parserConfig;

    GeneratorTaskFactory(final ImportResolutionMode importMode) {
        switch (importMode) {
            case REVISION_EXACT_OR_LATEST:
                parserConfig = YangParserConfiguration.DEFAULT;
                break;
            case SEMVER_LATEST:
                parserConfig = SEMVER_CONFIG;
                break;
            default:
                throw new LinkageError("Unhandled import mode " + importMode);
        }
    }

    @Override
    final YangParserConfiguration parserConfig() {
        return parserConfig;
    }

    final String generatorName() {
        return generator().getClass().getName();
    }

    /**
     * Create a new {@link GeneratorTask} which will work in scope of specified {@link MavenProject} with the effective
     * model held in specified {@link ContextHolder}.
     *
     * @param project current Maven Project
     * @param context model generation context
     */
    abstract GeneratorTask<?> createTask(MavenProject project, ContextHolder context);

    abstract Object generator();

    ToStringHelper addToStringProperties(final ToStringHelper helper) {
        return helper.add("generator", generatorName());
    }

    @Override
    public final String toString() {
        return addToStringProperties(MoreObjects.toStringHelper(this).omitNullValues()).toString();
    }
}
