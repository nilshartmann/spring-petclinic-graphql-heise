package org.springframework.samples.petclinic.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.model.InvalidVetDataException;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.model.VetService;
import org.springframework.samples.petclinic.repository.SpecialtyRepository;
import org.springframework.samples.petclinic.repository.VetRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@RestController
public class VetApiController {

    private static final Logger log = LoggerFactory.getLogger( VetApiController.class );

    private final VetRepository vetRepository;
    private final VetService vetService;
    private final SpecialtyRepository specialtyRepository;

    private int request = 0;

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

    @GetMapping(value = "/find-vets/{vetIds}")
    public List<VetResource> users(@PathVariable Integer[] vetIds) {
        log.info("'find'-Request for Vets with Ids '{}' received", List.of(vetIds));
        return Arrays.stream(vetIds).
            map(vetRepository::findById)
            .map(VetResource::fromVet)
            .toList();
    }


    @GetMapping("/vets/{id}")
    public VetResource vets(@PathVariable("id") Integer id) {
        log.info("Request for Vet Id '{}' received", id);
        slowdownForVetId(id);

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

    private void slowdownForVetId(Integer id) {
        sleep(100 + (id * 50));
    }

    private  void slowdown() {
        sleep((request++) * 10);
    }

    private static void sleep(long ms) {
        log.info("Sleep {} ms",ms);
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
            // dont care
        }
    }
}
