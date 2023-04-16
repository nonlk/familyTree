package ru.test.INOBITEC.familyTree.repository;

import org.apache.ibatis.annotations.*;
import ru.test.INOBITEC.familyTree.model.Branch;

import java.util.List;

@Mapper
public interface BranchRepository {

    @Select("SELECT * FROM branch")
    @Results({
            @Result(property = "parentId", column = "parent_id"),
            @Result(property = "childId", column = "child_id")
    })
    List<Branch> findAll();

    @Delete("DELETE FROM branch WHERE id = #{id}")
    int deleteById(Integer Id);

    @Insert("INSERT INTO branch(parent_id, child_id)"
            + "VALUES (#{parentId}, #{childId})")
    int insert(Branch branch);
}
