package org.springframework.samples.petclinic.graphql;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.WebGraphQlTester;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;

public class VisitControllerTests extends AbstractClinicGraphqlTests {
    @MockBean
    private VetServiceClient vetServiceClient;

    @Test
    public void visit_shouldIncludeTreatingVet() {
        when(vetServiceClient.vetsWithIds(Set.of(1, 4, 2)))
            .thenReturn(Flux.just(new VetResource(
                1, "Klaus", "Dieter", List.of()
            ),new VetResource(
                4, "Smith", "Carla", List.of()
            ),new VetResource(
                2, "Shawn", "Clark", List.of()
            )));
        userRoleGraphQlTester
            .document("query { pet(id: 8) { id visits { visits { id treatingVet { id } } } } }")
            .execute()
            .path("data.pet.visits.visits[*]").entityList(Object.class).hasSize(4)
            .path("data.pet.visits.visits[0].id").entity(String.class).isEqualTo("2")
            .path("data.pet.visits.visits[1].id").entity(String.class).isEqualTo("3")
            .path("data.pet.visits.visits[0].treatingVet.id").entity(String.class).isEqualTo("1")
            .path("data.pet.visits.visits[1].treatingVet.id").entity(String.class).isEqualTo("4")
            .path("data.pet.visits.visits[2].treatingVet.id").entity(String.class).isEqualTo("2")
            .path("data.pet.visits.visits[3].treatingVet.id").entity(String.class).isEqualTo("4");
    }

    @Test
    @DirtiesContext
    void shouldAddNewVisit() {
        userRoleGraphQlTester
            .documentName("addVisitMutation")
            .execute()
            .path("addVisit.visit.description").entity(String.class).isEqualTo("hurray")
            .path("addVisit.visit.date").entity(String.class).isEqualTo("2020/12/31")
            .path("addVisit.visit.pet.id").entity(String.class).isEqualTo("1");
    }

    @Test
    @DirtiesContext
    void shouldAddNewVisitFromVariables_And_HandlesDateCoercingInVariables(@Autowired WebGraphQlTester graphQlTester) {
        userRoleGraphQlTester
            .documentName("addVisitMutationWithVariables")
            .variable("addVisitInput", Map.of(
                "petId", 3,//
                "description", "Another visit", //
                "date", "2022/03/25"
            ))
            .execute()
            .path("addVisit.visit.description").entity(String.class).isEqualTo("Another visit")
            .path("addVisit.visit.date").entity(String.class).isEqualTo("2022/03/25")
            .path("addVisit.visit.pet.id").entity(String.class).isEqualTo("3")
            .path("addVisit.visit.treatingVet").valueIsNull();
    }

    @Test
    @DirtiesContext
    void shouldAddNewVisitFromVariablesWithVetId(@Autowired WebGraphQlTester graphQlTester) {
        when(vetServiceClient.vetsWithIds(anySet()))
            .thenReturn(Flux.just(new VetResource(1, "Horst", "Schimansky", List.of())));

        userRoleGraphQlTester
            .documentName("addVisitMutationWithVariables")
            .variable("addVisitInput", Map.of(
                "petId", 3,//
                "description", "Another visit", //
                "date", "2022/03/25",
                "vetId", 1
            ))
            .execute()
            .path("addVisit.visit.description").entity(String.class).isEqualTo("Another visit")
            .path("addVisit.visit.date").entity(String.class).isEqualTo("2022/03/25")
            .path("addVisit.visit.pet.id").entity(String.class).isEqualTo("3")
            .path("addVisit.visit.treatingVet.id").entity(String.class).isEqualTo("1");
    }
}
