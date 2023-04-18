package ru.test.INOBITEC.familyTree.model;

import java.util.List;

public class Tree {

    private Integer id;
    private Integer rootPersonId;
    private List<Branch> branches;

    public Tree() {
    }

    public Tree(Integer id, Integer rootPersonId, List<Branch> branches) {
        this.id = id;
        this.rootPersonId = rootPersonId;
        this.branches = branches;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRootPersonId() {
        return rootPersonId;
    }

    public void setRootPersonId(Integer rootPersonId) {
        this.rootPersonId = rootPersonId;
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }
}
