package com.example.domain;

import static java.util.Arrays.asList;

import java.util.Iterator;
import java.util.List;

public class TestIterable implements Iterable<Integer> {

	private List<Integer> delegate;

	public TestIterable(Integer...values) {
		this.delegate=asList(values);
	}

	public Iterator<Integer> iterator() {
		return this.delegate.iterator();
	}
}
