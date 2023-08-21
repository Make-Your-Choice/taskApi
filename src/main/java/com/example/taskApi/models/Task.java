package com.example.taskApi.models;

import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "date")
    private Date date;
    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name="id_task_type", nullable = true)
    //@JsonIgnore
    private TaskType type;
    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name="id_tag", nullable = true)
    @JsonIgnore
    private Tag tag;

    public Task() {

    }

    public Task(String name, String description, Date date) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.type = null;
        this.tag = null;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public int getTypeId() {
        return type.getId();
    }
}
