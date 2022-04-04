/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p> Base class for Repository integration tests. </p> <p> Subclasses should specify Spring context
 * configuration using {@link ContextConfiguration @ContextConfiguration} annotation </p> <p>
 * AbstractclinicServiceTests and its subclasses benefit from the following services provided by the Spring
 * TestContext Framework: </p> <ul> <li><strong>Spring IoC container caching</strong> which spares us unnecessary set up
 * time between test execution.</li> <li><strong>Dependency Injection</strong> of test fixture instances, meaning that
 * we don't need to perform application context lookups.
 * <li><strong>Transaction management</strong>, meaning each test method is executed in its own transaction,
 * which is automatically rolled back by default. Thus, even if tests insert or otherwise change database state, there
 * is no need for a teardown or cleanup script. <li> An {@link org.springframework.context.ApplicationContext
 * ApplicationContext} is also inherited and can be used for explicit bean lookup if necessary. </li> </ul>
 *
 * @author Ken Krebs
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 * @author Vitaliy Fedoriv
 */
@DataJpaTest
public class VetServiceRepositoryTests {

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldFindVetDyId() {
        Vet vet = this.vetRepository.findById(1);
        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
    }

    @Test
    @Transactional
    void shouldInsertVet() {
        Collection<Vet> vets = this.vetRepository.findAll();
        int found = vets.size();

        Vet vet = new Vet();
        vet.setFirstName("John");
        vet.setLastName("Dow");

        this.vetRepository.save(vet);
        assertThat(vet.getId().longValue()).isNotEqualTo(0);

        vets = this.vetRepository.findAll();
        assertThat(vets.size()).isEqualTo(found + 1);
    }

    @Test
    @Transactional
    void shouldUpdateVet() {
        Vet vet = this.vetRepository.findById(1);
        String oldLastName = vet.getLastName();
        String newLastName = oldLastName + "X";
        vet.setLastName(newLastName);
        this.vetRepository.save(vet);
        vet = this.vetRepository.findById(1);
        assertThat(vet.getLastName()).isEqualTo(newLastName);
    }

    @Test
    @Transactional
    void shouldDeleteVet() {
        Vet vet = this.vetRepository.findById(1);
        this.vetRepository.delete(vet);
        try {
            vet = this.vetRepository.findById(1);
        } catch (Exception e) {
            vet = null;
        }
        assertThat(vet).isNull();
    }

    @Test
    public void shouldFindSpecialtyById() {
        Specialty specialty = this.specialtyRepository.findById(1);
        assertThat(specialty.getName()).isEqualTo("radiology");
    }

    @Test
    public void shouldFindAllSpecialtys() {
        Collection<Specialty> specialties = this.specialtyRepository.findAll();
        Specialty specialty1 = EntityUtils.getById(specialties, Specialty.class, 1);
        assertThat(specialty1.getName()).isEqualTo("radiology");
        Specialty specialty3 = EntityUtils.getById(specialties, Specialty.class, 3);
        assertThat(specialty3.getName()).isEqualTo("dentistry");
    }

    @Test
    @Transactional
    public void shouldInsertSpecialty() {
        Collection<Specialty> specialties = this.specialtyRepository.findAll();
        int found = specialties.size();

        Specialty specialty = new Specialty();
        specialty.setName("dermatologist");

        this.specialtyRepository.save(specialty);
        assertThat(specialty.getId().longValue()).isNotEqualTo(0);

        specialties = this.specialtyRepository.findAll();
        assertThat(specialties.size()).isEqualTo(found + 1);
    }

    @Test
    @Transactional
    public void shouldUpdateSpecialty() {
        Specialty specialty = this.specialtyRepository.findById(1);
        String oldLastName = specialty.getName();
        String newLastName = oldLastName + "X";
        specialty.setName(newLastName);
        this.specialtyRepository.save(specialty);
        specialty = this.specialtyRepository.findById(1);
        assertThat(specialty.getName()).isEqualTo(newLastName);
    }

    @Test
    @Transactional
    public void shouldDeleteSpecialty() {
        Specialty specialty = this.specialtyRepository.findById(1);
        this.specialtyRepository.delete(specialty);
        try {
            specialty = this.specialtyRepository.findById(1);
        } catch (Exception e) {
            specialty = null;
        }
        assertThat(specialty).isNull();
    }
}
