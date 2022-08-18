/*
 * Copyright (c) 2015, 2016 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.parser.stmt.reactor;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Verify.verifyNotNull;
import static java.util.Objects.requireNonNull;
import static org.opendaylight.yangtools.yang.parser.spi.meta.ModelProcessingPhase.EFFECTIVE_MODEL;
import static org.opendaylight.yangtools.yang.parser.spi.meta.ModelProcessingPhase.FULL_DECLARATION;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import org.eclipse.jdt.annotation.NonNull;
import org.opendaylight.yangtools.yang.model.api.meta.DeclaredStatement;
import org.opendaylight.yangtools.yang.model.api.meta.EffectiveStatement;
import org.opendaylight.yangtools.yang.parser.spi.meta.ModelActionBuilder;
import org.opendaylight.yangtools.yang.parser.spi.meta.ModelProcessingPhase;
import org.opendaylight.yangtools.yang.parser.spi.meta.NamespaceKeyCriterion;
import org.opendaylight.yangtools.yang.parser.spi.meta.ParserNamespace;
import org.opendaylight.yangtools.yang.parser.spi.meta.StmtContext;
import org.opendaylight.yangtools.yang.parser.spi.meta.StmtContext.Mutable;
import org.opendaylight.yangtools.yang.parser.stmt.reactor.StatementContextBase.ContextMutation;
import org.opendaylight.yangtools.yang.parser.stmt.reactor.StatementContextBase.OnNamespaceItemAdded;
import org.opendaylight.yangtools.yang.parser.stmt.reactor.StatementContextBase.OnPhaseFinished;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ModifierImpl implements ModelActionBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(ModifierImpl.class);

    private final Set<AbstractPrerequisite<?>> unsatisfied = new HashSet<>(1);
    private final Set<AbstractPrerequisite<?>> mutations = new HashSet<>(1);
    private final InferenceContext ctx = new InferenceContext() { };

    private List<Runnable> bootstraps;
    private InferenceAction action;
    private boolean actionApplied;

    private <D> AbstractPrerequisite<D> addReq(final AbstractPrerequisite<D> prereq) {
        LOG.trace("Modifier {} adding prerequisite {}", this, prereq);
        unsatisfied.add(prereq);
        return prereq;
    }

    private <T> @NonNull AbstractPrerequisite<T> addMutation(final @NonNull AbstractPrerequisite<T> mutation) {
        LOG.trace("Modifier {} adding mutation {}", this, mutation);
        mutations.add(mutation);
        return mutation;
    }

    private void checkNotRegistered() {
        checkState(action == null, "Action was already registered.");
    }

    private boolean removeSatisfied() {
        final Iterator<AbstractPrerequisite<?>> it = unsatisfied.iterator();
        while (it.hasNext()) {
            final AbstractPrerequisite<?> prereq = it.next();
            if (prereq.isDone()) {
                // We are removing current prerequisite from list.
                LOG.trace("Modifier {} prerequisite {} satisfied", this, prereq);
                it.remove();
            }
        }
        return unsatisfied.isEmpty();
    }

    boolean isApplied() {
        return actionApplied;
    }

    void failModifier() {
        removeSatisfied();
        action.prerequisiteFailed(unsatisfied);
        action = null;
    }

    private <K, C extends StmtContext<?, ?, ?>> @NonNull AbstractPrerequisite<C> requiresCtxImpl(
            final StmtContext<?, ?, ?> context, final ParserNamespace<K, C> namespace, final K key,
            final ModelProcessingPhase phase)  {
        checkNotRegistered();

        AddedToNamespace<C> addedToNs = new AddedToNamespace<>(phase);
        addReq(addedToNs);
        contextImpl(context).onNamespaceItemAddedAction(namespace, key, addedToNs);
        return addedToNs;
    }

    private <K, C extends StmtContext<?, ?, ?>> @NonNull AbstractPrerequisite<C> requiresCtxImpl(
            final StmtContext<?, ?, ?> context, final ParserNamespace<K, C> namespace,
            final NamespaceKeyCriterion<K> criterion, final ModelProcessingPhase phase)  {
        checkNotRegistered();

        AddedToNamespace<C> addedToNs = new AddedToNamespace<>(phase);
        addReq(addedToNs);
        contextImpl(context).onNamespaceItemAddedAction(namespace, phase, criterion, addedToNs);
        return addedToNs;
    }

    private <C extends StmtContext<?, ?, ?>> @NonNull AbstractPrerequisite<C> requiresCtxImpl(final C context,
            final ModelProcessingPhase phase) {
        checkNotRegistered();

        PhaseFinished<C> phaseFin = new PhaseFinished<>();
        addReq(phaseFin);
        addBootstrap(() -> contextImpl(context).addPhaseCompletedListener(phase, phaseFin));
        return phaseFin;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private <K, C extends Mutable<?, ?, ?>> AbstractPrerequisite<C> mutatesCtxImpl(final StmtContext<?, ?, ?> context,
            final ParserNamespace<K, ? extends StmtContext<?, ?, ?>> namespace, final K key,
            final ModelProcessingPhase phase) {
        checkNotRegistered();

        final PhaseModificationInNamespace<C> mod = new PhaseModificationInNamespace<>(EFFECTIVE_MODEL);
        addReq(mod);
        addMutation(mod);
        contextImpl(context).onNamespaceItemAddedAction((ParserNamespace) namespace, key, mod);
        return mod;
    }

    private static StatementContextBase<?, ?, ?> contextImpl(final Object value) {
        checkArgument(value instanceof StatementContextBase, "Supplied context %s is not provided by this reactor.",
            value);
        return StatementContextBase.class.cast(value);
    }

    boolean tryApply() {
        checkState(action != null, "Action was not defined yet.");

        if (removeSatisfied()) {
            if (!actionApplied) {
                action.apply(ctx);
                actionApplied = true;
            }
            return true;
        }
        return false;
    }

    @Override
    public <C extends Mutable<?, ?, ?>, T extends C> Prerequisite<C> mutatesCtx(final T context,
            final ModelProcessingPhase phase) {
        return addMutation(new PhaseMutation<>(contextImpl(context), phase));
    }

    @Override
    public <A, D extends DeclaredStatement<A>, E extends EffectiveStatement<A, D>>
            AbstractPrerequisite<StmtContext<A, D, E>> requiresCtx(final StmtContext<A, D, E> context,
                final ModelProcessingPhase phase) {
        return requiresCtxImpl(context, phase);
    }

    @Override
    public <K, C extends StmtContext<?, ?, ?>> Prerequisite<C> requiresCtx(final StmtContext<?, ?, ?> context,
            final ParserNamespace<K, C> namespace, final K key, final ModelProcessingPhase phase) {
        return requiresCtxImpl(context, namespace, key, phase);
    }

    @Override
    public <K, C extends StmtContext<?, ?, ?>> Prerequisite<C> requiresCtx(final StmtContext<?, ?, ?> context,
            final ParserNamespace<K, C> namespace, final NamespaceKeyCriterion<K> criterion,
            final ModelProcessingPhase phase) {
        return requiresCtxImpl(context, namespace, criterion, phase);
    }

    @Override
    public <K, E extends EffectiveStatement<?, ?>> Prerequisite<StmtContext<?, ?, E>> requiresCtxPath(
            final StmtContext<?, ?, ?> context, final ParserNamespace<K, ? extends StmtContext<?, ?, ?>> namespace,
            final Iterable<K> keys, final ModelProcessingPhase phase) {
        checkNotRegistered();

        final var ret = new PhaseRequirementInNamespacePath<StmtContext<?, ?, E>, K>(EFFECTIVE_MODEL, keys);
        addReq(ret);
        addBootstrap(() -> ret.hookOnto(context, namespace));
        return ret;
    }

    @Override
    public <D extends DeclaredStatement<?>> Prerequisite<D> requiresDeclared(
            final StmtContext<?, ? extends D, ?> context) {
        return requiresCtxImpl(context, FULL_DECLARATION).transform(StmtContext::declared);
    }

    @Override
    @Deprecated
    public <K, D extends DeclaredStatement<?>> Prerequisite<D> requiresDeclared(final StmtContext<?, ?, ?> context,
            final ParserNamespace<K, StmtContext<?, ? extends D, ?>> namespace, final K key) {
        return requiresCtxImpl(context, namespace, key, FULL_DECLARATION).transform(StmtContext::declared);
    }

    @Override
    @Deprecated
    public <K, C extends StmtContext<?, ?, ?>> AbstractPrerequisite<C> requiresDeclaredCtx(
            final StmtContext<?, ?, ?> context, final ParserNamespace<K, C> namespace, final K key) {
        return requiresCtxImpl(context, namespace, key, FULL_DECLARATION);
    }

    @Override
    @Deprecated
    public <E extends EffectiveStatement<?, ?>> Prerequisite<E> requiresEffective(
            final StmtContext<?, ?, ? extends E> stmt) {
        return requiresCtxImpl(stmt, EFFECTIVE_MODEL).transform(StmtContext::buildEffective);
    }

    @Override
    @Deprecated
    public <K, E extends EffectiveStatement<?, ?>> Prerequisite<E> requiresEffective(final StmtContext<?, ?, ?> context,
            final ParserNamespace<K, StmtContext<?, ?, ? extends E>> namespace, final K key) {
        return requiresCtxImpl(context, namespace, key, EFFECTIVE_MODEL).transform(StmtContext::buildEffective);
    }

    @Override
    @Deprecated
    public <K, C extends StmtContext<?, ?, ?>> AbstractPrerequisite<C> requiresEffectiveCtx(
            final StmtContext<?, ?, ?> context, final ParserNamespace<K, C> namespace, final K key) {
        return requiresCtxImpl(contextImpl(context), namespace, key, EFFECTIVE_MODEL);
    }

    @Override
    @Deprecated
    public Prerequisite<Mutable<?, ?, ?>> mutatesNs(final Mutable<?, ?, ?> context,
            final ParserNamespace<?, ?> namespace) {
        return addMutation(new NamespaceMutation(contextImpl(context), namespace));
    }

    @Override
    public <K, E extends EffectiveStatement<?, ?>> AbstractPrerequisite<Mutable<?, ?, E>> mutatesEffectiveCtx(
            final StmtContext<?, ?, ?> context, final ParserNamespace<K, ? extends StmtContext<?, ?, ?>> namespace,
            final K key) {
        return mutatesCtxImpl(context, namespace, key, EFFECTIVE_MODEL);
    }

    @Override
    public <K, E extends EffectiveStatement<?, ?>> AbstractPrerequisite<Mutable<?, ?, E>> mutatesEffectiveCtxPath(
            final StmtContext<?, ?, ?> context, final ParserNamespace<K, ? extends StmtContext<?, ?, ?>> namespace,
            final Iterable<K> keys) {
        checkNotRegistered();

        final var ret = new PhaseModificationInNamespacePath<Mutable<?, ?, E>, K>(EFFECTIVE_MODEL, keys);
        addReq(ret);
        addMutation(ret);
        addBootstrap(() -> ret.hookOnto(context, namespace));
        return ret;
    }

    @Override
    @SuppressWarnings("checkstyle:hiddenField")
    public void apply(final InferenceAction action) {
        checkState(this.action == null, "Action already defined to %s", this.action);
        this.action = requireNonNull(action);
        if (bootstraps != null) {
            bootstraps.forEach(Runnable::run);
            bootstraps = null;
        }
    }

    private void addBootstrap(final Runnable bootstrap) {
        if (bootstraps == null) {
            bootstraps = new ArrayList<>(1);
        }
        bootstraps.add(bootstrap);
    }

    private abstract class AbstractPrerequisite<T> implements Prerequisite<T> {
        private boolean done = false;
        private T value;

        @Override
        @SuppressWarnings("checkstyle:hiddenField")
        public final T resolve(final InferenceContext ctx) {
            checkState(done);
            checkArgument(ctx == ModifierImpl.this.ctx);
            return verifyNotNull(value, "Attempted to access unavailable prerequisite %s", this);
        }

        final boolean isDone() {
            return done;
        }

        @SuppressWarnings("checkstyle:hiddenField")
        final boolean resolvePrereq(final T value) {
            this.value = value;
            this.done = true;
            return isApplied();
        }

        final <O> @NonNull Prerequisite<O> transform(final Function<? super T, O> transformation) {
            return context -> transformation.apply(resolve(context));
        }

        @Override
        public final String toString() {
            return addToStringAttributes(MoreObjects.toStringHelper(this).omitNullValues()).toString();
        }

        ToStringHelper addToStringAttributes(final ToStringHelper toStringHelper) {
            return toStringHelper.add("value", value);
        }
    }

    private abstract class AbstractPathPrerequisite<C extends StmtContext<?, ?, ?>, K> extends AbstractPrerequisite<C>
            implements OnNamespaceItemAdded {
        private final ModelProcessingPhase modPhase;
        private final Iterable<K> keys;
        private final Iterator<K> it;

        AbstractPathPrerequisite(final ModelProcessingPhase phase, final Iterable<K> keys) {
            this.modPhase = requireNonNull(phase);
            this.keys = requireNonNull(keys);
            it = keys.iterator();
        }

        @Override
        public final void namespaceItemAdded(final StatementContextBase<?, ?, ?> context,
                final ParserNamespace<?, ?> namespace, final Object key, final Object value) {
            LOG.debug("Action for {} got key {}", keys, key);

            final StatementContextBase<?, ?, ?> target = contextImpl(value);
            if (!target.isSupportedByFeatures()) {
                LOG.debug("Key {} in {} is not supported", key, keys);
                resolvePrereq(null);
                action.prerequisiteUnavailable(this);
                return;
            }

            nextStep(modPhase, context, target);

            if (!it.hasNext()) {
                // Last step: we are done
                if (resolvePrereq((C) value)) {
                    tryApply();
                }
                return;
            }

            // Make sure target's storage notifies us when the next step becomes available.
            hookOnto(target, namespace, it.next());
        }

        abstract void nextStep(ModelProcessingPhase phase, StatementContextBase<?, ?, ?> current,
            StatementContextBase<?, ?, ?> next);

        @Override
        final ToStringHelper addToStringAttributes(final ToStringHelper toStringHelper) {
            return super.addToStringAttributes(toStringHelper).add("phase", modPhase).add("keys", keys);
        }

        final void hookOnto(final StmtContext<?, ?, ?> context, final ParserNamespace<?, ?> namespace) {
            checkArgument(it.hasNext(), "Namespace %s keys may not be empty", namespace);
            hookOnto(contextImpl(context), namespace, it.next());
        }

        @SuppressWarnings("unchecked")
        private void hookOnto(final StatementContextBase<?, ?, ?> context, final ParserNamespace<?, ?> namespace,
                final K key) {
            context.onNamespaceItemAddedAction((ParserNamespace) namespace, requireNonNull(key), this);
        }
    }

    private final class PhaseMutation<C> extends AbstractPrerequisite<C> implements ContextMutation {
        @SuppressWarnings("unchecked")
        PhaseMutation(final StatementContextBase<?, ?, ?> context, final ModelProcessingPhase phase) {
            context.addMutation(phase, this);
            resolvePrereq((C) context);
        }

        @Override
        public boolean isFinished() {
            return isApplied();
        }
    }

    private final class PhaseFinished<C extends StmtContext<?, ?, ?>> extends AbstractPrerequisite<C>
            implements OnPhaseFinished {
        @SuppressWarnings("unchecked")
        @Override
        public boolean phaseFinished(final StatementContextBase<?, ?, ?> context,
                final ModelProcessingPhase finishedPhase) {
            return resolvePrereq((C) context) || tryApply();
        }
    }

    private final class NamespaceMutation extends AbstractPrerequisite<Mutable<?, ?, ?>> {
        NamespaceMutation(final StatementContextBase<?, ?, ?> ctx, final ParserNamespace<?, ?> namespace) {
            resolvePrereq(ctx);
        }
    }

    private final class AddedToNamespace<C extends StmtContext<?, ?, ?>> extends AbstractPrerequisite<C>
            implements OnNamespaceItemAdded, OnPhaseFinished {
        private final ModelProcessingPhase phase;

        AddedToNamespace(final ModelProcessingPhase phase) {
            this.phase = requireNonNull(phase);
        }

        @Override
        public void namespaceItemAdded(final StatementContextBase<?, ?, ?> context,
                final ParserNamespace<?, ?> namespace, final Object key, final Object value) {
            ((StatementContextBase<?, ?, ?>) value).addPhaseCompletedListener(phase, this);
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean phaseFinished(final StatementContextBase<?, ?, ?> context,
                final ModelProcessingPhase finishedPhase) {
            return resolvePrereq((C) context) || tryApply();
        }

        @Override
        ToStringHelper addToStringAttributes(final ToStringHelper toStringHelper) {
            return super.addToStringAttributes(toStringHelper).add("phase", phase);
        }
    }

    private final class PhaseRequirementInNamespacePath<C extends StmtContext<?, ?, ?>, K>
            extends AbstractPathPrerequisite<C, K> {
        PhaseRequirementInNamespacePath(final ModelProcessingPhase phase, final Iterable<K> keys) {
            super(phase, keys);
        }

        @Override
        void nextStep(final ModelProcessingPhase phase, final StatementContextBase<?, ?, ?> current,
                final StatementContextBase<?, ?, ?> next) {
            // No-op
        }
    }

    private final class PhaseModificationInNamespace<C extends Mutable<?, ?, ?>> extends AbstractPrerequisite<C>
            implements OnNamespaceItemAdded, ContextMutation {
        private final ModelProcessingPhase modPhase;

        PhaseModificationInNamespace(final ModelProcessingPhase phase) {
            checkArgument(phase != null, "Model processing phase must not be null");
            this.modPhase = phase;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void namespaceItemAdded(final StatementContextBase<?, ?, ?> context,
                final ParserNamespace<?, ?> namespace, final Object key, final Object value) {
            StatementContextBase<?, ?, ?> targetCtx = contextImpl(value);
            targetCtx.addMutation(modPhase, this);
            resolvePrereq((C) targetCtx);
        }

        @Override
        public boolean isFinished() {
            return isApplied();
        }
    }

    /**
     * This similar to {@link PhaseModificationInNamespace}, but allows recursive descent until it finds the real
     * target. The mechanics is driven as a sequence of prerequisites along a path: first we hook onto namespace to
     * give us the first step. When it does, we hook onto the first item to provide us the second step and so on.
     */
    private final class PhaseModificationInNamespacePath<C extends Mutable<?, ?, ?>, K>
            extends AbstractPathPrerequisite<C, K> implements ContextMutation {
        PhaseModificationInNamespacePath(final ModelProcessingPhase phase, final Iterable<K> keys) {
            super(phase, keys);
        }

        @Override
        public boolean isFinished() {
            return isApplied();
        }

        @Override
        void nextStep(final ModelProcessingPhase phase, final StatementContextBase<?, ?, ?> current,
                final StatementContextBase<?, ?, ?> next) {
            // Hook onto target: we either have a modification of the target itself or one of its children.
            next.addMutation(phase, this);
            // We have completed the context -> target step, hence we are no longer directly blocking context from
            // making forward progress.
            current.removeMutation(phase, this);
        }
    }
}
