package com.gabuanii.controller;

import com.gabuanii.entity.ProjectPlan;
import com.gabuanii.entity.Task;
import com.gabuanii.service.ProjectPlanAndTaskService;
import com.gabuanii.service.ProjectPlanAndTaskServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.gabuanii.util.ProjectPlanUtil.*;

public class TaskController {

    private static final String NO_EXISTING_TASK = "No existing project plan task!";

    private ProjectPlanAndTaskService projectPlanAndTaskService = new ProjectPlanAndTaskServiceImpl();

    public void test() {
        System.out.println(projectPlanAndTaskService.getAllProjectPlans().size());
    }

    public void showTaskMainMenu(ProjectPlan projectPlan) {
        int input;

        do {
            System.out.println();
            System.out.println("PROJECT PLAN: " + projectPlan.getProjectName());
            System.out.println("Choose Task Operation: ");
            System.out.println("1. Add Task");
            System.out.println("2. Edit Task");
            System.out.println("3. Show Task");
            System.out.println("4. Remove Task");
            System.out.println("5. Back");
            System.out.println();
            input = getIntegerInput(5);

            switch (input) {
                case 1:
                    System.out.println();
                    addTask(projectPlan);
                    break;
                case 2:
                    System.out.println();
                    showListOfEditTaskMenu(projectPlan);
                    break;
                case 3:
                    System.out.println();
                    showDisplayTaskMenu(projectPlan);
                    break;
                case 4:
                    System.out.println();
                    showRemoveTaskMenu(projectPlan);
                    break;
                case 5:
                    break;

            }
        } while (input != 5);
    }

    private void addTask(ProjectPlan projectPlan) {
        int projectPlanId = projectPlan.getId();

        System.out.print("Enter task name:");
        String taskName = getStringInput();
        System.out.println();
        System.out.println("Enter duration:");
        int duration = getIntegerInput(999);

        if (!projectPlanAndTaskService.isTaskNameExist(projectPlanId, taskName)) {

            Task task = new Task();
            task.setTaskName(taskName);
            task.setDuration(duration);

            projectPlanAndTaskService.saveTask(projectPlanId, task);

            System.out.println();
            System.out.println("Task name '" + taskName + "' has been saved!");
            System.out.println();
        } else {
            System.out.println();
            System.out.println("Task name '" + taskName + "' is already taken!");
            System.out.println();
        }

    }

    private void showListOfEditTaskMenu(ProjectPlan projectPlan) {
        List<Task> tasks = projectPlan.getTaskList();
        if (!tasks.isEmpty()) {
            int input;
            int maxInput;

            do {
                System.out.println();
                System.out.println("Select task to edit:");

                Map<Integer, Task> map = generateTaskMenu(projectPlan);
                maxInput = map.size() + 1;
                System.out.println(generateMenuItem(maxInput, "Back"));
                System.out.println();
                input = getIntegerInput(maxInput);

                if (input != maxInput) {
                    Task task = map.get(input);
                    showEditTaskMenu(projectPlan, task);
                }

            } while (input != maxInput);
        } else {
            showMessage(NO_EXISTING_TASK);
        }
    }

    private void showEditTaskMenu(ProjectPlan projectPlan, Task task) {
        Integer input;

        do {
            System.out.println();
            System.out.println("TASK: " + task.getTaskName());
            System.out.println("Select operation to edit: ");
            System.out.println("1. Change Task Name");
            System.out.println("2. Change Duration");
            System.out.println("3. Add Task Dependencies");
            System.out.println("4. Back");
            System.out.println();
            input = getIntegerInput(4);

            switch (input) {
                case 1:
                    System.out.println();
                    editTaskName(projectPlan.getId(), task);
                    break;
                case 2:
                    System.out.println();
                    editTaskDuration(task);
                    break;
                case 3:
                    System.out.println();
                    addTaskDependency(projectPlan, task);
                    break;
                case 4:
                    break;

            }
        } while (input != 4);
    }

    private void editTaskName(Integer projectPlanId, Task task) {
        System.out.println("Old task name: " + task.getTaskName());

        System.out.println("Enter new task name: ");
        String taskName = getStringInput();

        if (!projectPlanAndTaskService.isTaskNameExist(projectPlanId, taskName)) {
            task.setTaskName(taskName);
            System.out.println("Task name '" + taskName + "' is successfully changed!");
        } else
            System.out.println("Task name '" + taskName + "' is already taken!");
    }

    private void editTaskDuration(Task task) {
        System.out.println("Old duration: " + task.getTaskName());

        System.out.println("Enter new duration: ");
        int newDuration = getIntegerInput(999);

        task.setDuration(newDuration);
        System.out.println("Task duration '" + newDuration + "' is successfully changed!");
    }

