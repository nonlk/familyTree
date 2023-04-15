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
    public Map<String, Boolean> createPerson (@RequestBody People person) {
        Map<String, Boolean> responce = new HashMap<>();
        Boolean aBoolean = peopleRepository.insert(person) > 0 ?
                responce.put("", Boolean.TRUE) : responce.put("", Boolean.FALSE);
        return responce;
    }
}
