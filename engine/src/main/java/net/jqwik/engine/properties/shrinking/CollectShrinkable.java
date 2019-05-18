package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;

public class CollectShrinkable<T> implements Shrinkable<List<T>> {
	private final List<T> value;
	private final List<Shrinkable<T>> elements;
	private final Predicate<List<T>> until;
	private final long randomSeed;

	public CollectShrinkable(List<Shrinkable<T>> elements, Predicate<List<T>> until, long randomSeed) {
		this.value = createValue(elements);
		this.elements = elements;
		this.until = until;
		this.randomSeed = randomSeed;
	}

	@Override
	public List<T> value() {
		return value;
	}

	private List<T> createValue(List<Shrinkable<T>> elements) {
		return elements
				   .stream()
				   .map(Shrinkable::value)
				   .collect(Collectors.toList());
	}

	@Override
	public ShrinkingSequence<List<T>> shrink(Falsifier<List<T>> falsifier) {
		return ShrinkingSequence.dontShrink(this);
	}

	@Override
	public ShrinkingDistance distance() {
		return ShrinkingDistance.of(0);
	}
}
