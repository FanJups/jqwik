package net.jqwik.api.arbitraries;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of Long and long values.
 */
@API(status = MAINTAINED, since = "1.0")
public interface LongArbitrary extends Arbitrary<Long> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated numbers.
	 */
	default LongArbitrary between(long min, long max) {
		return greaterOrEqual(min).lessOrEqual(max);
	}

	/**
	 * Set the allowed lower {@code min} (included) bounder of generated numbers.
	 */
	LongArbitrary greaterOrEqual(long min);

	/**
	 * Set the allowed upper {@code max} (included) bounder of generated numbers.
	 */
	LongArbitrary lessOrEqual(long max);
}
