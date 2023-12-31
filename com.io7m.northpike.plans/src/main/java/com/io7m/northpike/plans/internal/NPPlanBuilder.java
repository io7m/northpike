/*
 * Copyright © 2023 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */


package com.io7m.northpike.plans.internal;

import com.io7m.northpike.model.NPCleanLater;
import com.io7m.northpike.model.NPCleanPolicyType;
import com.io7m.northpike.model.NPFailureFail;
import com.io7m.northpike.model.NPFailurePolicyType;
import com.io7m.northpike.model.NPToolReference;
import com.io7m.northpike.model.NPToolReferenceName;
import com.io7m.northpike.model.agents.NPAgentLabelName;
import com.io7m.northpike.model.agents.NPAgentResourceName;
import com.io7m.northpike.model.comparisons.NPComparisonSetType;
import com.io7m.northpike.model.plans.NPPlanBarrierBuilderType;
import com.io7m.northpike.model.plans.NPPlanBarrierType;
import com.io7m.northpike.model.plans.NPPlanBuilderType;
import com.io7m.northpike.model.plans.NPPlanDependency;
import com.io7m.northpike.model.plans.NPPlanElementBuilderType;
import com.io7m.northpike.model.plans.NPPlanElementName;
import com.io7m.northpike.model.plans.NPPlanElementType;
import com.io7m.northpike.model.plans.NPPlanException;
import com.io7m.northpike.model.plans.NPPlanIdentifier;
import com.io7m.northpike.model.plans.NPPlanTaskBuilderType;
import com.io7m.northpike.model.plans.NPPlanTaskType;
import com.io7m.northpike.model.plans.NPPlanTimeouts;
import com.io7m.northpike.model.plans.NPPlanToolExecution;
import com.io7m.northpike.model.plans.NPPlanType;
import com.io7m.northpike.strings.NPStrings;
import org.jgrapht.graph.AsUnmodifiableGraph;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.graph.GraphCycleProhibitedException;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import static com.io7m.northpike.plans.internal.NPPlanExceptions.errorCycle;
import static com.io7m.northpike.plans.internal.NPPlanExceptions.errorDuplicateElement;
import static com.io7m.northpike.plans.internal.NPPlanExceptions.errorDuplicateToolReference;
import static com.io7m.northpike.plans.internal.NPPlanExceptions.errorNonexistentElement;
import static com.io7m.northpike.plans.internal.NPPlanExceptions.errorNonexistentToolReference;
import static com.io7m.northpike.strings.NPStringConstants.BARRIER;
import static com.io7m.northpike.strings.NPStringConstants.TASK;

/**
 * A mutable plan builder.
 */

