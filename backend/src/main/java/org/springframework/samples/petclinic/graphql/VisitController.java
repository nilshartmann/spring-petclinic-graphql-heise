package org.springframework.samples.petclinic.graphql;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.model.VisitService;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

/**
 * GraphQL handler functions for "Vitis" GraphQL type, Query, Mutation and Subscription
 *
 * @author Nils Hartmann (nils@nilshartmann.net)
 */

@Controller
public class VisitController {

    private final VisitService visitService;
    private final VisitPublisher visitPublisher;
    private final VetServiceClient vetServiceClient;


    public VisitController(VisitService visitService, VisitPublisher visitPublisher, VetServiceClient vetServiceClient) {
        this.visitService = visitService;
        this.visitPublisher = visitPublisher;
        this.vetServiceClient = vetServiceClient;
    }

    @SchemaMapping
    public VetResource treatingVet(Visit visit) {
        if (!visit.hasVetId()) {
            return null;
        }
        return vetServiceClient.vetById(visit.getVetId());
    }

    @MutationMapping
    public AddVisitPayload addVisit(@Argument AddVisitInput input) {
        Visit visit = visitService.addVisit(
            input.getPetId(),
            input.getDescription(),
            input.getDate(),
            input.getVetId()
        );

        return new AddVisitPayload(visit);
    }

    @SubscriptionMapping
    public Flux<Visit> onNewVisit() {
        return visitPublisher.getPublisher();
    }

}
