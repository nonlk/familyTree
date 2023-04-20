package ru.test.INOBITEC.familyTree.model;

public class Tree {

    private Integer id;
    private Integer rootPersonId;

    public Tree() {
    }

    public Tree(Integer id, Integer rootPersonId) {
        this.id = id;
        this.rootPersonId = rootPersonId;
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

}
