package com.gabuanii.repository;

import com.gabuanii.entity.ProjectPlan;
import com.gabuanii.entity.Task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectPlanAndTaskRepositoryImpl implements ProjectPlanAndTaskRepository {
    @Override
    public List<ProjectPlan> findAll() {
        return projectPlans;
    }

    @Override
    public ProjectPlan findProjectPlanById(int id) {
        return projectPlans.stream()
                .filter(projectPlan -> projectPlan.getId().equals(id))
                .findFirst()
                .get();
    }

    @Override
    public ProjectPlan saveProjectPlan(ProjectPlan projectPlan) {
        projectPlan.setId(getProjectPlanId());
        projectPlans.add(projectPlan);
        return projectPlan;
    }

    @Override
    public void deleteById(int id) {
        ProjectPlan projectPlan = findProjectPlanById(id);
        projectPlans.remove(projectPlan);
    }

    @Override
    public boolean existsByProjectName(String projectName) {
        if (projectPlans == null || projectPlans.isEmpty()) return false;
        return projectPlans.stream()
                .allMatch(projectPlan -> projectPlan.getProjectName().equalsIgnoreCase(projectName));
    }

    private int getProjectPlanId() {
        return (projectPlans.stream()
                .max(Comparator.comparing(ProjectPlan::getId))
                .map(projectPlan -> Integer.valueOf(projectPlan.getId()))
                .orElse(0)) + 1;
    }

    @Override
    public Task saveTask(int projectPlanId, Task task) {
        LocalDate today = LocalDate.now();
        ProjectPlan projectPlan = findProjectPlanById(projectPlanId);

        task.setId(getNextTaskId());
        task.setDateStart(today.format(DateTimeFormatter.ISO_LOCAL_DATE));
        task.setDateEnd(today.plusDays(task.getDuration()).format(DateTimeFormatter.ISO_LOCAL_DATE));

        projectPlan.getTaskList().add(task);
        return task;
    }

    private int getNextTaskId() {
        return (projectPlans.stream()
                .map(projectPlan -> {
                            if (projectPlan.getTaskList() == null || projectPlan.getTaskList().isEmpty())
                                return 0;
                            return projectPlan.getTaskList()
                                    .stream().max(Comparator.comparing(Task::getId))
                                    .map(task -> Integer.valueOf(task.getId()))
                                    .orElse(0);
                        }
                ).collect(Collectors.toList())
                .stream()
                .max((o1, o2) -> o1.compareTo(o2) > 0 ? o1 : o2)
                .orElse(0)) + 1;
    }

    @Override
    public boolean existsByTaskName(int projectPlanId, String taskName) {
        ProjectPlan projectPlan = findProjectPlanById(projectPlanId);
        List<Task> tasks = projectPlan.getTaskList();
        if (tasks == null || tasks.isEmpty()) return false;

        return tasks.stream()
                .allMatch(task -> task.getTaskName().equalsIgnoreCase(taskName));
    }

    @Override
    public Task findTaskById(int projectPlanId, int taskId) {
        ProjectPlan projectPlan = findProjectPlanById(projectPlanId);
        return projectPlan.getTaskList().stream()
                .filter(task -> task.getId().equals(taskId))
                .findFirst()
                .get();
    }

    @Override
    public void refreshTaskDuration(int projectPlanId) {
        // list task
        ProjectPlan projectPlan = findProjectPlanById(projectPlanId);
        List<Task> tasks = projectPlan.getTaskList();

        LocalDate today = LocalDate.now();
        for (Task task : tasks) {
            int maxDuration = 0;
            // check task has dependency
            boolean hasDependencies = !task.getTaskDependencies().isEmpty();
            if (hasDependencies) {
                // if it has dependency find the highest duration of task
                maxDuration = getMaxDurationOfTask(projectPlanId, task);

            } else {
                maxDuration = task.getDuration();
            }

            // update the start and end date base highest duration of task
            Integer startOffset = task.getDuration() == maxDuration ? 0 : maxDuration - task.getDuration();
            task.setDateStart(today.plusDays(startOffset).format(DateTimeFormatter.ISO_LOCAL_DATE));
            task.setDateEnd(today.plusDays(maxDuration).format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
    }

    private Integer getMaxDurationOfTask(int projectPlanId, Task task) {
        List<Integer> durations = new ArrayList<>();
        for (Integer taskId : task.getTaskDependencies()) {
            Task taskDependency = findTaskById(projectPlanId, taskId);
            durations.add(getMaxDurationOfTask(projectPlanId, taskDependency));
        }

        return durations.stream()
                .mapToInt(Integer::intValue)
                .max().orElse(0) + task.getDuration();
    }

    @Override
    public boolean hasCircularDependencies(int projectPlanId, Task task, Task taskDependency) {
        List<Integer> taskDependencyIds = new ArrayList<>();
        findDependenciesIds(projectPlanId, taskDependency, taskDependencyIds);
        if (taskDependencyIds.contains(task.getId())) {
            taskDependencyIds.add(taskDependency.getId());
            System.out.println("Path: " +
                    taskDependencyIds.stream()
                            .map(x -> String.valueOf(x))
                            .collect(Collectors.joining(" -> ")) + " ...");
            return true;
        } else return false;
    }

    private void findDependenciesIds(int projectPlanId, Task task, List<Integer> result) {
        for (Integer taskId : task.getTaskDependencies()) {
            Task taskDependency = findTaskById(projectPlanId, taskId);
            result.add(taskId);
            findDependenciesIds(projectPlanId, taskDependency, result);
        }

    }

    @Override
    public boolean taskHasDependency(int projectPlanId, int taskId) {
        ProjectPlan projectPlan = findProjectPlanById(projectPlanId);
        return projectPlan.getTaskList().stream()
                .anyMatch(task -> task.getTaskDependencies().contains(taskId));
    }
}
