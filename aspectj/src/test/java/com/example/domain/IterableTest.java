package com.example.domain;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Ignore;
import org.junit.Test;

import com.example.support.Fun1;

public class IterableTest {

	@Test
	@Ignore
	public void testAddMapToIterable() {
		Collection<Integer> result=new TestIterable(1,2,3).map(new Fun1<Integer,Integer>() {
			public Integer apply(Integer param) {
				return param*param;
			}});
		assertEquals(asList(1,4,9),result);
	}
}
