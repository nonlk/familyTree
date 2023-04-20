package ru.test.INOBITEC.familyTree.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.test.INOBITEC.familyTree.exception.ResourceNotFoundException;
import ru.test.INOBITEC.familyTree.model.People;
import ru.test.INOBITEC.familyTree.model.Tree;
import ru.test.INOBITEC.familyTree.repository.BranchMapper;
import ru.test.INOBITEC.familyTree.repository.TreeMapper;
import ru.test.INOBITEC.familyTree.service.BranchService;
import ru.test.INOBITEC.familyTree.service.PeopleService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/familyTree")
public class TreeController {
    @Autowired
    private TreeMapper treeMapper;
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private BranchService branchService;
    @Autowired
    private PeopleService peopleService;

    //Тестовый сервис получения всех ветвей для проверки работы бэка
    @RequestMapping(value = "getAllTrees", method = RequestMethod.GET, produces = "application/json")
    public List<Tree> getAllTrees() {
        return treeMapper.findAll();
    }

    //Сервис создания корневого человека и нового дерева, соответственно
    @RequestMapping(value = "createRootPerson", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Boolean> createRootPerson(@RequestBody Map<String, String> request) {
        Map<String, Boolean> responce = peopleService.createPerson(request);
        if (responce.get("created") == Boolean.TRUE) {
            Integer rootPersonId = peopleService.getPersonId(request.get("firstName"), request.get("lastName"));
            Boolean aBoolean = treeMapper.insert(rootPersonId) > 0 ?
                    responce.put("created", Boolean.TRUE) :
                    responce.put("created", Boolean.FALSE);
        }
        else responce.put("Данный человек уже есть в базе, измените имя или фамилию", Boolean.FALSE);
        return responce;
    }


    //Сервис получения всех корневых людей созданных деревьев в формате "Имя + Фамилия"
    @RequestMapping(value = "getAllTreesPeople", method = RequestMethod.GET, produces = "application/json")
    public List<String> getAllTreesPeople() {
        List<Tree> trees = treeMapper.findAll();
        List<String> peopleNames = new ArrayList<>();
        trees.forEach(tree -> {
            peopleService.getPersonById(tree.getRootPersonId());
            peopleNames.add(peopleService.getPersonById(tree.getRootPersonId()).getFirstName() +
                    " " + peopleService.getPersonById(tree.getRootPersonId()).getLastName());
        });
        return peopleNames;
    }



    //Сервис получения детей по id родителя в строчном формате "Имя + Фамилия"
    @RequestMapping(value = "getChildrenByPersonId", method = RequestMethod.GET)
    public List<String> getChildrenByPersonId (@RequestParam Integer personId) {
        List<People> children = branchService.getChildrenByParentId(personId);
        List<String> childrenName = new ArrayList<>();
        children.forEach(child -> childrenName.add(child.getFirstName() + " " + child.getLastName()));
        return childrenName;
    }

    /*
      Сервис удаления любого человека по его имени и фамилии.
      Если человек является корневым в дереве, то удаляется все дерево.
      В противном случае удаляются только этот человек, его потомки и все ветки, связанные с человеком
     */
    @RequestMapping(
            value = "deletePeopleAndBranchesOrTreeByPersonParams",
            method = RequestMethod.DELETE,
            produces = "application/json")
    public Map<String, Boolean> deletePeopleAndBranchesOrTreeByPersonParams (@RequestParam String parentFirstName,
                                                                             @RequestParam String parentLastName) {
        Map<String, Boolean> responce = branchService.deleteBranchesAndPeople(parentFirstName, parentLastName);

        //Проверка на удаление потомков человека
        if (responce.get("deleted") == Boolean.TRUE) {
            People parent = peopleService.getPersonById(peopleService.getPersonId(parentFirstName, parentLastName));

            //Проверка на удаление самого родителя
            if (peopleService.deleteById(parent.getId()) > 0) {

                //Проверка: является ли человек корневым. Не является - id дерева = null
                Tree tree = Optional.ofNullable(treeMapper.findByRootPersonId
                        (parent.getId())).orElseGet(() -> new Tree(null, null));

                //Если человек является корневым
                if (tree.getId() != null) {
                    //Удаление дерева
                    Boolean aBoolean = treeMapper.deleteById(tree.getId()) > 0 ?
                            responce.put("deleted", Boolean.TRUE) :
                            responce.put("deleted", Boolean.FALSE);
                }
                else {
                    //Удаление ветки, где он является ребенком
                    Boolean aBoolean = branchMapper.deleteById
                            (branchMapper.findByChildId(parent.getId()).
                                    orElseThrow(() -> new ResourceNotFoundException("Branch is not found")).getId()) > 0 ?
                            responce.put("deleted", Boolean.TRUE) :
                            responce.put("deleted", Boolean.FALSE);
                }
            }
        }
        return responce;
    }
}
