package de.exceptionflug.haunted.util;

import java.util.*;
import java.util.function.Supplier;

public final class FormatUtils {

	private FormatUtils() {
	}

	public static List<String> format(final Collection<String> in, final Supplier<String[]> replacer) {
		return format(in, createReplacementMap(replacer.get()));
	}

	public static List<String> format(final Collection<String> in, final String... replacements) {
		return format(in, createReplacementMap(replacements));
	}

	public static List<String> format(final Collection<String> in, final Map<String, String> replacements) {
		final List<String> out = new ArrayList<>();
		for (final String i : in) {
			out.add(format(i, replacements));
		}
		return out;
	}

	public static String format(final String in, final Supplier<String[]> replacer) {
		return format(in, createReplacementMap(replacer.get()));
	}

	public static String format(final String in, final String... replacements) {
		return format(in, createReplacementMap(replacements));
	}

	public static String format(String in, final Map<String, String> replacements) {
		for (final String placeholder : replacements.keySet()) {
			final String replacement = replacements.get(placeholder);
			in = in.replace(placeholder, replacement);
		}
		return in;
	}

	public static Map<String, String> createReplacementMap(final String... replacements) {
		final Map<String, String> out = new HashMap<>();
		final Iterator<String> iterator = Arrays.asList(replacements).iterator();
		while (iterator.hasNext()) {
			final String placeholder = iterator.next();
			if (!iterator.hasNext()) {
				throw new IllegalArgumentException("Odd replacement parameter count.");
			}
			final String replacement = iterator.next();
			out.put(placeholder, replacement);
		}
		return out;
	}

	public static String formatAmpersandColorCodes(final String in) {
		final char[] b = in.toCharArray();
		for (int i = 0; i < b.length - 1; ++i) {
			if (b[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
				b[i] = 167;
				b[i + 1] = Character.toLowerCase(b[i + 1]);
			}
		}
		return new String(b);
	}

	public static List<String> formatAmpersandColorCodes(final List<String> in) {
		final List<String> out = new ArrayList<>();
		for (final String i : in) {
			out.add(formatAmpersandColorCodes(i));
		}
		return out;
	}
}
