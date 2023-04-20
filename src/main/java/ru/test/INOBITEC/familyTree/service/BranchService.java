package ru.test.INOBITEC.familyTree.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.test.INOBITEC.familyTree.exception.ResourceNotFoundException;
import ru.test.INOBITEC.familyTree.model.Branch;
import ru.test.INOBITEC.familyTree.model.People;
import ru.test.INOBITEC.familyTree.repository.BranchMapper;
import ru.test.INOBITEC.familyTree.repository.PeopleMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BranchService {
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private PeopleMapper peopleMapper;

    //Метод для получения списка всех потомков по id родителя
    private List<People> searchChildren(List<People> childList, Integer parentId) {
        if (peopleMapper.findById(parentId).isPresent()) {
            List<Branch> branches = branchMapper.findByParentId(parentId);
            branches.forEach(branch ->  {
                childList.add(peopleMapper.findById(branch.getChildId()).
                        orElseThrow(() -> new ResourceNotFoundException("child is not found")));
                searchChildren(childList, childList.get(childList.size() - 1).getId());
            });
        }
        return childList;
    }

    //Сервис получения всех детей по id родителя
    public List<People> getChildrenByParentId(Integer parentId) {
        //Поиск человека по id
        People parent = peopleMapper.findById(parentId).
                orElseThrow(() -> new ResourceNotFoundException("Parent is not found"));

        List<People> children = new ArrayList<>();

        //Сбор всех веток человека, где человек является родителем
        List<Branch> branches = branchMapper.findByParentId(parent.getId());

        //Добавление детей в список
        branches.forEach(branch -> children.add(peopleMapper.findById(branch.getChildId()).
                orElseThrow(() -> new ResourceNotFoundException("Child is not found"))));
        return children;
    }

    //Сервис удаления веток и людей по имени и фамилии родителя
    public Map<String, Boolean> deleteBranchesAndPeople(String parentFirstName, String parentLastName) {
        //Поиск человека по имени и фамилии
        People parent = peopleMapper.findByParam(parentFirstName, parentLastName).
                orElseThrow(() -> new ResourceNotFoundException("Parent is not found"));

        //Сбор всех потомков найденного человека
        List<People> children = new ArrayList<>();
        searchChildren(children, parent.getId());

        Map<String, Boolean> response = new HashMap<>();

        //Удаление найденных потомков и веток, в которых они указаны
        children.forEach(child -> {
            if (peopleMapper.deleteById(child.getId()) > 0) {
                //Проверка удаления ветки, где найден потомок
                Boolean aBoolean = branchMapper.deleteById(branchMapper.findByChildId(child.getId()).
                        orElseThrow(() -> new ResourceNotFoundException("Child branch is not found")).getId()) > 0 ?
                        response.put("deleted", Boolean.TRUE) : response.put("deleted", Boolean.FALSE);
            } else response.put("deleted", Boolean.FALSE);
        });
        return response;
    }
}
