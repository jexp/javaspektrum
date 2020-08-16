package com.example.support;

import java.io.PrintStream;

import com.example.domain.Konto;


public privileged aspect KontoAspekt {

	private PrintStream Konto.audit = System.out;
	
	public int Konto.getWert() { return wert; }

	pointcut buchung(int delta, Konto konto) : call(void Konto.buche(int)) && args(delta) && this(konto);

	before(int delta, Konto konto) : buchung(delta,konto) {
		if (delta < konto.wert) throw new IllegalArgumentException("Konto wird mit "+delta+" Ÿberzogen.");
	}

	after(int delta, Konto konto) returning: buchung(delta,konto) {
		konto.audit.printf("Gebucht auf Konto %s : Betrag: %d%n",konto.name, delta);
	}

	before(int neu) : set(int Konto.wert) && args(neu) {
		if (neu < 0) throw new IllegalStateException("Error Kontowert sollte negativ werden");
	}
}