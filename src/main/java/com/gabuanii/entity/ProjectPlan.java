package com.gabuanii.entity;

import java.util.ArrayList;
import java.util.List;

public class ProjectPlan {

    private Integer id;
    private String projectName;

    private List<Task> taskList;

    public ProjectPlan() {
        this.taskList = new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    @Override
    public String toString() {
        return "Project Plan: " +
                "\nid=" + id +
                "\nprojectName=" + projectName +
                "\n" + taskList.toString();
    }
}
