package com.example.support;

import java.util.Arrays;

import org.aspectj.lang.reflect.MethodSignature;


public privileged aspect DomaenenAspekt {

	private PersistenceManager pm;
	public interface DomaenenObjekt {}
	pointcut domainObject() : within(com.example.domain.*);
	
	declare parents : @Entity * implements java.io.Serializable;
	declare parents : @Entity * implements DomaenenObjekt;
	declare @field : !static Object+ DomaenenObjekt+.* : @NotNull;
	
	before(Object neu) : set(@NotNull (Object+) *.*) && args(neu) {
		if (neu == null) throw new IllegalStateException("Feld "+thisJoinPointStaticPart.getSignature()+" sollte null gesetzt werden! ");
	}

	before(int neu, Min min) : set(@Min (int || long) *.*) && args(neu) && @target(min) {
		if (neu < min.value()) throw new IllegalStateException("Feld "+thisJoinPointStaticPart.getSignature()+" kannt nicht kleiner als das Minimum "+min+" gesetzt werden!");
	}

	Object around() : execution(@Transactional (*) @Entity *.*(..)) {
		pm.begin();
		try {
			Object result=proceed();
			pm.commit();
			return result;
		} catch(Exception e) {
			pm.rollback();
			throw new RuntimeException("Fehler beim transaktionalem AusfŸhren",e);
		}
	}

	declare error : within(com.example.domain.*) && (call(* com.example.ui.*.*(..)) || set(* com.example.ui.*.*) || get(* com.example.ui.*.*)): "Architektur: Domain ruft UI Layer";
	
	declare @method: void DomaenenObjekt+.delete() : @Transactional; 
	
	@Transactional public void DomaenenObjekt.persist() {
		aspectOf().pm.persist(this);
	}
	public void DomaenenObjekt.delete() {
		aspectOf().pm.delete(this);
	}

	Object around() : call(@NotNull (*) @Entity *.*(..)) {
		Object[] params=(Object[]) thisJoinPoint.getArgs();
		for (int i=0;i<params.length;i++) {
			if (params[i] == null) {
				MethodSignature signature= (MethodSignature)thisJoinPointStaticPart.getSignature();
				throw new IllegalArgumentException("Parameter "+
						signature.getParameterTypes()[i]+ " "+ signature.getParameterNames()[i]
						+ " ist null!");
			}
		}
		Object result=proceed();
		if (result==null) {
			throw new IllegalStateException("Methode "+(MethodSignature)thisJoinPointStaticPart.getSignature()+ "gab null fŸr die Argumente "+Arrays.toString(thisJoinPoint.getArgs()));
		}
		return result;
	}

}