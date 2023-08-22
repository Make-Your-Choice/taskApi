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
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@SpringBootTest
@WebMvcTest(Controller.class)
@ContextConfiguration(classes=TaskApiApplication.class)
class TaskApiApplicationTests {

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
        //mockMvc.perform((get("/api/tasks/{id}", id)));
        mockMvc.perform(get("/api/tasks/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(task.getName()))
                .andExpect(jsonPath("$.description").value(task.getDescription()))
                .andExpect(jsonPath("$.date").isNotEmpty())
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
        String date = new SimpleDateFormat("yyyy-MM-dd").format(tasks.get(0).getDate());
        when(taskRepository.findByDateWithTypesSorted((date + " 00:00:00").replace("\"", ""), (date + " 23:59:59").replace("\"", "")))
                .thenReturn(tasks);
        mockMvc.perform(get("/api/tasks/date").content(objectMapper.writeValueAsString(date).replace("\"", "")))
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

    /**
     * тестирует успешное обновление полей задачи (без типа) по ее id
     *
     * @throws Exception
     */
    @Test
    void testSuccessUpdateTaskWithCorrectId() throws Exception {
        int id = 0;
        Task task = new Task("имя", "описание", new Date());
        task.setType(new TaskType());
        Task taskUpdated = new Task("имя1", "описание1", new Date());
        taskUpdated.setType(task.getType());
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(taskUpdated);
        mockMvc.perform(put("/api/tasks/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskUpdated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(taskUpdated.getName()))
                .andExpect(jsonPath("$.description").value(taskUpdated.getDescription()))
                .andExpect(jsonPath("$.date").isNotEmpty())
                .andDo(print());
    }

    /**
     * тестирует успешное обновление типа задачи по его id и id задачи
     *
     * @throws Exception
     */
    @Test
    void testSuccessUpdateTaskTypeInTaskById() throws Exception {
        int id = 0;
        int idType = 1;
        String nameType = "тип";
        Task task = new Task("имя", "описание", new Date());
        task.setType(new TaskType());
        TaskType type = new TaskType();
        type.setName(nameType);
        type.setId(idType);
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(taskTypeRepository.findById(idType)).thenReturn(Optional.of(type));
        task.setType(type);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        mockMvc.perform(put("/api/tasks/type/id/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(type.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type.id").value(type.getId()))
                .andExpect(jsonPath("$.type.name").value(type.getName()))
                .andDo(print());
    }

    /**
     * тестирует обновление полей несуществующей задачи (без типа) по ее id
     *
     * @throws Exception
     */
    @Test
    void testFailUpdateTaskWithIncorrectId() throws Exception {
        int id = 0;
        Task taskUpdated = new Task("имя1", "описание1", new Date());
        taskUpdated.setType(new TaskType());
        when(taskRepository.findById(id)).thenReturn(Optional.empty());
        when(taskRepository.save(any(Task.class))).thenReturn(taskUpdated);
        mockMvc.perform(put("/api/tasks/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskUpdated)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    /**
     * тестирует успешное удаление задачи по ее id
     *
     * @throws Exception
     */
    @Test
    void testSuccessDeleteTask() throws Exception {
        int id = 0;
        Task task = new Task("имя", "описание", new Date());
        doNothing().when(taskRepository).deleteById(id);
        mockMvc.perform(delete("/api/tasks/{id}", id))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    /**
     * тестирует успешное удаление всех задач
     *
     * @throws Exception
     */
    @Test
    void testSuccessDeleteAllTasks() throws Exception {
        List<Task> tasks = new ArrayList<>();
        fillTasks(tasks);
        doNothing().when(taskRepository).deleteAll();
        mockMvc.perform(delete("/api/tasks"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    /**
     * тестирует успешное создание нового тега
     *
     * @throws Exception
     */
    @Test
    void testSuccessCreateTag() throws Exception {
        Tag tag = new Tag("имя");
        mockMvc.perform(post("/api/tags").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tag)))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    /**
     * тестирует успешный поиск тега по его id
     *
     * @throws Exception
     */
    @Test
    void testSuccesGetTagByCorrectId() throws Exception {
        int id = 0;
        Tag tag = new Tag("имя");
        when(tagRepository.findById(id)).thenReturn(Optional.of(tag));
        //mockMvc.perform((get("/api/tags/{id}", id)));
        mockMvc.perform(get("/api/tags/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(tag.getName()))
                .andDo(print());
    }

    /**
     * тестирует поиск тега по id в пустой базе
     *
     * @throws Exception
     */
    @Test
    void testFailGetTagByIncorrectId() throws Exception {
        int id = 0;
        when(tagRepository.findById(id)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/tags/{id}", id))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    /**
     * тестирует успешный вывод всех тегов
     *
     * @throws Exception
     */
    @Test
    void testSuccessGetAllTags() throws Exception {
        List<Tag> tags = new ArrayList<>();
        fillTags(tags);
        when(tagRepository.findAll()).thenReturn(tags);
        mockMvc.perform(get("/api/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(tags.size()))
                .andDo(print());
    }

    /**
     * тестирует успешный вывод всех тегов, у которых есть задача
     *
     * @throws Exception
     */
    @Test
    void testSuccessGetAllTagsThatHaveTasks() throws Exception {
        List<Tag> tags = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();
        fillTags(tags);
        fillTasks(tasks);
        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).setTag(tags.get(0));
            tags.get(0).addTask(tasks.get(i));
        }
        when(tagRepository.findAll()).thenReturn(tags);
        tags = new ArrayList<>(tags.stream()
                .distinct()
                .filter(tag -> (!tag.getTasks().isEmpty()))
                .collect(Collectors.toList())
        );
        mockMvc.perform(get("/api/tags/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(tags.size()))
                .andDo(print());
    }

    public void fillTasks(List<Task> tasks) {
        for (int i = 0; i < 3; i++) {
            Task task = new Task("имя" + i, "описание" + i, new Date());
            task.setId(i);
            task.setType(new TaskType());
            tasks.add(task);
        }
    }

    public void fillTags(List<Tag> tags) {
        for (int i = 0; i < 3; i++) {
            Tag tag = new Tag("имя" + i);
            tags.add(tag);
        }
    }
}
