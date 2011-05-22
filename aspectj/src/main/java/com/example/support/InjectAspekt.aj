package com.example.support;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.reflect.ConstructorSignature;

public privileged aspect InjectAspekt {

	private final Map<Class, Object> context = new HashMap<Class, Object>();

	before(Object entity) : execution((@Inject *).new(..)) && this(entity) {
		ConstructorSignature signature = ((ConstructorSignature) 
									thisJoinPointStaticPart.getSignature());
		injectFields(entity, signature.getDeclaringType().getDeclaredFields());
	}

	private void injectFields(Object entity, Field[] fields) {
		for (Field field : fields) {
			if (!field.isAnnotationPresent(Inject.class)) continue;
			try {
				field.setAccessible(true);
				field.set(entity, context.get(field.getType()));
			} catch (Exception e) {
				throw new RuntimeException("Error setting field: " + field, e);
			}
		}
	}

	public <T> void addDependency(T value) {
		this.context.put(value.getClass(), value);
	}
}