    private void addTaskDependency(ProjectPlan projectPlan, Task task) {
        // 1. show the current dependencies if it has
        System.out.println();
        System.out.println("TASK: " + task.getTaskName());
        boolean hasDependency = !task.getTaskDependencies().isEmpty();
        if (hasDependency) {
            System.out.println("Current task dependencies: " + task.getTaskDependencies().toString());
        }

        // 2. make a list of task to be assigned as dependency and don't include the current task
        // 3. generate menu of dependency
        System.out.println();
        System.out.println("Add Task Dependencies");

        int input;
        int maxInput;
        do {
            System.out.println();
            Map<Integer, Task> taskMap = generateTaskDependencyMenu(projectPlan, task);
            maxInput = (hasDependency ? taskMap.size() + 2 : taskMap.size() + 1);
            if (hasDependency) {
                System.out.println(generateMenuItem(maxInput - 1, "Clear Dependencies"));
            }
            System.out.println(generateMenuItem(maxInput, "Back"));

            System.out.println();

            input = getIntegerInput(maxInput);
            Task taskDependency = taskMap.get(input);

            // 5. select task  for dependency
            // 5.1 clear dependency
            // 6. check if circular dependency occur
            // 7. successfully added

            if (input != maxInput) {
                if (hasDependency && input == (maxInput - 1)) {
                    task.getTaskDependencies().clear();
                    projectPlanAndTaskService.generateTaskDuration(projectPlan.getId());
                    System.out.println("Successfully cleared dependencies!");
                    hasDependency = false;
                } else if (projectPlanAndTaskService
                        .checkHasCircularDependencies(projectPlan.getTaskList(), task.getId(), taskDependency.getId())) {
                    System.out.println("Cannot add Task Dependency " + taskDependency.getTaskName()
                            + " to Task " + task.getTaskName()
                            + " Circular Dependency may occur!");
                } else {
                    task.getTaskDependencies().add(taskDependency.getId());
                    projectPlanAndTaskService.generateTaskDuration(projectPlan.getId());
                    System.out.println("Successfully added as dependency!");
                }
            }

        } while (input != maxInput);
        System.out.println();
    }

    private void showDisplayTaskMenu(ProjectPlan projectPlan) {
        List<Task> tasks = projectPlan.getTaskList();
        if (!tasks.isEmpty()) {
            System.out.println();
            System.out.println("PROJECT PLAN: " + projectPlan.getProjectName());
            System.out.println("List of Task:");
            generateTaskMenu(projectPlan);
            System.out.println();
            promptAnyKey();
        } else {
            showMessage(NO_EXISTING_TASK);
        }
    }

    private void showRemoveTaskMenu(ProjectPlan projectPlan) {
        List<Task> tasks = projectPlan.getTaskList();
        if (!tasks.isEmpty()) {
            int input;
            int maxInput;

            do {
                System.out.println();
                System.out.println("Select task to delete:");

                Map<Integer, Task> map = generateTaskMenu(projectPlan);
                maxInput = map.size() + 1;

                System.out.println(generateMenuItem(maxInput, "Back"));
                System.out.println();
                input = getIntegerInput(maxInput);

                if (input != maxInput && showConfirmationMessage()) {
                    Task task = map.get(input);

                    if (projectPlanAndTaskService.checkTaskHasDependency(projectPlan.getId(), task.getId())) {
                        Task taskDependency = projectPlan.getTaskList().stream()
                                .filter(t -> t.getTaskDependencies().contains(task.getId()))
                                .findFirst().get();

                        System.out.println("Cannot remove Task " + task.getTaskName()
                                + " has dependency to Task " + taskDependency.getTaskName());
                    } else {
                        projectPlan.getTaskList().remove(task);
                        projectPlanAndTaskService.generateTaskDuration(projectPlan.getId());
                        System.out.println();
                        System.out.println("Task '" + task.getTaskName() + "' successfully removed!");
                    }
                }

            } while (input != maxInput);
        } else {
            showMessage(NO_EXISTING_TASK);
        }
    }


    private Map<Integer, Task> generateTaskDependencyMenu(ProjectPlan projectPlan, Task task) {
        List<Task> tasks = projectPlan.getTaskList().stream()
                .filter(t -> !t.getId().equals(task.getId())
                        && task.getTaskDependencies().stream().noneMatch(dep -> dep == t.getId())
                        && !t.getTaskDependencies().stream().anyMatch(dep -> dep == task.getId())
                )
                .collect(Collectors.toList());

        int cnt = 1;
        Map<Integer, Task> taskMap = new HashMap<>();
        for (Task t : tasks) {
            taskMap.put(cnt, t);
            cnt++;
        }

        for (Map.Entry<Integer, Task> entry : taskMap.entrySet()) {
            Task t = entry.getValue();
            System.out.println(generateMenuItem(entry.getKey(),
                    "ID[" + t.getId() + "] " + t.getTaskName()));
        }
        return taskMap;
    }

    private Map<Integer, Task> generateTaskMenu(ProjectPlan projectPlan) {
        List<Task> tasks = projectPlan.getTaskList();
        int cnt = 1;

        Map<Integer, Task> map = new HashMap<>();
        for (Task task : tasks) {
            map.put(cnt, task);
            cnt++;
        }

        for (Map.Entry<Integer, Task> entry : map.entrySet()) {
            Task task = entry.getValue();
            System.out.println(generateMenuItem(entry.getKey(),
                    "ID[" + task.getId() + "] " + task.getTaskName()));
        }

        return map;
    }
}
