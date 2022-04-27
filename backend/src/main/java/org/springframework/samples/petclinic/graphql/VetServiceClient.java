package org.springframework.samples.petclinic.graphql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.model.InvalidVetDataException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class VetServiceClient {

    private static final Logger log = LoggerFactory.getLogger( VetServiceClient.class );

    private final WebClient webClient;

    public VetServiceClient(@Value("${petclinic.vet-service.url}") String vetServiceUrl) {
        this.webClient = WebClient.builder().baseUrl(vetServiceUrl).build();
    }

    public Flux<VetResource> vets() {
        return webClient.get()
            .uri("/vets")
            .retrieve()
            .bodyToFlux(VetResource.class);
    }

    public Flux<SpecialtyResource> specialties() {
        return webClient.get()
            .uri("/specialties")
            .retrieve()
            .bodyToFlux(SpecialtyResource.class);
    }

    /**
     * Read a single vet using a blocking call.
     *
     * Note: normaly you would use the reactive {@link #vetById(Integer)} method, this is for
     * demonstration purposes only.
     */
    public VetResource getVetById(Integer id) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/vets/{id}")
                .build(id))
            .retrieve()
            .bodyToMono(VetResource.class)
            .block();
    }


    /**
     * Read a single vet, returning a reactive Mono object for the result
     */
    public Mono<VetResource> vetById(Integer id) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/vets/{id}")
                .build(id))
            .retrieve()
            .bodyToMono(VetResource.class)
            .elapsed()
            .doFirst( () -> log.info("Start Http Request for Vet with id '{}'", id))
            .doOnNext(result -> log.info("Finished Http Request for vet id '{}' took {}ms", id, result.getT1()))
            .map(Tuple2::getT2);
    }

    /**
     * Uses the `find-vets` endpoint to read all vets with given ids in ONE rest call.
     */
    public Flux<VetResource> findVetsWithIds(Collection<Integer> vetIds) {
        var vetIdStrings = vetIds.stream().map(Object::toString).collect(Collectors.joining(","));
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/find-vets/{vetIds}")
                .build(vetIdStrings))
            .retrieve().bodyToFlux(VetResource.class)
            .doFirst( () -> log.info("Start Http Request for Vets with ids '{}'", vetIdStrings));
    }

    /**
     * Uses the `vets` endpoint to read all vets with given ids in one-by-one in MULTIPLE rest calls.
     *
     * Note: you would use {@link #findVetsWithIds(Collection)} normaly, but for demonstration
     * (concurrency problems) purposes we need this method here too
     */
    public Flux<VetResource> vetsWithIds(Collection<Integer> ids) {
        return Flux.fromStream(ids.stream()).flatMap(this::vetById);
    }

    public Mono<VetResource> addVet(AddVetInput addVetInput) {
        return webClient.post()
            .uri("/vet")
            .bodyValue(addVetInput)
            .retrieve().
            onStatus(HttpStatus::is4xxClientError,
                response -> response.bodyToMono(String.class).map(InvalidVetDataException::new))
            .bodyToMono(VetResource.class);
    }
}
