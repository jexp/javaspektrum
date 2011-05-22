package com.example.domain;

import static org.junit.Assert.*;

import org.junit.Test;

import com.example.support.Inject;
import com.example.support.InjectAspekt;


public class InjectTest {
	@Inject
	public static class DeepThought {
		@Inject Integer antwort;
		@Inject String frage;
		public String antworte() {
			return String.format("%s ist die Antwort auf: %s",antwort, frage);
		}
	}
	@Test
	public void testInject() {
		InjectAspekt.aspectOf().addDependency(42);
		String frage = "Frage nach dem Leben, dem Universum und dem ganzen Rest?";
		InjectAspekt.aspectOf().addDependency(frage);
		DeepThought computer = new DeepThought();
		assertEquals("42 ist die Antwort auf: "+frage,computer.antworte());
	}
}
