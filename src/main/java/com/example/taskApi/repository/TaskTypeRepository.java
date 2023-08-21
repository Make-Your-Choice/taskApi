package com.example.taskApi.repository;

import com.example.taskApi.models.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskTypeRepository extends JpaRepository<TaskType, Integer> {
    TaskType getByName(String name);
}
