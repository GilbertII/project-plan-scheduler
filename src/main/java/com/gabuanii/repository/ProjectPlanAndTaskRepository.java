package com.gabuanii.repository;

import com.gabuanii.entity.ProjectPlan;
import com.gabuanii.entity.Task;

import java.util.ArrayList;
import java.util.List;

public interface ProjectPlanAndTaskRepository {

    List<ProjectPlan> projectPlans = new ArrayList<>();

    List<ProjectPlan> findAll();

    ProjectPlan findProjectPlanById(int id);

    ProjectPlan saveProjectPlan(ProjectPlan projectPlan);

    void deleteById(int id);

    boolean existsByProjectName(String projectName);

    Task saveTask(int projectPlanId, Task task);

    boolean existsByTaskName(int projectPlanId, String taskName);

    Task findTaskById(int projectPlanId, int taskId);

    void refreshTaskDuration(int projectPlanId);

    boolean hasCircularDependencies(int projectPlanId, Task task, Task taskDependency);

    boolean taskHasDependency(int projectPlanId, int taskId);


}
