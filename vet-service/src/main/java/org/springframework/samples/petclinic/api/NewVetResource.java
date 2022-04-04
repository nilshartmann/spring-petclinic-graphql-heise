package org.springframework.samples.petclinic.api;

import java.util.List;
import java.util.Map;

/**
 * @author Nils Hartmann
 */
public class NewVetResource {

    private String firstName;
    private String lastName;
    private List<Integer> specialtyIds;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Integer> getSpecialtyIds() {
        return specialtyIds;
    }

    public void setSpecialtyIds(List<Integer> specialtyIds) {
        this.specialtyIds = specialtyIds;
    }
}
