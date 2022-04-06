package org.springframework.samples.petclinic.graphql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * GraphQL handler functions for "Specialty" GraphQL type, Query and Mutation
 *
 * @author Nils Hartmann (nils@nilshartmann.net)
 */
@Controller
public class SpecialtyController {

    private static final Logger log = LoggerFactory.getLogger(SpecialtyController.class);

    private final VetServiceClient vetServiceClient;

    public SpecialtyController(VetServiceClient vetServiceClient) {
        this.vetServiceClient = vetServiceClient;
    }

    @QueryMapping
    public Flux<SpecialtyResource> specialties() {
      return vetServiceClient.specialties();
    }
}
