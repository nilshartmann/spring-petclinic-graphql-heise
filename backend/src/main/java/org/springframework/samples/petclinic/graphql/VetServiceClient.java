package org.springframework.samples.petclinic.graphql;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.model.InvalidVetDataException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class VetServiceClient {

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


    public Mono<VetResource> vetById(Integer id) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/vets/{id}")
                .build(id))
            .retrieve()
            .bodyToMono(VetResource.class);
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
