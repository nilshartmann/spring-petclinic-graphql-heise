package org.springframework.samples.petclinic.graphql;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

/**
 * @author Nils Hartmann (nils@nilshartmann.net)
 */
public record AddVisitInput(
    int petId,
    Integer vetId,
    LocalDate date,
    @Size(min=5)
    String description
) {}

