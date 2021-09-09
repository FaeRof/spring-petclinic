package org.springframework.samples.petclinic.visit;


import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class VisitValidator implements Validator {

	private static final String REQUIRED = "required";

	@Override
	public void validate(Object obj, Errors errors) {
		Visit visit = (Visit) obj;
		String description = visit.getDescription();

		if (!StringUtils.hasLength(description)){
			errors.rejectValue("description", REQUIRED, REQUIRED);
		}

		if (visit.getDate() == null){
			errors.rejectValue("date", REQUIRED, REQUIRED);
		}

		if (visit.isNew() && visit.getVet() == null){
			errors.rejectValue("vet", REQUIRED, REQUIRED);
		}
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return Visit.class.isAssignableFrom(clazz);
	}
}