public final class NPPlanBuilder
  implements NPPlanBuilderType
{
  private final NPStrings strings;
  private final DirectedAcyclicGraph<NPPlanElementName, NPPlanTaskNameDependency> graph;
  private final HashMap<NPToolReferenceName, NPToolReference> toolReferences;
  private final HashMap<NPPlanElementName, NPPlanElementBuilder> elements;
  private final NPPlanIdentifier identifier;
  private NPPlanTimeouts timeouts;
  private String description;

  /**
   * A mutable plan builder.
   *
   * @param inStrings    The string resources
   * @param inIdentifier The plan identifier
   */

  public NPPlanBuilder(
    final NPStrings inStrings,
    final NPPlanIdentifier inIdentifier)
  {
    this.strings =
      Objects.requireNonNull(inStrings, "strings");
    this.identifier =
      Objects.requireNonNull(inIdentifier, "identifier");

    this.description =
      "";
    this.timeouts =
      NPPlanTimeouts.defaultTimeouts();
    this.graph =
      new DirectedAcyclicGraph<>(NPPlanTaskNameDependency.class);
    this.elements =
      new HashMap<>();
    this.toolReferences =
      new HashMap<>();
  }

  @Override
  public NPPlanBuilderType setDescription(
    final String newDescription)
    throws NPPlanException
  {
    this.description =
      Objects.requireNonNull(newDescription, "description");
    return this;
  }

  @Override
  public NPPlanType build()
    throws NPPlanException
  {
    final var imToolReferences =
      Map.copyOf(this.toolReferences);

    final var newElements = new HashMap<NPPlanElementName, NPPlanElementType>();
    for (final var elementBuilder : this.elements.values()) {
      newElements.put(
        elementBuilder.name(),
        elementBuilder.build(imToolReferences, newElements)
      );
    }

    final var newGraph =
      new DirectedAcyclicGraph<NPPlanElementType, NPPlanDependency>(
        NPPlanDependency.class);

    for (final var source : newElements.values()) {
      newGraph.addVertex(source);
      for (final var target : source.dependsOn()) {
        final var targetElement = newElements.get(target);
        newGraph.addVertex(targetElement);
        final var dependency = new NPPlanDependency(source, targetElement);
        newGraph.addEdge(source, targetElement, dependency);
      }
    }

    return new NPPlan(
      this.identifier,
      this.description,
      this.timeouts,
      imToolReferences,
      Map.copyOf(newElements),
      new AsUnmodifiableGraph<>(newGraph)
    );
  }

  @Override
  public NPPlanBuilderType setTimeouts(
    final NPPlanTimeouts inTimeouts)
  {
    this.timeouts = Objects.requireNonNull(inTimeouts, "timeouts");
    return this;
  }

  @Override
  public NPPlanBuilderType addToolReference(
    final NPToolReference reference)
    throws NPPlanException
  {
    Objects.requireNonNull(reference, "reference");

    if (this.toolReferences.containsKey(reference.referenceName())) {
      throw errorDuplicateToolReference(this.strings, reference);
    }

    this.toolReferences.put(reference.referenceName(), reference);
    return this;
  }

  @Override
  public NPPlanBarrierBuilderType addBarrier(
    final NPPlanElementName barrierName)
    throws NPPlanException
  {
    Objects.requireNonNull(barrierName, "name");

    if (this.elements.containsKey(barrierName)) {
      throw errorDuplicateElement(this.strings, barrierName, BARRIER);
    }

    final var builder = new NPPlanBarrierBuilder(this, barrierName);
    this.elements.put(barrierName, builder);
    this.graph.addVertex(barrierName);
    return builder;
  }

  @Override
  public NPPlanTaskBuilderType addTask(
    final NPPlanElementName taskName)
    throws NPPlanException
  {
    Objects.requireNonNull(taskName, "name");

    if (this.elements.containsKey(taskName)) {
      throw errorDuplicateElement(this.strings, taskName, TASK);
    }

    final var builder = new NPPlanTaskBuilder(this, taskName);
    this.elements.put(taskName, builder);
    this.graph.addVertex(taskName);
    return builder;
  }

  private static sealed abstract class NPPlanElementBuilder
    implements NPPlanElementBuilderType
  {
    private final NPPlanBuilder builder;
    private final NPPlanElementName name;
    private final TreeSet<NPPlanElementName> dependsOn;
    private String description;

    NPPlanElementBuilder(
      final NPPlanBuilder inBuilder,
      final NPPlanElementName inName)
    {
      this.builder = inBuilder;
      this.name = inName;
      this.description = "";
      this.dependsOn = new TreeSet<>();
    }

    protected final NPPlanBuilder builder()
    {
      return this.builder;
    }

    @Override
    public final NPPlanElementName name()
    {
      return this.name;
    }

    protected final TreeSet<NPPlanElementName> dependsOn()
    {
      return this.dependsOn;
    }

    protected final String description()
    {
      return this.description;
    }

    protected final void opSetDescription(
      final String newDescription)
    {
      this.description =
        Objects.requireNonNull(newDescription, "description");
    }

    protected final void opAddDependsOn(
      final NPPlanElementName target)
      throws NPPlanException
    {
      try {
        final var edge = new NPPlanTaskNameDependency(this.name, target);
        this.builder.graph.addEdge(this.name, target, edge);
        this.dependsOn.add(target);
      } catch (final IllegalArgumentException e) {
        if (e instanceof GraphCycleProhibitedException) {
          throw errorCycle(this.builder.strings, e, this.name, target);
        }
        if (Objects.equals(e.getMessage(), "loops not allowed")) {
          throw errorCycle(this.builder.strings, e, this.name, target);
        }
        throw errorNonexistentElement(
          this.builder.strings,
          e,
          this.name,
          target
        );
      }
    }

    abstract NPPlanElementType build(
      Map<NPToolReferenceName, NPToolReference> imToolReferences,
      HashMap<NPPlanElementName, NPPlanElementType> newElements)
      throws NPPlanException;
  }

  private static final class NPPlanBarrierBuilder
    extends NPPlanElementBuilder
    implements NPPlanBarrierBuilderType
  {
    NPPlanBarrierBuilder(
      final NPPlanBuilder inBuilder,
      final NPPlanElementName inName)
    {
      super(inBuilder, inName);
    }

    @Override
    NPPlanBarrier build(
      final Map<NPToolReferenceName, NPToolReference> toolReferences,
      final HashMap<NPPlanElementName, NPPlanElementType> newElements)
    {
      return new NPPlanBarrier(
        this.name(),
        List.copyOf(this.dependsOn()),
        this.description()
      );
    }

    @Override
    public NPPlanBarrierBuilderType setDescription(
      final String newDescription)
    {
      this.opSetDescription(newDescription);
      return this;
    }

    @Override
    public NPPlanBarrierBuilderType addDependsOn(
      final NPPlanElementName target)
      throws NPPlanException
    {
      this.opAddDependsOn(target);
      return this;
    }
  }

  private record NPPlanBarrier(
    NPPlanElementName name,
    List<NPPlanElementName> dependsOn,
    String description)
    implements NPPlanBarrierType
  {
    NPPlanBarrier
    {
      Objects.requireNonNull(name, "name");
      Objects.requireNonNull(description, "description");
      Objects.requireNonNull(dependsOn, "dependsOn");
    }
  }

  private static final class NPPlanTaskBuilder
    extends NPPlanElementBuilder
    implements NPPlanTaskBuilderType
  {
    private final TreeSet<NPAgentResourceName> lockAgentResources;
    private NPFailurePolicyType failurePolicy;
    private NPComparisonSetType<NPAgentLabelName> preferWithLabels;
    private NPComparisonSetType<NPAgentLabelName> requireWithLabels;
    private Optional<NPPlanToolExecution> toolExecution;
    private Optional<NPPlanElementName> sameAgentAs;
    private Optional<Duration> agentSelectionTimeout;
    private Optional<Duration> executionTimeout;
    private NPCleanPolicyType cleanPolicy;

    NPPlanTaskBuilder(
      final NPPlanBuilder inBuilder,
      final NPPlanElementName inName)
    {
      super(inBuilder, inName);
      this.preferWithLabels = new NPComparisonSetType.Anything<>();
      this.requireWithLabels = new NPComparisonSetType.Anything<>();
      this.lockAgentResources = new TreeSet<>();
      this.toolExecution = Optional.empty();
      this.sameAgentAs = Optional.empty();
      this.agentSelectionTimeout = Optional.empty();
      this.executionTimeout = Optional.empty();
      this.failurePolicy = NPFailureFail.FAIL;
      this.cleanPolicy = NPCleanLater.CLEAN_LATER;
    }

    @Override
    NPPlanTaskType build(
      final Map<NPToolReferenceName, NPToolReference> toolReferences,
      final HashMap<NPPlanElementName, NPPlanElementType> newElements)
      throws NPPlanException
    {
      final var sameAs =
        this.sameAgentAs
          .flatMap(name -> Optional.ofNullable(newElements.get(name)))
          .map(NPPlanTaskType.class::cast);

      if (this.toolExecution.isEmpty()) {
        throw NPPlanExceptions.errorNoToolExecution(
          this.builder().strings,
          this.name()
        );
      }

      return new NPPlanTask(
        this.name(),
        this.description(),
        List.copyOf(this.dependsOn()),
        this.requireWithLabels,
        this.preferWithLabels,
        Collections.unmodifiableSortedSet(
          new TreeSet<>(this.lockAgentResources)
        ),
        this.toolExecution.get(),
        sameAs,
        this.agentSelectionTimeout,
        this.executionTimeout,
        toolReferences,
        this.failurePolicy,
        this.cleanPolicy
      );
    }

    @Override
    public NPPlanTaskBuilderType addDependsOn(
      final NPPlanElementName target)
      throws NPPlanException
    {
      this.opAddDependsOn(target);
      return this;
    }

    @Override
    public NPPlanTaskBuilderType setDescription(
      final String newDescription)
    {
      this.opSetDescription(newDescription);
      return this;
    }

    @Override
    public NPPlanTaskBuilderType setAgentRequireWithLabels(
      final NPComparisonSetType<NPAgentLabelName> match)
    {
      this.requireWithLabels = Objects.requireNonNull(match, "match");
      return this;
    }

    @Override
    public NPPlanTaskBuilderType setAgentPreferWithLabels(
      final NPComparisonSetType<NPAgentLabelName> match)
    {
      this.preferWithLabels = Objects.requireNonNull(match, "match");
      return this;
    }

    @Override
    public NPPlanTaskBuilderType setAgentMustBeSameAs(
      final NPPlanElementName task)
      throws NPPlanException
    {
      this.sameAgentAs = Optional.of(task);
      this.addDependsOn(task);
      return this;
    }

    @Override
    public NPPlanTaskBuilderType addLockAgentResource(
      final NPAgentResourceName name)
    {
      this.lockAgentResources.add(Objects.requireNonNull(name, "name"));
      return this;
    }

    @Override
    public NPPlanTaskBuilderType setToolExecution(
      final NPPlanToolExecution newExecution)
      throws NPPlanException
    {
      Objects.requireNonNull(newExecution, "toolExecution");

      final var toolRefs =
        this.builder().toolReferences;

      if (!toolRefs.containsKey(newExecution.name())) {
        throw errorNonexistentToolReference(
          this.builder().strings,
          newExecution.name()
        );
      }

      for (final var ref : newExecution.toolRequirements()) {
        if (!toolRefs.containsKey(ref)) {
          throw errorNonexistentToolReference(this.builder().strings, ref);
        }
      }

      this.toolExecution = Optional.of(newExecution);
      return this;
    }

    @Override
    public NPPlanTaskBuilderType setAgentSelectionTimeout(
      final Duration duration)
    {
      Objects.requireNonNull(duration, "duration");
      this.agentSelectionTimeout = Optional.of(duration);
      return this;
    }

    @Override
    public NPPlanTaskBuilderType setExecutionTimeout(
      final Duration duration)
    {
      Objects.requireNonNull(duration, "duration");
      this.executionTimeout = Optional.of(duration);
      return this;
    }

    @Override
    public NPPlanTaskBuilderType setFailurePolicy(
      final NPFailurePolicyType newFailurePolicy)
    {
      this.failurePolicy =
        Objects.requireNonNull(newFailurePolicy, "failurePolicy");
      return this;
    }

    @Override
    public NPPlanTaskBuilderType setCleanPolicy(
      final NPCleanPolicyType newCleanPolicy)
    {
      this.cleanPolicy =
        Objects.requireNonNull(newCleanPolicy, "cleanPolicy");
      return this;
    }
  }

  private record NPPlanTask(
    NPPlanElementName name,
    String description,
    List<NPPlanElementName> dependsOn,
    NPComparisonSetType<NPAgentLabelName> agentRequireWithLabel,
    NPComparisonSetType<NPAgentLabelName> agentPreferWithLabel,
    SortedSet<NPAgentResourceName> lockAgentResources,
    NPPlanToolExecution toolExecution,
    Optional<NPPlanTaskType> agentMustBeSameAs,
    Optional<Duration> agentSelectionTimeout,
    Optional<Duration> executionTimeout,
    Map<NPToolReferenceName, NPToolReference> toolReferences,
    NPFailurePolicyType failurePolicy,
    NPCleanPolicyType cleanPolicy)
    implements NPPlanTaskType
  {
    NPPlanTask
    {
      Objects.requireNonNull(name, "name");
      Objects.requireNonNull(description, "description");
      Objects.requireNonNull(dependsOn, "dependsOn");
      Objects.requireNonNull(agentRequireWithLabel, "agentRequireWithLabel");
      Objects.requireNonNull(agentPreferWithLabel, "agentPreferWithLabel");
      Objects.requireNonNull(lockAgentResources, "lockAgentResources");
      Objects.requireNonNull(agentMustBeSameAs, "inAgentMustBeSameAs");
      Objects.requireNonNull(toolExecution, "toolExecution");
      Objects.requireNonNull(agentSelectionTimeout, "agentSelectionTimeout");
      Objects.requireNonNull(executionTimeout, "inExecutionTimeout");
      Objects.requireNonNull(failurePolicy, "failurePolicy");
      Objects.requireNonNull(cleanPolicy, "cleanPolicy");
    }

    @Override
    public Optional<NPToolReference> toolByReferenceName(
      final NPToolReferenceName refName)
    {
      Objects.requireNonNull(refName, "name");
      return Optional.ofNullable(this.toolReferences.get(refName));
    }
  }
}
