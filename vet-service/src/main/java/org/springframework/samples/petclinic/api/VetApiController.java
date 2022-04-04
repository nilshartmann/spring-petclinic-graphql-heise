package org.springframework.samples.petclinic.api;

import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.model.InvalidVetDataException;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.model.VetService;
import org.springframework.samples.petclinic.repository.SpecialtyRepository;
import org.springframework.samples.petclinic.repository.VetRepository;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Stream;

@RestController
public class VetApiController {

    private final VetRepository vetRepository;
    private final VetService vetService;
    private final SpecialtyRepository specialtyRepository;

    public VetApiController(VetRepository vetRepository, VetService vetService, SpecialtyRepository specialtyRepository) {
        this.vetRepository = vetRepository;
        this.vetService = vetService;
        this.specialtyRepository = specialtyRepository;
    }

    @GetMapping("/vets")
    public Stream<VetResource> vets() {
        return vetRepository.findAll()
            .stream()
            .map(VetResource::fromVet);
    }

    @GetMapping("/vets/{id}")
    public VetResource vets(@PathVariable("id") Integer id) {
        Vet vet = vetRepository.findById(id);
        if (vet == null) {
            // todo could return 404 here
            return null;
        }

        return VetResource.fromVet(vet);
    }

    @GetMapping("/specialties")
    public Stream<SpecialtyResource> specialties() {
        return specialtyRepository.findAll()
            .stream()
            .map(specialty -> new SpecialtyResource(specialty.getId(), specialty.getName()))
            ;
    }

    @PostMapping("/vet")
    public ResponseEntity<?> newVet(@RequestBody NewVetResource newVetResource) throws InvalidVetDataException {
        try {
            Vet newVew = vetService.createVet(
                newVetResource.getFirstName(),
                newVetResource.getLastName(),
                newVetResource.getSpecialtyIds()
            );

            return ResponseEntity.ok(VetResource.fromVet(newVew));
        } catch (InvalidVetDataException ex) {
            return ResponseEntity.
                badRequest().body(ex.getMessage());
        }
    }
}
