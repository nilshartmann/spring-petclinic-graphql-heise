package org.springframework.samples.petclinic.graphql;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.samples.petclinic.model.InvalidVetDataException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class VetControllerTests extends AbstractClinicGraphqlTests{
    @MockBean
    private VetServiceClient vetServiceClient;

    private static SpecialtyResource specialtyOne =
        new SpecialtyResource(1, "radiology");

    private static SpecialtyResource specialtyTwo =
        new SpecialtyResource(2, "surgery");

    private static SpecialtyResource specialtyThree =
        new SpecialtyResource(3, "dentistry");

    @Test
    void shouldAddNewVet() {
        final AddVetInput input = new AddVetInput("Klaus", "Smith",
            List.of(1, 3));

        when(vetServiceClient.addVet(input))
            .thenReturn(Mono.just(new VetResource(123, "Klaus", "Smith",
                List.of(specialtyThree, specialtyOne))));

        managerRoleGraphQlTester
            .documentName("addVetMutation")
            .variable("specialtyIds", new int[]{1, 3})
            .execute()
            .path("addVet.vet.id").hasValue()
            .path("addVet.vet.firstName").entity(String.class).isEqualTo("Klaus")
            .path("addVet.vet.lastName").entity(String.class).isEqualTo("Smith")
            .path("addVet.vet.specialties[*]").entityList(Object.class).hasSize(2)
            .path("addVet.vet.specialties[0].id").entity(String.class).isEqualTo("3")
            .path("addVet.vet.specialties[1].id").entity(String.class).isEqualTo("1");
    }

    @Test
    void shouldReturnErrorPayloadOnUnknownSpecialty() {
        when(vetServiceClient.addVet(any()))
            .thenThrow(new InvalidVetDataException("Specialty with Id '666' not found"));

        managerRoleGraphQlTester
            .documentName("addVetMutation")
            .variable("specialtyIds", new int[]{666})
            .execute()
            .path("addVet.vet").pathDoesNotExist()
            .path("addVet.error").entity(String.class).isEqualTo("Specialty with Id '666' not found");
    }

    @Test
    void shouldForbidAddingVetsAsUser() {
        userRoleGraphQlTester
            .documentName("addVetMutation")
            .variable("specialtyIds", new int[]{1, 3})
            .execute()
            .errors()
            .satisfy(errors -> {
                assertThat(errors).hasSize(1);
                assertThat(errors.get(0).getErrorType()).isEqualTo(ErrorType.FORBIDDEN);
            });
    }


    @Test
    public void vetsReturnsListOfAllVets() {
        when(vetServiceClient.vets())
            .thenReturn(Flux.just(
                new VetResource(1, "Klaus", "Dieter", List.of()),
                new VetResource(2, "Susi", "Meyer", List.of()),
                new VetResource(3, "Peter", "Miller", List.of(
                    specialtyOne, specialtyTwo
                )),
                new VetResource(4, "Maja", "Smith", List.of())
            ));

        String query = "query {" +
            "  vets {" +
            "    id" +
            "    specialties {" +
            "      id" +
            "    }"+
            "    visits {" +
            "      totalCount" +
            "      visits {" +
            "        id" +
            "        pet {" +
            "          id" +
            "        }" +
            "      }" +
            "    }" +
            "  }" +
            "}";

        userRoleGraphQlTester.document(query)
            .execute()
            .path("vets").entityList(Object.class).hasSizeGreaterThan(3)
            .path("vets[2].specialties").entityList(Object.class).hasSize(2)
            .path("vets[3].visits.totalCount").entity(int.class).isEqualTo(3)
            .path("vets[3].visits.visits[0].id").entity(String.class).isEqualTo("1")
            .path("vets[3].visits.visits[0].pet.id").entity(String.class).isEqualTo("7")
        ;
    }

    @Test
    public void vetReturnsVetById() {
        when(vetServiceClient.vetById(4))
            .thenReturn(
                Mono.just(
                    new VetResource(4, "Maja", "Smith", List.of(specialtyTwo))
                )
            );
        String query = "query {" +
            "  vet(id:4) { lastName firstName" +
            "    id" +
            "    specialties {" +
            "      id" +
            "    }"+
            "    visits {" +
            "      totalCount" +
            "      visits {" +
            "        id" +
            "        pet {" +
            "          id" +
            "        }" +
            "      }" +
            "    }" +
            "  }" +
            "}";

        userRoleGraphQlTester.document(query)
            .execute()
            .path("vet.specialties[0].id").entity(int.class).isEqualTo(2)
            .path("vet.visits.totalCount").entity(int.class).isEqualTo(3)
            .path("vet.visits.visits[0].id").entity(String.class).isEqualTo("1")
            .path("vet.visits.visits[0].pet.id").entity(String.class).isEqualTo("7")
        ;
    }

    @Test
    public void vetReturnsNullIfNotFound() {
        String query = "query {" +
            "  vet(id:666) {" +
            "    id" +
            "    specialties {" +
            "      id" +
            "    }"+
            "    visits {" +
            "      totalCount" +
            "      visits {" +
            "        id" +
            "        pet {" +
            "          id" +
            "        }" +
            "      }" +
            "    }" +
            "  }" +
            "}";

        userRoleGraphQlTester.document(query)
            .execute()
            .path("vet").valueIsNull();
        ;
    }

}
