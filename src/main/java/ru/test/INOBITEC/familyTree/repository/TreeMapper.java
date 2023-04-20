package ru.test.INOBITEC.familyTree.repository;

import org.apache.ibatis.annotations.*;
import ru.test.INOBITEC.familyTree.model.Tree;

import java.util.List;
import java.util.Optional;

@Mapper
public interface TreeMapper {

    @Select("SELECT * FROM tree")
    @Result(property = "rootPersonId", column = "root_person_id")
    List<Tree> findAll();

    @Insert("INSERT INTO tree(root_person_id)" +
            "VALUES (#{rootPersonId})")
    int insert(Integer rootPersonId);

    @Delete("DELETE FROM tree WHERE id = #{id}")
    int deleteById(Integer id);

    @Select("SELECT * FROM tree WHERE root_person_id = #{rootPersonId}")
    @Result(property = "rootPersonId", column = "root_person_id")
    Tree findByRootPersonId(Integer rootPersonId);
}
