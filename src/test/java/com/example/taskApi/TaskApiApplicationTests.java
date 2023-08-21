package com.example.taskApi;

import com.example.taskApi.models.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.Date;

@SpringBootTest
class TaskApiApplicationTests {

    @Autowired
    Controller controller;

    @Test
    void contextLoads() {
    }

    @Test
    void testSuccessCreateTask() throws ParseException {
        Task task = new Task("имя", "описание", new Date());
        ResponseEntity<Task> taskResponseEntity = new ResponseEntity<>(task, HttpStatus.CREATED);
        ResponseEntity<Task> taskResponseEntity1 = controller.createTask(task);
        Assertions.assertEquals(taskResponseEntity.getBody().getName(), taskResponseEntity1.getBody().getName());
        Assertions.assertEquals(taskResponseEntity.getBody().getDescription(), taskResponseEntity1.getBody().getDescription());
        Assertions.assertEquals(taskResponseEntity.getBody().getDate(), taskResponseEntity1.getBody().getDate());
        controller.deleteTask(taskResponseEntity1.getBody().getId());
    }

}
