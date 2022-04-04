package org.springframework.samples.petclinic.graphql;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Nils Hartmann
 */
public class AddVetInput {

    private String firstName;
    private String lastName;
    private List<Integer> specialtyIds;

    public AddVetInput() {
    }

    public AddVetInput(String firstName, String lastName, List<Integer> specialtyIds) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialtyIds = specialtyIds;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddVetInput that = (AddVetInput) o;
        return Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(specialtyIds, that.specialtyIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, specialtyIds);
    }
}
