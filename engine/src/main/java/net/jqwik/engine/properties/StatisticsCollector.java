package net.jqwik.engine.properties;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.junit.platform.engine.reporting.*;

public class StatisticsCollector {

	public static final String KEY_STATISTICS = "statistics";
	private static ThreadLocal<StatisticsCollector> collector = ThreadLocal.withInitial(StatisticsCollector::new);

	public static void clearAll() {
		collector.remove();
	}

	public static StatisticsCollector get() {
		return collector.get();
	}

	public static void report(Consumer<ReportEntry> reporter, String propertyName) {
		StatisticsCollector collector = get();
		if (collector.isEmpty())
			return;
		reporter.accept(collector.createReportEntry(propertyName));
	}

	private final Map<List<Object>, Integer> counts = new HashMap<>();

	private boolean isEmpty() {
		return counts.isEmpty();
	}

	public Map<List<Object>, Integer> getCounts() {
		return counts;
	}

	public ReportEntry createReportEntry(String propertyName) {
		StringBuilder statistics = new StringBuilder();
		int sum = counts.values().stream().mapToInt(aCount -> aCount).sum();
		List<StatisticsEntry> statisticsEntries =
			counts.entrySet().stream()
				  .sorted(this::compareStatisticsEntries)
				  .filter(entry -> !entry.getKey().equals(Collections.emptyList()))
				  .map(entry -> new StatisticsEntry(displayKey(entry.getKey()), entry.getValue() * 100.0 / sum))
				  .collect(Collectors.toList());
		int maxKeyLength = statisticsEntries.stream().mapToInt(entry -> entry.name.length()).max().orElse(0);
		boolean fullNumbersOnly = !statisticsEntries.stream().anyMatch(entry -> entry.percentage < 1);

		for (StatisticsEntry statsEntry : statisticsEntries) {
			statistics.append(formatEntry(statsEntry, maxKeyLength, fullNumbersOnly));
		}

		String keyStatistics = String.format("%s for [%s]", KEY_STATISTICS, propertyName);
		return ReportEntry.from(keyStatistics, statistics.toString());
	}

	private String formatEntry(StatisticsEntry statsEntry, int maxKeyLength, boolean fullNumbersOnly) {
		return String.format(
			"%n    %1$-" + maxKeyLength + "s : %2$s %%",
			statsEntry.name,
			displayPercentage(statsEntry.percentage, fullNumbersOnly)
		);
	}

	private int compareStatisticsEntries(Map.Entry<List<Object>, Integer> e1, Map.Entry<List<Object>, Integer> e2) {
		List<Object> k1 = e1.getKey();
		List<Object> k2 = e2.getKey();
		if (k1.size() != k2.size()) {
			return Integer.compare(k1.size(), k2.size());
		}
		return e2.getValue().compareTo(e1.getValue());
	}

	private String displayPercentage(double percentage, boolean fullNumbersOnly) {
		if (fullNumbersOnly)
			return String.valueOf(Math.round(percentage));
		return String.valueOf(Math.round(percentage * 100.0) / 100.0);
	}

	private String displayKey(List<Object> key) {
		return key.stream().map(Object::toString).collect(Collectors.joining(" "));
	}

	public void collect(Object... values) {
		List<Object> key = Collections.emptyList();
		if (values != null) {
			key = Arrays.stream(values) //
						.filter(Objects::nonNull) //
						.collect(Collectors.toList());
		}

		int count = counts.computeIfAbsent(key, any -> 0);
		counts.put(key, ++count);
	}

	static class StatisticsEntry {
		private final String name;
		private final double percentage;

		StatisticsEntry(String name, double percentage) {
			this.name = name;
			this.percentage = percentage;
		}
	}
}
