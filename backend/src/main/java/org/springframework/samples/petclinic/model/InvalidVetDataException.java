package org.springframework.samples.petclinic.model;

public class InvalidVetDataException extends RuntimeException {
    public InvalidVetDataException(String msg) {
        super(msg);
    }

    @Override
    public String toString() {
        return super.getMessage();
    }
}
