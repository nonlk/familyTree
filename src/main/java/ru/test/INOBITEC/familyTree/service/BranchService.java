package ru.test.INOBITEC.familyTree.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.test.INOBITEC.familyTree.exception.ResourceNotFoundException;
import ru.test.INOBITEC.familyTree.model.Branch;
import ru.test.INOBITEC.familyTree.model.People;
import ru.test.INOBITEC.familyTree.repository.BranchRepository;
import ru.test.INOBITEC.familyTree.repository.PeopleRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BranchService {
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private PeopleRepository peopleRepository;

    //Метод для получения списка всех потомков по id удаляемого родителя
    private List<People> searchChildren(List<People> childList, Integer parentId) {
        if (peopleRepository.findById(parentId).isPresent()) {
            List<Branch> branches = branchRepository.findByParentId(parentId);
            for (int i = 0; i < branches.size(); i++) {
                childList.add(peopleRepository.findById(branches.get(i).getChildId()).
                        orElseThrow(() -> new ResourceNotFoundException("child is not found")));
                searchChildren(childList, childList.get(childList.size() - 1).getId());
            }
        }
        return childList;
    }

    //Сервис получения всех созданных веток
    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    //Сервис получения всех детей по имени и фамилии родителя
    public List<People> getChildrenByParentName(String parentFirstName, String parentLastName) {
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
    public Map<String, Boolean> createBranchAndChild(Map<String, String> request) {
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
    public Map<String, Boolean> deleteBranchesAndPeople(String parentFirstName, String parentLastName) {
        //Поиск человека по имени и фамилии
        People parent = peopleRepository.findByParam(parentFirstName, parentLastName).
                orElseThrow(() -> new ResourceNotFoundException("Parent is not found"));

        //Сбор всех потомков найденного человека
        List<People> children = new ArrayList<>();
        children = searchChildren(children, parent.getId());

        Map<String, Boolean> response = new HashMap<>();

        //Удаление найденных потомков и веток, в которых они указаны
        children.forEach(child -> {
            if (peopleRepository.deleteById(child.getId()) > 0) {
                //Проверка удаления ветки, где найден потомок
                Boolean aBoolean = branchRepository.deleteById(branchRepository.findByChildId(child.getId()).
                        orElseThrow(() -> new ResourceNotFoundException("Child branch is not found")).getId()) > 0 ?
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
