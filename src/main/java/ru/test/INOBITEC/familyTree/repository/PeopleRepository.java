package ru.test.INOBITEC.familyTree.repository;

import org.apache.ibatis.annotations.*;
import ru.test.INOBITEC.familyTree.model.People;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PeopleRepository {

    @Select("SELECT * FROM people")
    @Results({
            @Result(property = "firstName", column = "first_name"),
            @Result(property = "lastName", column = "last_name"),
            @Result(property = "familyId", column = "family_id")
    })
    List<People> findAll();

    @Select("SELECT * FROM people WHERE id = #{id}")
    @Results({
            @Result(property = "firstName", column = "first_name"),
            @Result(property = "lastName", column = "last_name"),
            @Result(property = "familyId", column = "family_id")
    })
    Optional<People> findById(Integer id);


    //@Delete("DELETE FROM people WHERE id = #{id}")
    //int deleteById(Integer id);

    @Insert("INSERT INTO people(first_name, last_name, family_id) " +
            " VALUES (#{firstName}, #{lastName}, #{familyId})")
    int insert(People person);

}
