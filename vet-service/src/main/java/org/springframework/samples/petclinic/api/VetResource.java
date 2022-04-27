package org.springframework.samples.petclinic.api;

import org.springframework.samples.petclinic.model.Vet;

import java.util.List;

public record VetResource(Integer id,
                          String firstName,
                          String lastName,
                          List<SpecialtyResource> specialties
) {

    static VetResource fromVet(Vet vet) {
        if (vet == null) {
            return null;
        }
        return new VetResource(
            vet.getId(),
            vet.getFirstName(),
            vet.getLastName(),
            vet.getSpecialties().stream()
                .map(specialty -> new SpecialtyResource(specialty.getId(), specialty.getName())).toList()
        );
    }

}
