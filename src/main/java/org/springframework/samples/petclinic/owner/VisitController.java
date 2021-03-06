/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import java.util.Collection;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.visit.Visit;
import org.springframework.samples.petclinic.visit.VisitRepository;
import org.springframework.samples.petclinic.visit.VisitValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Dave Syer
 */
@Controller
class VisitController {

	private static final String VIEWS_VISIT_CREATE_OR_UPDATE_FORM = "pets/createOrUpdateVisitForm";

	private final VisitRepository visits;

	private final PetRepository pets;

	public VisitController(VisitRepository visits, PetRepository pets) {
		this.visits = visits;
		this.pets = pets;
	}

	@InitBinder("visit")
	public void initVisitBinder(WebDataBinder dataBinder){dataBinder.setValidator(new VisitValidator());}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	/**
	 * Called before each and every @RequestMapping annotated method. 2 goals: - Make sure
	 * we always have fresh data - Since we do not use the session scope, make sure that
	 * Pet object always has an id (Even though id is not part of the form fields)
	 * @param petId
	 * @return Pet
	 */
	@ModelAttribute("visit")
	public Visit loadPetWithVisit(@PathVariable("petId") int petId, Map<String, Object> model) {
		Pet pet = this.pets.findById(petId);
		pet.setVisitsInternal(this.visits.findByPetId(petId));
		model.put("pet", pet);
		Visit visit = new Visit();
		pet.addVisit(visit);
		return visit;
	}

	@ModelAttribute("vets")
	public Collection<Vet> vetCollection(){
		return this.visits.findVet();
	}

	// Spring MVC calls method loadPetWithVisit(...) before initNewVisitForm is called
	@GetMapping("/owners/*/pets/{petId}/visits/new")
	public String initNewVisitForm(@PathVariable("petId") int petId, Map<String, Object> model) {
		return VIEWS_VISIT_CREATE_OR_UPDATE_FORM;
	}

	// Spring MVC calls method loadPetWithVisit(...) before processNewVisitForm is called
	@PostMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	public String processNewVisitForm(@Valid Visit visit, BindingResult result) {
		if (result.hasErrors()) {
			return VIEWS_VISIT_CREATE_OR_UPDATE_FORM;
		}
		else {
			this.visits.save(visit);
			return "redirect:/owners/{ownerId}";
		}
	}

	@GetMapping("/owners/{ownerId}/pets/{petId}/visits/{visitId}/edit")
	public String initUpdateVisitForm(@PathVariable("visitId") int visitId, ModelMap model){
		Visit visit = this.visits.findById(visitId);
		model.addAttribute(visit);
		return VIEWS_VISIT_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/owners/{ownerId}/pets/{petId}/visits/{visitId}/edit")
	public String processUpdateVisitForm(@Valid Visit visit, BindingResult result,
										 @PathVariable("visitId") int visitId){
		if (result.hasErrors()){
			return VIEWS_VISIT_CREATE_OR_UPDATE_FORM;
		}
		else {
			visit.setId(visitId);
			this.visits.save(visit);
			return "redirect:/owners/{ownerId}";
		}
	}

	@GetMapping("/owners/{ownerId}/pets/{petId}/visits/{visitId}")
	public String initUpdateVisit(@PathVariable("visitId") int visitId, ModelMap model){
		Visit visit = this.visits.findById(visitId);
		model.addAttribute(visit);
		visit.setActive(true);
		this.visits.save(visit);
		return "redirect:/owners/{ownerId}";
	}

	@GetMapping("/owners/{ownerId}/pets/{petId}/visits/{visitId}/delete")
	public String processDeleteVisit(@PathVariable("visitId") int visitId){
		visits.deleteById(visitId);
		return "redirect:/owners/{ownerId}";
	}
}
