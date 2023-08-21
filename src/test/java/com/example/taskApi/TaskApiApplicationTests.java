package com.example.taskApi;

import com.example.taskApi.models.Tag;
import com.example.taskApi.models.Task;
import com.example.taskApi.models.TaskType;
import com.example.taskApi.repository.TagRepository;
import com.example.taskApi.repository.TaskRepository;
import com.example.taskApi.repository.TaskTypeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@SpringBootTest
@WebMvcTest(Controller.class)
@ContextConfiguration(classes=TaskApiApplication.class)
class TaskApiApplicationTests {

    /*@Autowired
    Controller controller;*/

    @MockBean
    private TaskRepository taskRepository;

    @MockBean
    private TaskTypeRepository taskTypeRepository;

    @MockBean
    private TagRepository tagRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void contextLoads() {
    }

    /*@Test
    void testSuccessCreateTask() throws ParseException {
        Task task = new Task("имя", "описание", new Date());
        ResponseEntity<Task> taskResponseEntity = new ResponseEntity<>(task, HttpStatus.CREATED);
        ResponseEntity<Task> taskResponseEntity1 = controller.createTask(task);
        Assertions.assertEquals(taskResponseEntity.getBody().getName(), taskResponseEntity1.getBody().getName());
        Assertions.assertEquals(taskResponseEntity.getBody().getDescription(), taskResponseEntity1.getBody().getDescription());
        Assertions.assertEquals(taskResponseEntity.getBody().getDate(), taskResponseEntity1.getBody().getDate());
        controller.deleteTask(taskResponseEntity1.getBody().getId());
    }*/

    /**
     * тестирует успешное создание новой задачи
     *
     * @throws Exception
     */
    @Test
    void testSuccessCreateTask() throws Exception {
        Task task = new Task("имя", "описание", new Date());
        task.setType(new TaskType());
        mockMvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    /**
     * тестирует создание новой задачи с некорректной датой (больше текущей)
     *
     * @throws Exception
     */
    @Test
    void testFailCreateTaskWithWrongDate() throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = "2222-12-12";
        Date date = formatter.parse(dateString);
        Task task = new Task("имя", "описание", date);
        task.setType(new TaskType());
        mockMvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    /**
     * тестирует успешный поиск задачи по ее id
     *
     * @throws Exception
     */
    @Test
    void testSuccesGetTaskByCorrectId() throws Exception {
        int id = 0;
        Task task = new Task("имя", "описание", new Date());
        task.setType(new TaskType());
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        mockMvc.perform((get("/api/tasks/{id}", id)));
        mockMvc.perform(get("/api/tasks/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(task.getName()))
                .andExpect(jsonPath("$.description").value(task.getDescription()))
                .andExpect(jsonPath("$.date").exists())
                .andDo(print());
    }

    /**
     * тестирует поиск задачи по id в пустой базе
     *
     * @throws Exception
     */
    @Test
    void testFailGetTaskByIncorrectId() throws Exception {
        int id = 0;
        when(taskRepository.findById(id)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/tasks/{id}", id))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    /**
     * тестирует успешный вывод всех задач
     *
     * @throws Exception
     */
    @Test
    void testSuccessGetAllTasks() throws Exception {
        List<Task> tasks = new ArrayList<>();
        fillTasks(tasks);
        when(taskRepository.findAll()).thenReturn(tasks);
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(tasks.size()))
                .andDo(print());
    }

    /*@Test
    void testGetTasksByDate() throws Exception {
        List<Task> tasks = new ArrayList<>();
        fillTasks(tasks);

        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).trim();
        when(taskRepository.findByDateWithTypesSorted(date + " 00:00:00", date + " 23:59:59"))
                .thenReturn(tasks);
        mockMvc.perform(get("/api/tasks/date").content(objectMapper.writeValueAsString(date)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(tasks.size()))
                .andDo(print());
    }*/

    /**
     * тестирует поиск задачи по дате в пустой базе
     *
     * @throws Exception
     */
    @Test
    void testFailGetTasksByDate() throws Exception {
        List<Task> tasks = new ArrayList<>();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).trim();
        when(taskRepository.findByDateWithTypesSorted(date + " 00:00:00", date + " 23:59:59"))
                .thenReturn(tasks);
        mockMvc.perform(get("/api/tasks/date").content(objectMapper.writeValueAsString(date)))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    public void fillTasks(List<Task> tasks) {
        for (int i = 0; i < 3; i++) {
            Task task = new Task("имя" + i, "описание" + i, new Date());
            task.setType(new TaskType());
            TaskType type = new TaskType();
            type.setId(i);
            type.setName("имя" + i);
            task.setType(type);
            tasks.add(task);
        }
    }
}
