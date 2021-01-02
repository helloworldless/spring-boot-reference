package com.davidagood.springbootreference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

public class Path {

	private final List<String> parts;

	private final String delimiter;

	private Path(String delimiter, List<String> parts) {
		requireNonNull(delimiter);
		requireNonNull(parts);
		this.delimiter = delimiter;
		this.parts = parts;
	}

	public static Path split(String delimiter, String path) {
		return new Path(delimiter, Arrays.asList(path.split(Pattern.quote(delimiter))));
	}

	public static Path from(String delimiter, String... parts) {
		String[] copy = Arrays.copyOf(parts, parts.length);
		return new Path(delimiter, Arrays.asList(copy));
	}

	public List<String> getParts() {
		return new ArrayList<>(parts);
	}

	@Override
	public String toString() {
		return String.join(delimiter, parts);
	}

	public Path add(String part) {
		List<String> copy = new ArrayList<>(parts);
		copy.add(part);
		return new Path(delimiter, copy);
	}

}