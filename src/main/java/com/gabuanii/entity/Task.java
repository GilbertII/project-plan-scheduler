package com.gabuanii.entity;

import java.util.HashSet;
import java.util.Set;

public class Task {

    private Integer id;
    private String taskName;
    private Set<Integer> taskDependencies;
    private Integer duration;
    private String dateStart;
    private String dateEnd;

    public Task() {
        this.taskDependencies = new HashSet<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Set<Integer> getTaskDependencies() {
        return taskDependencies;
    }

    public void setTaskDependencies(Set<Integer> taskDependencies) {
        this.taskDependencies = taskDependencies;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    @Override
    public String toString() {
        return "\nTask: " +
                "\nid=" + id +
                "\ntaskName=" + taskName +
                "\nduration=" + duration +
                "\ndateStart=" + dateStart+
                "\ndateEnd=" + dateEnd +
                "\ntaskDependencies=" + taskDependencies.toString();
    }

}
