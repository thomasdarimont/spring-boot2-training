package demo;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class SimpleCrudApp {

    public static void main(String[] args) {
        SpringApplication.run(SimpleCrudApp.class, args);
    }
}

@Data
@Entity
class Person {

    @Id
    @GeneratedValue
    Long id;

    String name;
}

interface PersonRepository extends JpaRepository<Person, Long> {
}

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class PersonService {

    private final PersonRepository repository;

    @Transactional
    public Person savePerson(@RequestBody Person person) {
        return repository.save(person);
    }

    public List<Person> findPersons() {
        return repository.findAll();
    }

    public Optional<Person> findPersonById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public void deletePersonById(Person person) {
        repository.delete(person);
    }
}

/**
 * <pre>
 *     curl -v -H "content-type: application/json" -d '{"name":"Tom"}' http://localhost:8080/persons
 *     curl -v -H "content-type: application/json" -d '{"name":"Anne"}' http://localhost:8080/persons
 *     curl -v http://localhost:8080/persons
 *     curl -v -X DELETE http://localhost:8080/persons/2
 *     curl -v -X PUT -H "content-type: application/json" -d '{"name":"Thomas"}' http://localhost:8080/persons/1
 *
 * </pre>
 */
@RestController
@RequestMapping("/persons")
@RequiredArgsConstructor
class PersonController {

    private final PersonService service;

    @PostMapping
    public Person registerPerson(@RequestBody Person person) {
        return service.savePerson(person);
    }

    @DeleteMapping("/{id}")
    public void deletePerson(@PathVariable Long id) {
        Optional<Person> person = service.findPersonById(id);
        person.ifPresent(service::deletePersonById);
    }

    @PutMapping("/{id}")
    public void updatePerson(@PathVariable Long id, @RequestBody Person update) {
        Optional<Person> candidate = service.findPersonById(id);
        candidate.map(person -> {
            person.setName(update.getName());
            return person;
        }).ifPresent(service::savePerson);
    }


    @GetMapping
    public List<Person> listAllPersons() {
        return service.findPersons();
    }
}