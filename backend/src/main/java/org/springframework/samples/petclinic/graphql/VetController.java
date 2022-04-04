package org.springframework.samples.petclinic.graphql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.repository.VisitRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL handler functions for Vet GraphQL type, Query and Mutation
 *
 * Note that the addVet mutation is secured in the domain layer, so that only
 * users with ROLE_MANAGER are allowed to create new vets
 *
 * @author Nils Hartmann (nils@nilshartmann.net)
 */
@Controller
public class VetController {

    private static final Logger log = LoggerFactory.getLogger(VetController.class);

    private final VetServiceClient vetServiceClient;
    private final VisitRepository visitRepository;

    public VetController(VetServiceClient vetServiceClient, VisitRepository visitRepository) {
        this.vetServiceClient = vetServiceClient;
        this.visitRepository = visitRepository;
    }


    @QueryMapping
    public List<VetResource> vets() {
        return vetServiceClient.vets();
    }

    @QueryMapping
    public VetResource vet(@Argument Integer id) {
        VetResource result = vetServiceClient.vetById(id);
        return result;
    }

    @SchemaMapping(typeName="Vet")
    public VisitConnection visits(VetResource vet) {
        List<Visit> visitList = visitRepository.findByVetId(vet.id());
        return new VisitConnection(visitList);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public AddVetPayload addVet(@Argument AddVetInput input) {
        try {
            VetResource newVet = vetServiceClient.addVet(input);

            return new AddVetSuccessPayload(newVet);
        } catch (Exception ex) {
            // todo
            return new AddVetErrorPayload(ex.getLocalizedMessage());
        }
    }
}
