package org.springframework.samples.petclinic.graphql;

/**
 * @author Nils Hartmann
 */
public class AddVetSuccessPayload implements AddVetPayload {
    private final VetResource vet;

    public AddVetSuccessPayload(VetResource vet) {
        this.vet = vet;
    }

    public VetResource getVet() {
        return vet;
    }
}
