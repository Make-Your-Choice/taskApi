package com.example.taskApi;

import com.example.taskApi.models.Tag;
import com.example.taskApi.models.Task;
import com.example.taskApi.models.TaskType;
import com.example.taskApi.repository.TagRepository;
import com.example.taskApi.repository.TaskRepository;
import com.example.taskApi.repository.TaskTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class Controller {
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    TaskTypeRepository taskTypeRepository;
    @Autowired
    TagRepository tagRepository;

    /**
     * выводит список всех задач
     *
     * @return список задач в json
     */
    @GetMapping("/tasks")
    public ResponseEntity<List<Task>> getAllTasks() {
        try {
            List<Task> tasks = new ArrayList<>(taskRepository.findAll());
            if (tasks.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * выводит список всех задач на заданную дату с сортировкой по приоритету
     *
     * @return список задач в json
     */
    @GetMapping("/tasks/date")
    public ResponseEntity<List<Task>> getTasksByDate(@RequestBody String dateString) {
        try {
            dateString = dateString.replace("\"", "");
            List<Task> tasks = new ArrayList<>(taskRepository.findByDateWithTypesSorted(dateString + " 00:00:00", dateString + " 23:59:59"));
            if (tasks.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * выводит задачу по ее id
     *
     * @param id id задачи
     * @return найденная задача
     */
    @GetMapping("/tasks/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable("id") int id) {
        Optional<Task> taskData = taskRepository.findById(id);
        if (taskData.isPresent()) {
            return new ResponseEntity<>(taskData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * создает новую задачу (без типа)
     *
     * @param task новая задача
     * @return новая задача
     */
    @PostMapping("/tasks")
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        try {
            if(task.getDate().after(new Date())) {
                throw new DataFormatException();
            }
            Task task1 = taskRepository
                    .save(new Task(task.getName(), task.getDescription(), task.getDate()));
            return new ResponseEntity<>(task1, HttpStatus.CREATED);
        } catch (DataFormatException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e1) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * обновляет данные задачи по ее id (без типа)
     *
     * @param id id задачи
     * @param task задача с обновленными параметрами
     * @return обновленная задача
     */
    @PutMapping("/tasks/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable("id") int id, @RequestBody Task task) {
        try {
            if(task.getDate().after(new Date())) {
                throw new DataFormatException();
            }
        } catch (DataFormatException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<Task> taskData = taskRepository.findById(id);
        if (taskData.isPresent()) {
            Task task1 = taskData.get();
            task1.setName(task.getName());
            task1.setDescription(task.getDescription());
            task1.setDate(task.getDate());
            return new ResponseEntity<>(taskRepository.save(task1), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * обновляет тип задачи по ее id и id типа
     *
     * @param id id задачи
     * @param idType id типа
     * @return обновленная задача
     */
    @PutMapping("/tasks/type/id/{id}")
    public ResponseEntity<Task> updateTaskTypeById(@PathVariable("id") int id, @RequestBody int idType) {
        Optional<Task> taskData = taskRepository.findById(id);
        Optional<TaskType> taskType = taskTypeRepository.findById(idType);

        if (taskData.isPresent() && taskType.isPresent()) {
            Task task1 = taskData.get();
            TaskType type1 = taskType.get();
            task1.setType(type1);
            return new ResponseEntity<>(taskRepository.save(task1), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * обновляет тип задачи по ее id и имени типа
     *
     * @param id id задачи
     * @param typeName id типа
     * @return обновленная задача
     */
    /*@PutMapping("/tasks/type/name/{id}")
    public ResponseEntity<Task> updateTaskTypeByName(@PathVariable("id") int id, @RequestBody String typeName) {
        Optional<Task> taskData = taskRepository.findById(id);
        TaskType type1 = taskTypeRepository.getByName(typeName.substring(1, typeName.length() - 1));

        if (taskData.isPresent() && type1 != null) {
            Task task1 = taskData.get();
            task1.setType(type1);
            return new ResponseEntity<>(taskRepository.save(task1), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }*/

    /**
     * удаляет задачу по ее id
     *
     * @param id id задачи
     * @return пустую страницу
     */
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<HttpStatus> deleteTask(@PathVariable("id") int id) {
        try {
            taskRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * удаляет все задачи
     *
     * @return пустую страницу
     */
    @DeleteMapping("/tasks")
    public ResponseEntity<HttpStatus> deleteAllTasks() {
        try {
            taskRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * выводит список всех тегов
     *
     * @return список тегов в json
     */
    @GetMapping("/tags")
    public ResponseEntity<List<Tag>> getAllTags() {
        try {
            List<Tag> tags = new ArrayList<>(tagRepository.findAll());
            if (tags.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(tags, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * выводит список всех тегов, у которых есть задача
     *
     * @return список тегов в json
     */
    @GetMapping("/tags/tasks")
    public ResponseEntity<List<Tag>> getTagsWithTasks() {
        try {
            List<Tag> tags = new ArrayList<>(tagRepository.findAll().stream()
                    .distinct()
                    .filter(tag -> (!tag.getTasks().isEmpty()))
                    .collect(Collectors.toList()));
            if (tags.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(tags, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * выводит тег по его id
     *
     * @param id id тега
     * @return найденный тег
     */
    @GetMapping("/tags/{id}")
    public ResponseEntity<Tag> getTagById(@PathVariable("id") int id) {
        Optional<Tag> tagData = tagRepository.findById(id);
        if (tagData.isPresent()) {
            Tag tag = tagData.get();
            tag.sortTasksDesc();
            return new ResponseEntity<>(tag, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * создает новый тег
     *
     * @param tag новый тег
     * @return новый тег
     */
    @PostMapping("/tags")
    public ResponseEntity<Tag> createTag(@RequestBody Tag tag) {
        try {
            Tag tag1 = tagRepository.save(new Tag(tag.getName()));
            return new ResponseEntity<>(tag1, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * обновляет имя тега по его id
     *
     * @param id id тега
     * @param tag тег с обновленным именем
     * @return обновленный тег
     */
    @PutMapping("/tags/{id}")
    public ResponseEntity<Tag> updateTag(@PathVariable("id") int id, @RequestBody Tag tag) {
        Optional<Tag> tagData = tagRepository.findById(id);
        if (tagData.isPresent()) {
            Tag tag1 = tagData.get();
            tag1.setName(tag.getName());
            return new ResponseEntity<>(tagRepository.save(tag1), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * присваивает тег задаче по его id и id задачи
     *
     * @param id id тега
     * @param idTask id задачи
     * @return обновленный тег
     */
    @PutMapping("/tags/task/{id}")
    public ResponseEntity<Tag> addTagToTask(@PathVariable("id") int id, @RequestBody int idTask) {
        Optional<Tag> tagData = tagRepository.findById(id);
        Optional<Task> task = taskRepository.findById(idTask);

        if (tagData.isPresent() && task.isPresent()) {
            Tag tag1 = tagData.get();
            Task task1 = task.get();
            tag1.addTask(task1);
            return new ResponseEntity<>(tagRepository.save(tag1), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * удаляет тег у задачи по его id и id задачи
     *
     * @param id id тега
     * @param idTask id задачи
     * @return обновленный тег
     */
    @DeleteMapping("/tags/task/{id}")
    public ResponseEntity<Tag> removeTagFromTask(@PathVariable("id") int id, @RequestBody int idTask) {
        Optional<Tag> tagData = tagRepository.findById(id);
        Optional<Task> task = taskRepository.findById(idTask);

        if (tagData.isPresent() && task.isPresent()) {
            Tag tag1 = tagData.get();
            Task task1 = task.get();
            tag1.removeTask(task1);
            return new ResponseEntity<>(tagRepository.save(tag1), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * удаляет тег по его id
     *
     * @param id id тега
     * @return пустую страницу
     */
    @DeleteMapping("/tags/{id}")
    public ResponseEntity<HttpStatus> deleteTag(@PathVariable("id") int id) {
        try {
            tagRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
