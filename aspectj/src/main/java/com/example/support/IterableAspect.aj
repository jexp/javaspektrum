package com.example.support;

import java.util.ArrayList;
import java.util.Collection;

public aspect IterableAspect pertypewithin(Iterable+){
	
	public <T,R> Collection<R> Iterable.map(Fun1<T,R> fun) {
		Collection<R> result=new ArrayList<R>();
		for (T value : (Iterable<T>)this) {
			result.add(fun.apply(value));
		}
		return result;
	}
}