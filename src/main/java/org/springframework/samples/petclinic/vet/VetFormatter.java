package org.springframework.samples.petclinic.vet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;
import org.springframework.samples.petclinic.visit.VisitRepository;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Collection;
import java.util.Locale;

@Component
public class VetFormatter implements Formatter<Vet> {

	private final VisitRepository visit;

	@Autowired
	public VetFormatter(VisitRepository visit) {
		this.visit = visit;
	}

	@Override
	public String print(Vet vet, Locale locale) {
		return vet.getName();
	}

	@Override
	public Vet parse(String text, Locale locale) throws ParseException {
		Collection<Vet> findVet = this.visit.findVet();
		for(Vet vet : findVet){
			if (vet.getName().equals(text)){
				return vet;
			}
		}
		throw new ParseException("vew not found: " + text, 0);
	}
}
