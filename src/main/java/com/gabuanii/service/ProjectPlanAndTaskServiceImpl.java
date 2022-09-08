package com.gabuanii.service;

import com.gabuanii.entity.ProjectPlan;
import com.gabuanii.entity.Task;
import com.gabuanii.repository.ProjectPlanAndTaskRepository;
import com.gabuanii.repository.ProjectPlanAndTaskRepositoryImpl;

import java.util.List;

public class ProjectPlanAndTaskServiceImpl implements ProjectPlanAndTaskService{

    private final ProjectPlanAndTaskRepository projectPlanAndTaskRepository = new ProjectPlanAndTaskRepositoryImpl();

    @Override
    public List<ProjectPlan> getAllProjectPlans() {
        return projectPlanAndTaskRepository.findAll();
    }
    @Override
    public ProjectPlan getProjectPlan(int id) {
        return projectPlanAndTaskRepository.findProjectPlanById(id);
    }
    @Override
    public ProjectPlan saveProjectPlan(ProjectPlan projectPlan) {
        return projectPlanAndTaskRepository.saveProjectPlan(projectPlan);
    }
    @Override
    public void remove(int id) {
        projectPlanAndTaskRepository.deleteById(id);
    }
    @Override
    public boolean isProjectNameExist(String projectName) {
        return projectPlanAndTaskRepository.existsByProjectName(projectName);
    }
    @Override
    public boolean isTaskNameExist(int projectPlanId, String taskName){
        return projectPlanAndTaskRepository.existsByTaskName(projectPlanId, taskName);
    }
    @Override
    public Task saveTask(int projectPlanId, Task task){
        return projectPlanAndTaskRepository.saveTask(projectPlanId, task);
    }

    @Override
    public void generateTaskDuration(int projectPlanId){
        projectPlanAndTaskRepository.refreshTaskDuration(projectPlanId);
    }

    @Override
    public boolean checkHasCircularDependencies(List<Task> tasks, int taskId, int taskDependencyId){
        return projectPlanAndTaskRepository.hasCircularDependencies(tasks,taskId, taskDependencyId);
    }

    @Override
    public boolean checkTaskHasDependency(int projectPlanId, int taskId){
        return projectPlanAndTaskRepository.taskHasDependency(projectPlanId, taskId);
    }

}
