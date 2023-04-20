package ru.test.INOBITEC.familyTree.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.test.INOBITEC.familyTree.exception.ResourceNotFoundException;
import ru.test.INOBITEC.familyTree.model.Branch;
import ru.test.INOBITEC.familyTree.model.People;
import ru.test.INOBITEC.familyTree.repository.BranchMapper;
import ru.test.INOBITEC.familyTree.repository.PeopleMapper;

import java.util.*;

@RestController
@RequestMapping("/api/familyTree")
public class BranchController {

    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private PeopleMapper peopleMapper;

    //Сервис получения всех созданных веток
    @RequestMapping(value = "getAllBranches", method = RequestMethod.GET, produces = "application/json")
    public List<Branch> getAllBranches() {
        return branchMapper.findAll();
    }

    //Сервис создания ветки и нового человека в иерархии
    @RequestMapping(value = "createBranchAndChild", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Boolean> createBranchAndChild(@RequestBody Map<String, String> request) {
        Map<String, Boolean> responce = new HashMap<>();

        //Поиск родителя (уже созданного) по передаваемым параметрам: имя и фамилия
        People parent = peopleMapper.
                findByParam(request.get("parentFirstName"), request.get("parentLastName")).
                orElseThrow(() -> new ResourceNotFoundException("Parent is not found"));

        //Проверка на создание одинаковых людей
        if (peopleMapper.findByParam(request.get("childFirstName"), request.get("childLastName")).isPresent()) {
            responce.put("Данный человек уже есть в базе, дайте ребенку второе имя через (-)", Boolean.FALSE);
            return responce;
        }
        //Создание нового человека по передаваемым параметрам: имя и фамилия
        People child = new People(request.get("childFirstName"), request.get("childLastName"));

        //Проверка на добавление созданного человека в таблицу БД
        if (peopleMapper.insert(child) > 0) {

            //Переприсвоение для получения id созданного человека
            child = peopleMapper.
                    findById(peopleMapper.findAll().get(peopleMapper.findAll().size() - 1).getId()).
                    orElseThrow(() -> new ResourceNotFoundException("Child is not found"));

            //Создание новой ветки дерева
            Branch branch = new Branch(parent.getId(), child.getId());

            //Проверка на добавление в таблицу БД
            Boolean aBoolean = branchMapper.insert(branch) > 0 ?
                    responce.put("created", Boolean.TRUE) :
                    responce.put("created", Boolean.FALSE);
        }
        else responce.put("child created", Boolean.FALSE);
        return responce;
    }

}
