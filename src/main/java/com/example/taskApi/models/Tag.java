package com.example.taskApi.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "tag")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "name")
    private String name;
    @OneToMany(mappedBy = "tag", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = true)
    List<Task> tasks;

    public Tag() {
        this.tasks = new ArrayList<>();
    }

    public Tag(String name) {
        this.name = name;
        this.tasks = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addTask(Task task) {
        this.tasks.add(task);
        task.setTag(this);
    }

    public void removeTask(Task task) {
        this.tasks.remove(task);
        task.setTag(null);
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void sortTasksDesc() {
        Collections.sort(tasks, Comparator.comparing(Task::getTypeId).reversed());
    }


}
