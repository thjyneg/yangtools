/*
 * Copyright (c) 2016 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.stmt;

import static org.junit.Assert.assertEquals;

import com.google.common.io.Files;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.common.YangConstants;
import org.opendaylight.yangtools.yang.model.api.DataNodeContainer;
import org.opendaylight.yangtools.yang.model.api.DataSchemaNode;
import org.opendaylight.yangtools.yang.model.api.EffectiveModelContext;
import org.opendaylight.yangtools.yang.model.api.Module;
import org.opendaylight.yangtools.yang.model.api.ModuleImport;
import org.opendaylight.yangtools.yang.model.api.ModuleLike;
import org.opendaylight.yangtools.yang.model.api.SchemaContext;
import org.opendaylight.yangtools.yang.model.api.SchemaNode;
import org.opendaylight.yangtools.yang.model.api.SchemaPath;
import org.opendaylight.yangtools.yang.model.api.Submodule;
import org.opendaylight.yangtools.yang.model.repo.api.SourceIdentifier;
import org.opendaylight.yangtools.yang.model.repo.api.YangTextSchemaSource;
import org.opendaylight.yangtools.yang.model.repo.api.YinTextSchemaSource;
import org.opendaylight.yangtools.yang.parser.api.YangParserConfiguration;
import org.opendaylight.yangtools.yang.parser.api.YangSyntaxErrorException;
import org.opendaylight.yangtools.yang.parser.rfc7950.reactor.RFC7950Reactors;
import org.opendaylight.yangtools.yang.parser.rfc7950.repo.YangStatementStreamSource;
import org.opendaylight.yangtools.yang.parser.rfc7950.repo.YinStatementStreamSource;
import org.opendaylight.yangtools.yang.parser.rfc7950.repo.YinTextToDomTransformer;
import org.opendaylight.yangtools.yang.parser.spi.meta.ReactorException;
import org.opendaylight.yangtools.yang.parser.spi.source.StatementStreamSource;
import org.opendaylight.yangtools.yang.parser.stmt.reactor.CrossSourceStatementReactor.BuildAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public final class StmtTestUtils {

    public static final FileFilter YANG_FILE_FILTER =
        file -> file.getName().endsWith(YangConstants.RFC6020_YANG_FILE_EXTENSION) && file.isFile();

    public static final FileFilter YIN_FILE_FILTER =
        file -> file.getName().endsWith(YangConstants.RFC6020_YIN_FILE_EXTENSION) && file.isFile();

    private static final Logger LOG = LoggerFactory.getLogger(StmtTestUtils.class);

    private StmtTestUtils() {

    }

    public static void log(final Throwable exception, final String indent) {
        LOG.debug("{}{}", indent, exception.getMessage());

        final Throwable[] suppressed = exception.getSuppressed();
        for (final Throwable throwable : suppressed) {
            log(throwable, indent + "        ");
        }
    }

    public static List<Module> findModules(final Collection<? extends Module> modules, final String moduleName) {
        final List<Module> result = new ArrayList<>();
        for (final Module module : modules) {
            if (module.getName().equals(moduleName)) {
                result.add(module);
            }
        }
        return result;
    }

    public static YangStatementStreamSource sourceForResource(final String resourceName) {
        try {
            return YangStatementStreamSource.create(YangTextSchemaSource.forFile(new File(
                StmtTestUtils.class.getResource(resourceName).toURI())));
        } catch (IOException | YangSyntaxErrorException | URISyntaxException e) {
            throw new IllegalArgumentException("Failed to create source", e);
        }
    }

    public static void printReferences(final ModuleLike module, final boolean isSubmodule, final String indent) {
        LOG.debug("{}{} {}", indent, isSubmodule ? "Submodule" : "Module", module.getName());
        for (final Submodule submodule : module.getSubmodules()) {
            printReferences(submodule, true, indent + "      ");
            printChilds(submodule.getChildNodes(), indent + "            ");
        }
    }

    public static void printChilds(final Collection<? extends DataSchemaNode> childNodes, final String indent) {

        for (final DataSchemaNode child : childNodes) {
            LOG.debug("{}{} {}", indent, "Child", child.getQName().getLocalName());
            if (child instanceof DataNodeContainer) {
                printChilds(((DataNodeContainer) child).getChildNodes(), indent + "      ");
            }
        }
    }

    public static EffectiveModelContext parseYangSource(final String yangSourcePath) throws ReactorException,
            URISyntaxException, IOException, YangSyntaxErrorException {
        return parseYangSource(yangSourcePath, YangParserConfiguration.DEFAULT, null);
    }

    public static EffectiveModelContext parseYangSource(final String yangSourcePath, final Set<QName> supportedFeatures)
            throws ReactorException, URISyntaxException, IOException, YangSyntaxErrorException {
        return parseYangSource(yangSourcePath, YangParserConfiguration.DEFAULT, supportedFeatures);
    }

    public static EffectiveModelContext parseYangSource(final String yangSourcePath,
            final YangParserConfiguration config, final Set<QName> supportedFeatures)
                    throws ReactorException, URISyntaxException, IOException, YangSyntaxErrorException {
        final URL source = StmtTestUtils.class.getResource(yangSourcePath);
        final File sourceFile = new File(source.toURI());
        return parseYangSources(config, supportedFeatures, sourceFile);
    }

    public static EffectiveModelContext parseYangSources(final StatementStreamSource... sources)
            throws ReactorException {
        return parseYangSources(YangParserConfiguration.DEFAULT, null, sources);
    }

    public static EffectiveModelContext parseYangSources(final YangParserConfiguration config,
            final Set<QName> supportedFeatures, final StatementStreamSource... sources) throws ReactorException {
        return parseYangSources(config, supportedFeatures, Arrays.asList(sources));
    }

    public static EffectiveModelContext parseYangSources(final YangParserConfiguration config,
            final Set<QName> supportedFeatures, final Collection<? extends StatementStreamSource> sources)
            throws ReactorException {
        final BuildAction reactor = RFC7950Reactors.defaultReactor().newBuild(config.importResolutionMode())
                .addSources(sources);
        if (supportedFeatures != null) {
            reactor.setSupportedFeatures(supportedFeatures);
        }

        return reactor.buildEffective();
    }

    public static EffectiveModelContext parseYangSources(final File... files) throws ReactorException, IOException,
            YangSyntaxErrorException {
        return parseYangSources(YangParserConfiguration.DEFAULT, null, files);
    }

    public static EffectiveModelContext parseYangSources(final YangParserConfiguration config,
            final Set<QName> supportedFeatures, final File... files) throws  ReactorException, IOException,
            YangSyntaxErrorException {

        final Collection<YangStatementStreamSource> sources = new ArrayList<>(files.length);
        for (File file : files) {
            sources.add(YangStatementStreamSource.create(YangTextSchemaSource.forFile(file)));
        }

        return parseYangSources(config, supportedFeatures, sources);
    }

    public static EffectiveModelContext parseYangSources(final Collection<File> files) throws ReactorException,
            IOException, YangSyntaxErrorException {
        return parseYangSources(files, YangParserConfiguration.DEFAULT);
    }

    public static EffectiveModelContext parseYangSources(final Collection<File> files,
            final YangParserConfiguration config) throws ReactorException, IOException, YangSyntaxErrorException {
        return parseYangSources(config, null, files.toArray(new File[files.size()]));
    }

    public static EffectiveModelContext parseYangSources(final String yangSourcesDirectoryPath)
            throws ReactorException, URISyntaxException, IOException, YangSyntaxErrorException {
        return parseYangSources(yangSourcesDirectoryPath, YangParserConfiguration.DEFAULT);
    }

    public static EffectiveModelContext parseYangSources(final String yangSourcesDirectoryPath,
            final YangParserConfiguration config) throws ReactorException, URISyntaxException, IOException,
            YangSyntaxErrorException {
        return parseYangSources(yangSourcesDirectoryPath, null, config);
    }

    public static EffectiveModelContext parseYangSources(final String yangSourcesDirectoryPath,
            final Set<QName> supportedFeatures, final YangParserConfiguration config) throws ReactorException,
            URISyntaxException, IOException, YangSyntaxErrorException {

        final URL resourceDir = StmtTestUtils.class.getResource(yangSourcesDirectoryPath);
        final File testSourcesDir = new File(resourceDir.toURI());

        return parseYangSources(config, supportedFeatures, testSourcesDir.listFiles(YANG_FILE_FILTER));
    }

    public static EffectiveModelContext parseYangSources(final String yangFilesDirectoryPath,
            final String yangLibsDirectoryPath)
            throws URISyntaxException, ReactorException, IOException, YangSyntaxErrorException {
        return parseYangSources(yangFilesDirectoryPath, yangLibsDirectoryPath, null);
    }

    public static EffectiveModelContext parseYangSources(final String yangFilesDirectoryPath,
            final String yangLibsDirectoryPath, final Set<QName> supportedFeatures) throws URISyntaxException,
            ReactorException, IOException, YangSyntaxErrorException {
        final File yangsDir = new File(StmtTestUtils.class.getResource(yangFilesDirectoryPath).toURI());
        final File libsDir = new File(StmtTestUtils.class.getResource(yangLibsDirectoryPath).toURI());

        return parseYangSources(yangsDir.listFiles(YANG_FILE_FILTER), libsDir.listFiles(YANG_FILE_FILTER),
                supportedFeatures);
    }

    private static EffectiveModelContext parseYangSources(final File[] yangFiles, final File[] libFiles,
            final Set<QName> supportedFeatures) throws ReactorException, IOException, YangSyntaxErrorException {
        final StatementStreamSource[] yangSources = new StatementStreamSource[yangFiles.length];
        for (int i = 0; i < yangFiles.length; i++) {
            yangSources[i] = YangStatementStreamSource.create(YangTextSchemaSource.forFile(yangFiles[i]));
        }

        final StatementStreamSource[] libSources = new StatementStreamSource[libFiles.length];
        for (int i = 0; i < libFiles.length; i++) {
            libSources[i] = YangStatementStreamSource.create(YangTextSchemaSource.forFile(libFiles[i]));
        }

        return parseYangSources(yangSources, libSources, supportedFeatures);
    }

    private static EffectiveModelContext parseYangSources(final StatementStreamSource[] yangSources,
            final StatementStreamSource[] libSources, final Set<QName> supportedFeatures) throws ReactorException {

        final BuildAction reactor = RFC7950Reactors.defaultReactor().newBuild()
                .addSources(yangSources).addLibSources(libSources);
        if (supportedFeatures != null) {
            reactor.setSupportedFeatures(supportedFeatures);
        }

        return reactor.buildEffective();
    }

    public static EffectiveModelContext parseYinSources(final String yinSourcesDirectoryPath)
            throws URISyntaxException, SAXException, IOException, ReactorException {
        return parseYinSources(yinSourcesDirectoryPath, YangParserConfiguration.DEFAULT);
    }

    public static EffectiveModelContext parseYinSources(final String yinSourcesDirectoryPath,
            final YangParserConfiguration config) throws URISyntaxException, SAXException, IOException,
            ReactorException {
        final URL resourceDir = StmtTestUtils.class.getResource(yinSourcesDirectoryPath);
        final File[] files = new File(resourceDir.toURI()).listFiles(YIN_FILE_FILTER);
        final StatementStreamSource[] sources = new StatementStreamSource[files.length];
        for (int i = 0; i < files.length; i++) {
            final SourceIdentifier identifier = YinTextSchemaSource.identifierFromFilename(files[i].getName());

            sources[i] = YinStatementStreamSource.create(YinTextToDomTransformer.transformSource(
                YinTextSchemaSource.delegateForByteSource(identifier, Files.asByteSource(files[i]))));
        }

        return parseYinSources(config, sources);
    }

    public static EffectiveModelContext parseYinSources(final YangParserConfiguration config,
            final StatementStreamSource... sources) throws ReactorException {
        return RFC7950Reactors.defaultReactor()
            .newBuild(config.importResolutionMode())
            .addSources(sources)
            .buildEffective();
    }

    public static Module findImportedModule(final SchemaContext context, final Module rootModule,
            final String importedModuleName) {
        ModuleImport requestedModuleImport = null;
        for (final ModuleImport moduleImport : rootModule.getImports()) {
            if (moduleImport.getModuleName().equals(importedModuleName)) {
                requestedModuleImport = moduleImport;
                break;
            }
        }

        return context.findModule(requestedModuleImport.getModuleName(), requestedModuleImport.getRevision())
                .orElse(null);
    }

    /**
     * Assertion that a {@link SchemaNode} reports expected path. This method deals with {@link SchemaNode#getPath()}
     * being unavailable by comparing {@link SchemaNode#getQName()} to {@link SchemaPath#getLastComponent()}.
     *
     * @param expected Expected
     * @param node Node to examine
     * @throws AssertionError if the
     */
    public static void assertPathEquals(final SchemaPath expected, final SchemaNode node) {
        final SchemaPath actual;
        try {
            actual = node.getPath();
        } catch (UnsupportedOperationException e) {
            LOG.trace("Node {} does not support getPath()", node, e);
            assertEquals(expected.getLastComponent(), node.getQName());
            return;
        }
        assertEquals(expected, actual);
    }
}
