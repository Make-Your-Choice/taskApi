package com.example.taskApi.repository;

import com.example.taskApi.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository  extends JpaRepository<Tag, Integer> {
    /*@Query(value = "SELECT t.id, t.name FROM tag t " +
            "LEFT JOIN task ts ON ts.id_tag = t.id " +
            "LEFT JOIN task_type tt ON ts.id_task_type = tt.id " +
            "ORDER BY tt.id DESC", nativeQuery = true)
    List<Tag> findAllSorted();

    @Query(value = "SELECT t.id, t.name FROM tag t " +
            "INNER JOIN task ts ON ts.id_tag = t.id " +
            "INNER JOIN task_type tt ON ts.id_task_type = tt.id " +
            "ORDER BY tt.id DESC", nativeQuery = true)
    List<Tag> findAllWithTasksSorted();*/

    /*@Query(value = "SELECT t.id, t.name FROM tag t " +
            "INNER JOIN task ts ON ts.id_tag = t.id " +
            "INNER JOIN task_type tt ON ts.id_task_type = tt.id " +
            "WHERE t.id = :id_value ORDER BY tt.id DESC", nativeQuery = true)
    Tag findByIdSorted(@Param("id_value") int id);*/
}
