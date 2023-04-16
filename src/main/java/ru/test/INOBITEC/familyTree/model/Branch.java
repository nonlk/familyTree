package ru.test.INOBITEC.familyTree.model;

public class Branch {

    private Integer id;
    private Integer parentId;
    private Integer childId;

    public Branch(Integer id, Integer parentId, Integer childId) {
        this.id = id;
        this.parentId = parentId;
        this.childId = childId;
    }

    public Branch() {
    }

    public Branch(Integer parentId, Integer childId) {
        this.parentId = parentId;
        this.childId = childId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getChildId() {
        return childId;
    }

    public void setChildId(Integer childId) {
        this.childId = childId;
    }
}

