package org.springframework.samples.petclinic.graphql;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.model.InvalidVetDataException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class VetServiceClient {

    private final WebClient webClient;

    public VetServiceClient(@Value("${petclinic.vet-service.url}") String vetServiceUrl) {
        this.webClient = WebClient.builder().baseUrl(vetServiceUrl).build();
    }

    public List<VetResource> vets() {
        return webClient.get()
            .uri("/vets")
            .retrieve()
            .bodyToFlux(VetResource.class)
            .collectList()
            .block();
    }

    public List<SpecialtyResource> specialties() {
        return webClient.get()
            .uri("/specialties")
            .retrieve()
            .bodyToFlux(SpecialtyResource.class)
            .collectList()
            .block();
    }


    public VetResource vetById(Integer id) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/vets/{id}")
                .build(id))
            .retrieve().bodyToMono(VetResource.class)
            .block();
    }

    public VetResource addVet(AddVetInput addVetInput) {
        return webClient.post()
            .uri("/vet")
            .bodyValue(addVetInput)
            .retrieve().
            onStatus(HttpStatus::is4xxClientError,
                response -> response.bodyToMono(String.class).map(InvalidVetDataException::new))
            .bodyToMono(VetResource.class)
            .block();
    }
}
