package ru.test.INOBITEC.familyTree.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.test.INOBITEC.familyTree.exception.ResourceNotFoundException;
import ru.test.INOBITEC.familyTree.model.People;
import ru.test.INOBITEC.familyTree.repository.PeopleMapper;

import java.util.HashMap;
import java.util.Map;

@Service
public class PeopleService {

    @Autowired
    private PeopleMapper peopleMapper;

    public People getPersonById(Integer id) {
        People person = peopleMapper.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Person is not found"));
        return person;
    }

    public Map<String, Boolean> createPerson(Map<String, String> request) {
        Map<String, Boolean> responce = new HashMap<>();
        //Проверка на существование человека с такими же именем и фамилией
        if (peopleMapper.findByParam(request.get("firstName"), request.get("lastName")).isPresent()) {
            responce.put("created", Boolean.FALSE);
            return responce;
        }
        People person = new People(request.get("firstName"), request.get("lastName"));

        Boolean aBoolean = peopleMapper.insert(person) > 0 ?
                responce.put("created", Boolean.TRUE) : responce.put("created", Boolean.FALSE);
        return responce;
    }

    public Integer getPersonId(String firstName, String lastName) {
        People person = peopleMapper.findByParam(firstName, lastName).
                orElseThrow(() -> new ResourceNotFoundException("Person is not found"));
        return person.getId();
    }

    public int deleteById(Integer id){
        return peopleMapper.deleteById(id);
    }

}
