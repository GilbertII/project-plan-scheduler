package com.gabuanii.service;

import com.gabuanii.entity.ProjectPlan;
import com.gabuanii.entity.Task;

import java.util.List;

public interface ProjectPlanAndTaskService {

    List<ProjectPlan> getAllProjectPlans();

    ProjectPlan getProjectPlan(int id);

    ProjectPlan saveProjectPlan(ProjectPlan projectPlan);

    void remove(int id);

    boolean isProjectNameExist(String projectName);

    boolean isTaskNameExist(int projectPlanId, String taskName);

    Task saveTask(int projectPlanId, Task task);

    void generateTaskDuration(int projectPlanId);

    boolean checkHasCircularDependencies(int projectPlanId, Task task, Task taskDependency);

    boolean checkTaskHasDependency(int projectPlanId, int taskId);

}
