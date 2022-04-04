package org.springframework.samples.petclinic.graphql;

import java.util.List;

public record VetResource(Integer id,
                          String firstName,
                          String lastName,
                          List<SpecialtyResource> specialties
) {
}
