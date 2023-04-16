package ru.test.INOBITEC.familyTree.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import ru.test.INOBITEC.familyTree.exception.ResourceNotFoundException;
import ru.test.INOBITEC.familyTree.model.Branch;
import ru.test.INOBITEC.familyTree.model.People;
import ru.test.INOBITEC.familyTree.repository.BranchRepository;
import ru.test.INOBITEC.familyTree.repository.PeopleRepository;

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

    @GetMapping("/branch")
    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    @RequestMapping(value = "createBranch", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Boolean> createBranch(@RequestBody Map<String, String> request) {
        People parent = peopleRepository.findByParam(request.get("parentFirstName"), request.get("parentLastName")).
                orElseThrow(() -> new ResourceNotFoundException("Parent is not found"));
        People child = peopleRepository.findByParam(request.get("childFirstName"), request.get("childLastName")).
                orElseThrow(() -> new ResourceNotFoundException("Child is not found"));
        Branch branch = new Branch(parent.getId(), child.getId());
        Map<String, Boolean> responce = new HashMap<>();
        Boolean aBoolean = branchRepository.insert(branch) > 0 ?
                responce.put("created", Boolean.TRUE) :
                responce.put("created", Boolean.FALSE);
        return responce;
    }
}
