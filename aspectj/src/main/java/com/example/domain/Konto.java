package com.example.domain;

import com.example.support.Entity;
import com.example.support.Min;
import com.example.support.Waehrung;

@Entity
public class Konto {
	private static final long serialVersionUID = 1L;

	@Min(10) private int wert;
	
	private String name;
	
	public void buche(int delta) {
		wert += delta;
	}

	public Konto(int wert, String name) {
		this.wert = wert;
		this.name = name;
	}

	
	public String toString() {
//		return String.format("%s: %d %s",name, wert , KontoRenderer.CURRENCY);
		return String.format("%s: %d %s",name, wert , Waehrung.EUR);
	}
}
