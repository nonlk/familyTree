package ru.test.INOBITEC.familyTree.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.test.INOBITEC.familyTree.model.People;
import ru.test.INOBITEC.familyTree.repository.PeopleMapper;

import java.util.List;

@RestController
@RequestMapping("/api/test")
public class PeopleController {

    @Autowired
    private PeopleMapper peopleMapper;

    //Тестовый сервис получения всех созданных людей для проверки бэка
    @GetMapping("/people")
    public List<People> getAllPeople() {
        return peopleMapper.findAll();
    }

}
