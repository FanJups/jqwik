package net.jqwik.discovery.specs;

import java.lang.reflect.AnnotatedElement;

public interface DiscoverySpec<T extends AnnotatedElement> {

	boolean shouldBeDiscovered(T candidate);

	boolean butSkippedOnExecution(T candidate);

	String skippingReason(T candidate);
}