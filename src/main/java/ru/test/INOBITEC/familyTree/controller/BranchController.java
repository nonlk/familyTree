package ru.test.INOBITEC.familyTree.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.test.INOBITEC.familyTree.exception.ResourceNotFoundException;
import ru.test.INOBITEC.familyTree.model.Branch;
import ru.test.INOBITEC.familyTree.model.People;
import ru.test.INOBITEC.familyTree.repository.BranchRepository;
import ru.test.INOBITEC.familyTree.repository.PeopleRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class BranchController {

    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private PeopleRepository peopleRepository;

    //Сервис получения всех созданных веток
    @RequestMapping(value = "getAllBranches", method = RequestMethod.GET, produces = "application/json")
    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    //Сервис получения всех детей по имени и фамилии родителя
    @RequestMapping(value = "getPeopleByParentName", method = RequestMethod.GET, produces = "application/json")
    public List<People> getPeopleByParentName(@RequestParam String parentFirstName,
                                                @RequestParam String parentLastName) {
        //Поиск человека по имени и фамилии
        People parent = peopleRepository.findByParam(parentFirstName, parentLastName).
                orElseThrow(() -> new ResourceNotFoundException("Parent is not found"));

        List<People> children = new ArrayList<>();

        //Сбор всех веток человека, где человек является родителем
        List<Branch> branches = branchRepository.findByParentId(parent.getId());

        //Добавление детей в список
        branches.forEach(branch -> {
            children.add(peopleRepository.findById(branch.getChildId()).
                    orElseThrow(() -> new ResourceNotFoundException("Child is not found")));
        });
        return children;
    }

    //Сервис создания ветки и нового человека в иерархии
    @RequestMapping(value = "createBranchAndChild", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Boolean> createBranchAndChild(@RequestBody Map<String, String> request) {
        Map<String, Boolean> responce = new HashMap<>();

        //Поиск родителя (уже созданного) по передаваемым параметрам: имя и фамилия
        People parent = peopleRepository.
                findByParam(request.get("parentFirstName"), request.get("parentLastName")).
                orElseThrow(() -> new ResourceNotFoundException("Parent is not found"));

        //Проверка на создание одинаковых людей
        if (peopleRepository.findByParam(request.get("childFirstName"), request.get("childLastName")).isPresent()) {
            responce.put("Данный человек уже есть в базе, дайте ребенку второе имя через (-)", Boolean.FALSE);
            return responce;
        }
        //Создание нового человека по передаваемым параметрам: имя и фамилия
        People child = new People(request.get("childFirstName"), request.get("childLastName"));

        //Проверка на добавление созданного человека в таблицу БД
        if (peopleRepository.insert(child) > 0) {

            //Переприсвоение для получения id созданного человека
            child = peopleRepository.
                    findById(peopleRepository.findAll().get(peopleRepository.findAll().size() - 1).getId()).
                    orElseThrow(() -> new ResourceNotFoundException("Child is not found"));

            //Создание новой ветки дерева
            Branch branch = new Branch(parent.getId(), child.getId());

            //Проверка на добавление в таблицу БД
            Boolean aBoolean = branchRepository.insert(branch) > 0 ?
                    responce.put("created", Boolean.TRUE) :
                    responce.put("created", Boolean.FALSE);
        }
        else responce.put("child created", Boolean.FALSE);
        return responce;
    }

    //Сервис удаления веток и людей по имени и фамилии родителя
    @RequestMapping(value = "deleteBranchesAndPeople", method = RequestMethod.DELETE, produces = "application/json")
    public Map<String, Boolean> deleteBranchesAndPeople(@RequestParam String parentFirstName,
                                                        @RequestParam String parentLastName) {
        //Поиск человека по имени и фамилии
        People parent = peopleRepository.findByParam(parentFirstName, parentLastName).
                orElseThrow(() -> new ResourceNotFoundException("Parent is not found"));

        //Сбор всех веток человека, где человек является родителем
        List<Branch> branches = branchRepository.findByParentId(parent.getId());

        Map<String, Boolean> response = new HashMap<>();

        branches.forEach(branch -> {
            //Поиск человека, являющегося ребенком найденного родителя, по id
            People child = peopleRepository.findById(branch.getChildId()).
                    orElseThrow(() -> new ResourceNotFoundException("Child is not found"));

            //Проверка удаления найденного ребенка
            if (peopleRepository.deleteById(child.getId()) > 0) {
                //Проверка удаления ветки, где найден ребенок
                Boolean aBoolean = branchRepository.deleteById(branch.getId()) > 0 ?
                                response.put("deleted", Boolean.TRUE) : response.put("deleted", Boolean.FALSE);
            } else response.put("child deleted", Boolean.FALSE);
        });

        //Проверка удаления родителя
        if (peopleRepository.deleteById(parent.getId()) > 0) {
            //Проверка удаления ветки, где найденный родитель является ребенком
            Boolean aBoolean = branchRepository.deleteById(branchRepository.findByChildId(parent.getId()).
                    orElseThrow(() -> new ResourceNotFoundException("Branch is not found")).getId()) > 0 ?
                    response.put("deleted", Boolean.TRUE) : response.put("deleted", Boolean.FALSE);
        }
        else response.put("deleted", Boolean.FALSE);
        return response;
    }
}
