package org.springframework.samples.petclinic.graphql;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.mockito.Mockito.when;

public class SpecialtyControllerTests extends AbstractClinicGraphqlTests {

    @MockBean
    private VetServiceClient vetServiceClient;

    @Test
    public void specialtiesQueryReturnsList() {
        when(vetServiceClient.specialties())
            .thenReturn(Flux.just(
                new SpecialtyResource(1, "A"),
                new SpecialtyResource(2, "B"),
                new SpecialtyResource(3, "C")
            ));
        String query = "query {" +
            "  specialties {" +
            "    id" +
            "    name" +
            "  }" +
            "}";

        userRoleGraphQlTester
            .document(query)
            .execute()
            .path("specialties").entityList(Object.class).hasSizeGreaterThan(2);
        ;
    }

}
