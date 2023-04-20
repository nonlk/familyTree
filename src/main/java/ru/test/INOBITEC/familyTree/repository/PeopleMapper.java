package ru.test.INOBITEC.familyTree.repository;

import org.apache.ibatis.annotations.*;
import ru.test.INOBITEC.familyTree.model.People;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PeopleMapper {

    @Select("SELECT * FROM people")
    @Results({
            @Result(property = "firstName", column = "first_name"),
            @Result(property = "lastName", column = "last_name")
    })
    List<People> findAll();

    @Select("SELECT * FROM people WHERE id = #{id}")
    @Results({
            @Result(property = "firstName", column = "first_name"),
            @Result(property = "lastName", column = "last_name")
    })
    Optional<People> findById(Integer id);

    @Select("SELECT * FROM people " +
            " WHERE first_name = #{firstName} AND last_name = #{lastName}")
    @Results({
            @Result(property = "firstName", column = "first_name"),
            @Result(property = "lastName", column = "last_name")
    })
    Optional<People> findByParam(String firstName, String lastName);

    @Delete("DELETE FROM people WHERE id = #{id}")
    int deleteById(Integer id);

    @Insert("INSERT INTO people(first_name, last_name) " +
            " VALUES (#{firstName}, #{lastName})")
    int insert(People person);

}
