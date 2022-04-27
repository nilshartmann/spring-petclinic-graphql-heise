package org.springframework.samples.petclinic.graphql;

import graphql.schema.DataFetchingEnvironment;
import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.DataLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.graphql.execution.BatchLoaderRegistry;
import org.springframework.samples.petclinic.graphql.runtime.GraphQLUtils;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.model.VisitService;
import org.springframework.samples.petclinic.repository.VisitRepository;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * GraphQL handler functions for "Visit" GraphQL type, Query, Mutation and Subscription
 *
 * @author Nils Hartmann (nils@nilshartmann.net)
 */

@Controller
public class VisitController {

    private static final Logger log = LoggerFactory.getLogger(VisitController.class);

    private final VisitRepository visitRepository;
    private final VisitService visitService;
    private final VisitPublisher visitPublisher;
    private final VetServiceClient vetServiceClient;


    public VisitController(VisitRepository visitRepository,
                           VisitService visitService,
                           VisitPublisher visitPublisher,
                           VetServiceClient vetServiceClient,
                           BatchLoaderRegistry batchLoaderRegistry) {
        this.visitRepository = visitRepository;
        this.visitService = visitService;
        this.visitPublisher = visitPublisher;
        this.vetServiceClient = vetServiceClient;

        batchLoaderRegistry.forTypePair(Integer.class, VetResource.class)
//            .registerBatchLoader(
//
//                (List<Integer> keys, BatchLoaderEnvironment env) -> {
//                   // Return Type: Flux<VetResource>. Order of emitted objects must match order of keys list!
//                   log.info("Loading vets with keys {}", keys);
//                    Flux<VetResource> vetsWithIds = vetServiceClient.vetsWithIds(keys);
//                    return vetsWithIds;
//                });
            .registerMappedBatchLoader(
                (Set<Integer> keys, BatchLoaderEnvironment env) -> {
                    // Mono<Map<Integer, VetResource>>
                    log.info("Mapped Batch Loader - Loading vets with keys {}", keys);
                    return vetServiceClient.vetsWithIds(keys).
                        map(v -> Map.entry(v.id(), v))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                });

    }


    record AddVisitInput(
        int petId,
        Integer vetId,
        LocalDate date,
        @Size(min = 5)
        String description
    ) {
    }

    @MutationMapping
    public AddVisitPayload addVisit(@Valid @Argument AddVisitInput input) {
        Visit visit = visitService.addVisit(
            input.petId(),
            input.description(),
            input.date(),
            input.vetId()
        );

        return new AddVisitPayload(visit);
    }

    @SubscriptionMapping
    public Flux<Visit> onNewVisit() {
        return visitPublisher.getPublisher();
    }

    @SchemaMapping
    public Integer treatingVetId(Visit visit) {
        return visit.getVetId();
    }

    @QueryMapping
    public Collection<Visit> visits(@Argument Optional<Integer> petId, DataFetchingEnvironment env) {

        log.info("Loading Visits from database");
        Collection<Visit> visits = petId.map(visitRepository::findAllByPetIdOrderById).orElseGet(visitRepository::findAll);

        GraphQLUtils.addFieldContext(env,
            petId.map(id -> "Loaded " + visits.size() + " Visits for Pet " + id + " from Database")
                .orElse("Loading all " + visits.size() + " Visits from Database")
        );
        return visits;
    }

    @SchemaMapping(typeName = "Visit")
    public CompletableFuture<VetResource> treatingVet(Visit visit, DataFetchingEnvironment env,
                                                      DataLoader<Integer, VetResource> dataLoader) {
        if (!visit.hasVetId()) {
            return null;
        }

        log.info("Delegating loading of Vet with id {} from REST", visit.getVetId());
        GraphQLUtils.addFieldContext(env, "Delegate loading Vet to DataLoader. Vet Id: " + visit.getVetId());

        return dataLoader.load(visit.getVetId());

        //  return vetServiceClient.vetById(visit.getVetId());
    }
}
