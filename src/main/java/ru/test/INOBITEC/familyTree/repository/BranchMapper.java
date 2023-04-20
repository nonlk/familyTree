package ru.test.INOBITEC.familyTree.repository;

import org.apache.ibatis.annotations.*;
import ru.test.INOBITEC.familyTree.model.Branch;

import java.util.List;
import java.util.Optional;

@Mapper
public interface BranchMapper {

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

    @Select("SELECT * FROM branch WHERE child_id = #{childId}")
    @Results({
            @Result(property = "parentId", column = "parent_id"),
            @Result(property = "childId", column = "child_id")
    })
    Optional<Branch> findByChildId(Integer childId);

    @Select("SELECT * FROM branch WHERE parent_id = #{parentId}")
    @Results({
            @Result(property = "parentId", column = "parent_id"),
            @Result(property = "childId", column = "child_id")
    })
    List<Branch> findByParentId(Integer parentId);
}
