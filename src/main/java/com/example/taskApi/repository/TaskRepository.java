package com.example.taskApi.repository;

import com.example.taskApi.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    @Query(value = "SELECT t.* FROM task t " +
            "INNER JOIN task_type tt ON t.id_task_type = tt.id " +
            "INNER JOIN tag tg ON t.id_tag = tg.id " +
            "WHERE t.date BETWEEN TO_TIMESTAMP(:date_value_start, 'yyyy-MM-dd HH24:MI:SS') " +
            "AND TO_TIMESTAMP(:date_value_end, 'yyyy-MM-dd HH24:MI:SS') ORDER BY tt.id DESC", nativeQuery = true)
    List<Task> findByDateWithTypesSorted(@Param("date_value_start") String date_start, @Param("date_value_end") String date_end);
}
