package net.jqwik.properties.arbitraries;

import net.jqwik.*;
import net.jqwik.api.*;

import java.math.*;
import java.util.*;

class RandomNumberGenerators {

	// TODO: Create BigIntegers even if outside Long range
	static RandomGenerator<BigInteger> bigIntegers(BigInteger min, BigInteger max) {
		if (min.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) < 0)
			throw new JqwikException("Cannot create numbers < Long.MIN_VALUE");
		if (min.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0)
			throw new JqwikException("Cannot create numbers > Long.MAX_VALUE");

		if (min.equals(max)) {
			return ignored -> Shrinkable.unshrinkable(min);
		}

		if (isWithinIntegerRange(min, max)) {
			return createIntegerGenerator(min, max);
		} else {
			return createBigIntegerGenerator(min, max);
		}
	}

	private static RandomGenerator<BigInteger> createBigIntegerGenerator(BigInteger min, BigInteger max) {
		final long _min = Math.min(min.longValue(), max.longValue());
		final long _max = Math.max(min.longValue(), max.longValue());
		return random -> {
			final double d = random.nextDouble();
			long value = (long) ((d * _max) + ((1.0 - d) * _min) + d);
			return new ShrinkableValue<>(BigInteger.valueOf(value), new BigIntegerShrinkCandidates(min, max));
		};
	}

	private static RandomGenerator<BigInteger> createIntegerGenerator(BigInteger min, BigInteger max) {
		final int _min = Math.min(min.intValue(), max.intValue());
		final int _max = Math.max(min.intValue(), max.intValue());
		return random -> {
			int bound = Math.abs(_max - _min) + 1;
			int value = random.nextInt(bound >= 0 ? bound : Integer.MAX_VALUE) + _min;
			return new ShrinkableValue<>(BigInteger.valueOf(value), new BigIntegerShrinkCandidates(min, max));
		};
	}

	private static boolean isWithinIntegerRange(BigInteger min, BigInteger max) {
		return min.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) >= 0
			&& max.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0;
	}

	/**
	 * Random decimal are not equally distributed but randomly scaled down towards 0. Thus random decimals are more likely
	 * to be closer to 0 than
	 *
	 * @param random
	 *            source of randomness
	 * @param min
	 *            lower bound (included) of value to generate
	 * @param max
	 *            upper bound (included) of value to generate
	 * @param precision
	 *            The number of decimals to the right of decimal point
	 *
	 * @return a generated instance of BigDecimal
	 */
	static BigDecimal randomDecimal(Random random, BigDecimal min, BigDecimal max, int precision) {
		BigDecimal range = max.subtract(min);
		BigDecimal randomFactor = new BigDecimal(random.nextDouble());
		BigDecimal unscaledRandom = randomFactor.multiply(range).add(min);
		int digits = Math.max(1, unscaledRandom.precision() - unscaledRandom.scale());
		int randomScaleDown = random.nextInt(digits);
		BigDecimal scaledRandom = unscaledRandom.movePointLeft(randomScaleDown);
		return scaledRandom.setScale(precision, BigDecimal.ROUND_DOWN);
	}
}
