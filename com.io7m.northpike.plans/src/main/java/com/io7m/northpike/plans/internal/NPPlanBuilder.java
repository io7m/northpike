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

import com.io7m.lanark.core.RDottedName;
import com.io7m.northpike.model.NPAgentLabelMatchType;
import com.io7m.northpike.model.NPToolReference;
import com.io7m.northpike.plans.NPPlanBarrierBuilderType;
import com.io7m.northpike.plans.NPPlanBarrierType;
import com.io7m.northpike.plans.NPPlanBuilderType;
import com.io7m.northpike.plans.NPPlanDependency;
import com.io7m.northpike.plans.NPPlanElementBuilderType;
import com.io7m.northpike.plans.NPPlanElementName;
import com.io7m.northpike.plans.NPPlanElementType;
import com.io7m.northpike.plans.NPPlanException;
import com.io7m.northpike.plans.NPPlanName;
import com.io7m.northpike.plans.NPPlanTaskBuilderType;
import com.io7m.northpike.plans.NPPlanTaskType;
import com.io7m.northpike.plans.NPPlanToolExecution;
import com.io7m.northpike.plans.NPPlanType;
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
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static com.io7m.northpike.model.NPAgentLabelMatchType.AnyLabel.ANY_LABEL;
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
  private final NPPlanName name;
  private final long version;
  private final DirectedAcyclicGraph<NPPlanElementName, NPPlanTaskNameDependency> graph;
  private final HashMap<RDottedName, NPToolReference> toolReferences;
  private final HashMap<NPPlanElementName, NPPlanElementBuilder> elements;

  /**
   * A mutable plan builder.
   *
   * @param inStrings The string resources
   * @param inName    The plan name
   * @param inVersion The plan version
   */

  public NPPlanBuilder(
    final NPStrings inStrings,
    final NPPlanName inName,
    final long inVersion)
  {
    this.strings =
      Objects.requireNonNull(inStrings, "strings");
    this.name =
      Objects.requireNonNull(inName, "name");
    this.version =
      inVersion;
    this.graph =
      new DirectedAcyclicGraph<>(NPPlanTaskNameDependency.class);
    this.elements =
      new HashMap<>();
    this.toolReferences =
      new HashMap<>();
  }

  @Override
  public NPPlanType build()
    throws NPPlanException
  {
    final var newElements = new HashMap<NPPlanElementName, NPPlanElementType>();
    for (final var elementBuilder : this.elements.values()) {
      newElements.put(elementBuilder.name(), elementBuilder.build(newElements));
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
      this.name,
      this.version,
      Map.copyOf(this.toolReferences),
      Map.copyOf(newElements),
      new AsUnmodifiableGraph<>(newGraph)
    );
  }

  @Override
  public NPPlanBuilderType addToolReference(
    final NPToolReference reference)
    throws NPPlanException
  {
    Objects.requireNonNull(reference, "reference");

    if (this.toolReferences.containsKey(reference.name())) {
      throw errorDuplicateToolReference(this.strings, reference);
    }

    this.toolReferences.put(reference.name(), reference);
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

    protected final void onAddDependsOn(
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
          target);
      }
    }

    abstract NPPlanElementType build(HashMap<NPPlanElementName, NPPlanElementType> newElements)
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
      final HashMap<NPPlanElementName, NPPlanElementType> newElements)
    {
      return new NPPlanBarrier(
        this.name(),
        this.description(),
        List.copyOf(this.dependsOn())
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
      this.onAddDependsOn(target);
      return this;
    }
  }

  private static final class NPPlanBarrier implements NPPlanBarrierType
  {
    private final NPPlanElementName name;
    private final List<NPPlanElementName> dependsOn;
    private final String description;

    NPPlanBarrier(
      final NPPlanElementName inName,
      final String inDescription,
      final List<NPPlanElementName> inDependsOn)
    {
      this.name =
        Objects.requireNonNull(inName, "name");
      this.description =
        Objects.requireNonNull(inDescription, "inDescription");
      this.dependsOn =
        Objects.requireNonNull(inDependsOn, "rDottedNames");
    }

    @Override
    public NPPlanElementName name()
    {
      return this.name;
    }

    @Override
    public String description()
    {
      return this.description;
    }

    @Override
    public List<NPPlanElementName> dependsOn()
    {
      return this.dependsOn;
    }
  }

  private static final class NPPlanTaskBuilder
    extends NPPlanElementBuilder
    implements NPPlanTaskBuilderType
  {
    private final TreeSet<RDottedName> lockAgentResources;
    private NPAgentLabelMatchType preferWithLabels;
    private NPAgentLabelMatchType requireWithLabels;
    private Optional<NPPlanToolExecution> toolExecution;
    private Optional<NPPlanElementName> sameAgentAs;
    private Optional<Duration> agentAssignmentTimeout;
    private Optional<Duration> executionTimeout;

    NPPlanTaskBuilder(
      final NPPlanBuilder inBuilder,
      final NPPlanElementName inName)
    {
      super(inBuilder, inName);
      this.preferWithLabels = ANY_LABEL;
      this.requireWithLabels = ANY_LABEL;
      this.lockAgentResources = new TreeSet<>();
      this.toolExecution = Optional.empty();
      this.sameAgentAs = Optional.empty();
      this.agentAssignmentTimeout = Optional.empty();
      this.executionTimeout = Optional.empty();
    }

    @Override
    NPPlanTaskType build(
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
        sameAs,
        this.toolExecution.get(),
        this.agentAssignmentTimeout,
        this.executionTimeout
      );
    }

    @Override
    public NPPlanTaskBuilderType addDependsOn(
      final NPPlanElementName target)
      throws NPPlanException
    {
      this.onAddDependsOn(target);
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
      final NPAgentLabelMatchType match)
    {
      this.requireWithLabels = Objects.requireNonNull(match, "match");
      return this;
    }

    @Override
    public NPPlanTaskBuilderType setAgentPreferWithLabels(
      final NPAgentLabelMatchType match)
    {
      this.preferWithLabels = Objects.requireNonNull(match, "match");
      return this;
    }

    @Override
    public NPPlanTaskBuilderType setAgentMustBeSameAs(
      final NPPlanTaskBuilderType task)
      throws NPPlanException
    {
      this.sameAgentAs = Optional.of(task.name());
      this.addDependsOn(task.name());
      return this;
    }

    @Override
    public NPPlanTaskBuilderType addLockAgentResource(
      final RDottedName name)
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
    public NPPlanTaskBuilderType setAgentAssignmentTimeout(
      final Duration duration)
    {
      Objects.requireNonNull(duration, "duration");
      this.agentAssignmentTimeout = Optional.of(duration);
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
  }

  private static final class NPPlanTask implements NPPlanTaskType
  {
    private final NPPlanElementName name;
    private final String description;
    private final List<NPPlanElementName> dependsOn;
    private final NPAgentLabelMatchType agentRequireWithLabel;
    private final NPAgentLabelMatchType agentPreferWithLabel;
    private final SortedSet<RDottedName> lockAgentResources;
    private final NPPlanToolExecution toolExecution;
    private final Optional<NPPlanTaskType> agentMustBeSameAs;
    private final Optional<Duration> agentAssignmentTimeout;
    private final Optional<Duration> executionTimeout;

    NPPlanTask(
      final NPPlanElementName inName,
      final String inDescription,
      final List<NPPlanElementName> inDependsOn,
      final NPAgentLabelMatchType inAgentRequireWithLabel,
      final NPAgentLabelMatchType inAgentPreferWithLabel,
      final SortedSet<RDottedName> inLockAgentResources,
      final Optional<NPPlanTaskType> inAgentMustBeSameAs,
      final NPPlanToolExecution inToolExecution,
      final Optional<Duration> inAgentAssignmentTimeout,
      final Optional<Duration> inExecutionTimeout)
    {
      this.name =
        Objects.requireNonNull(inName, "name");
      this.description =
        Objects.requireNonNull(inDescription, "description");
      this.dependsOn =
        Objects.requireNonNull(inDependsOn, "dependsOn");
      this.agentRequireWithLabel =
        Objects.requireNonNull(
          inAgentRequireWithLabel, "agentRequireWithLabel");
      this.agentPreferWithLabel =
        Objects.requireNonNull(
          inAgentPreferWithLabel, "agentPreferWithLabel");
      this.lockAgentResources =
        Objects.requireNonNull(inLockAgentResources, "lockAgentResources");
      this.agentMustBeSameAs =
        Objects.requireNonNull(inAgentMustBeSameAs, "inAgentMustBeSameAs");
      this.toolExecution =
        Objects.requireNonNull(inToolExecution, "toolExecution");
      this.agentAssignmentTimeout =
        Objects.requireNonNull(
          inAgentAssignmentTimeout, "agentAssignmentTimeout");
      this.executionTimeout =
        Objects.requireNonNull(inExecutionTimeout, "inExecutionTimeout");
    }

    @Override
    public NPPlanElementName name()
    {
      return this.name;
    }

    @Override
    public String description()
    {
      return this.description;
    }

    @Override
    public List<NPPlanElementName> dependsOn()
    {
      return this.dependsOn;
    }

    @Override
    public NPAgentLabelMatchType agentRequireWithLabel()
    {
      return this.agentRequireWithLabel;
    }

    @Override
    public NPAgentLabelMatchType agentPreferWithLabel()
    {
      return this.agentPreferWithLabel;
    }

    @Override
    public Optional<NPPlanTaskType> agentMustBeSameAs()
    {
      return this.agentMustBeSameAs;
    }

    @Override
    public Optional<Duration> agentAssignmentTimeout()
    {
      return this.agentAssignmentTimeout;
    }

    @Override
    public Optional<Duration> executionTimeout()
    {
      return this.executionTimeout;
    }

    @Override
    public Set<RDottedName> lockAgentResources()
    {
      return this.lockAgentResources;
    }

    @Override
    public NPPlanToolExecution toolExecution()
    {
      return this.toolExecution;
    }
  }
}
