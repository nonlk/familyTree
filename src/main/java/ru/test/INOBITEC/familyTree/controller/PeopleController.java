package ru.test.INOBITEC.familyTree.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.test.INOBITEC.familyTree.exception.ResourceNotFoundException;
import ru.test.INOBITEC.familyTree.model.People;
import ru.test.INOBITEC.familyTree.repository.PeopleRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class PeopleController {

    @Autowired
    private PeopleRepository peopleRepository;

    @GetMapping("/people")
    public List<People> getAllPeople() {
        return peopleRepository.findAll();
    }

    @GetMapping("/people/{id}")
    public People getPersonById(@PathVariable Integer id) {
        People person = peopleRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Person is not found"));
        return person;
    }

    @PostMapping("/people")
    public Map<String, Boolean> createPerson(@RequestBody People person) {
        Map<String, Boolean> responce = new HashMap<>();
        Boolean aBoolean = peopleRepository.insert(person) > 0 ?
                responce.put("created", Boolean.TRUE) : responce.put("created", Boolean.FALSE);
        return responce;
    }

    @GetMapping("/people/getPersonId")
    public Integer getPersonId(@RequestParam String firstName,
                                @RequestParam String lastName) {
        People person = peopleRepository.findByParam(firstName, lastName).
                orElseThrow(() -> new ResourceNotFoundException("Person is not found"));
        return person.getId();
    }

    @DeleteMapping("/people/{id}")
    public Map<String, Boolean> deleteById(@PathVariable Integer id) {

        People person = peopleRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Person is not found"));

        Map<String, Boolean> response = new HashMap<>();

        Boolean aBoolean = peopleRepository.deleteById(person.getId()) > 0 ?
                response.put("deleted", Boolean.TRUE) : response.put("deleted", Boolean.FALSE);
        return response;
    }


}
