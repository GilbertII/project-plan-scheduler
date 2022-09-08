package com.gabuanii.controller;

import com.gabuanii.entity.ProjectPlan;
import com.gabuanii.entity.Task;
import com.gabuanii.service.ProjectPlanAndTaskService;
import com.gabuanii.service.ProjectPlanAndTaskServiceImpl;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.gabuanii.util.ProjectPlanUtil.ChainComparator;
import static com.gabuanii.util.ProjectPlanUtil.generateMenuItem;
import static com.gabuanii.util.ProjectPlanUtil.getIntegerInput;
import static com.gabuanii.util.ProjectPlanUtil.getStringInput;
import static com.gabuanii.util.ProjectPlanUtil.showConfirmationMessage;
import static com.gabuanii.util.ProjectPlanUtil.showMessage;

public class ProjectPlanController {

    private static final String NO_EXISTING_PROJECT_PLAN = "No existing project plan!";

    private ProjectPlanAndTaskService projectPlanAndTaskService = new ProjectPlanAndTaskServiceImpl();

    private TaskController taskController = new TaskController();

    public void execute() {
        boolean hasTerminate;
        do {
            hasTerminate = showMainMenu();
        } while (hasTerminate == false);
    }

    private Boolean showMainMenu() {
        System.out.println();
        System.out.println("Choose Project Plan Operation:");
        System.out.println("1. Add Project Plan");
        System.out.println("2. Edit Project Plan");
        System.out.println("3. Select Project Plan");
        System.out.println("4. Remove Project Plan");
        System.out.println("5. Generate Project Plan Schedule");
        System.out.println("6. Exit");
        System.out.println();

        Integer ans = getIntegerInput(6);
        switch (ans) {
            case 1:
                addProjectPlan();
                break;
            case 2:
                showListOfEditProjectPlanMenu();
                break;
            case 3:
                selectProjectPlan();
                break;
            case 4:
                removeProjectPlan();
                break;
            case 5:
                generateSchedule();
                break;
            case 6:
                return true;
        }
        return false;
    }

    private void addProjectPlan() {
        System.out.println();
        System.out.print("Enter Project Plan Name: ");
        String projectPlanName = getStringInput();

        if (!projectPlanAndTaskService.isProjectNameExist(projectPlanName)) {

            ProjectPlan projectPlan = new ProjectPlan();
            projectPlan.setProjectName(projectPlanName);
            projectPlanAndTaskService.saveProjectPlan(projectPlan);

            System.out.println();
            System.out.println("Project plan name '" + projectPlanName + "' has been saved!");
            System.out.println();
        } else {
            System.out.println();
            System.out.println("Project plan name '" + projectPlanName + "' is already taken!");
            System.out.println();
        }
    }

    private void showListOfEditProjectPlanMenu() {
        List<ProjectPlan> projectPlans = projectPlanAndTaskService.getAllProjectPlans();

        if (!projectPlans.isEmpty()) {
            System.out.println();
            System.out.println("Select Project Plan (to edit): ");

            Map<Integer, ProjectPlan> map = generateProjectPlanMenu();

            Integer selectedId = getIntegerInput(projectPlans.size());
            ProjectPlan projectPlan = map.get(selectedId);

            // show
            showEditProjectPlanMenu(projectPlan);

        } else {
            showMessage(NO_EXISTING_PROJECT_PLAN);
        }
        System.out.println();
    }

    private void showEditProjectPlanMenu(ProjectPlan projectPlan) {
        int input;

        do {
            System.out.println();
            System.out.println("Project Plan: " + projectPlan.getProjectName());
            System.out.println("Select value to edit: ");
            System.out.println("1. Change Project Name");
            System.out.println("2. Back");
            System.out.println();
            input = getIntegerInput(2);

            switch (input) {
                case 1:
                    System.out.println();
                    editProjectPlanName(projectPlan);
                    break;
                case 2:
                    break;
            }
        } while (input != 2);
    }

    private void editProjectPlanName(ProjectPlan projectPlan) {
        if (projectPlan.getProjectName() != null) {
            System.out.println("Old Project Plan Name: " + projectPlan.getProjectName());
        }
        System.out.print("Input Project Plan Name: ");
        String newProjectPlanName = getStringInput();
        if (!projectPlanAndTaskService.isProjectNameExist(newProjectPlanName)) {
            projectPlan.setProjectName(newProjectPlanName);
            System.out.println("Project plan name '" + newProjectPlanName + "' is successfully changed!");
        } else
            System.out.println("Project plan name '" + newProjectPlanName + "' is already taken!");
    }

    private void selectProjectPlan() {
        List<ProjectPlan> projectPlans = projectPlanAndTaskService.getAllProjectPlans();
        if (!projectPlans.isEmpty()) {

            int input;
            int maxSize;

            do {
                System.out.println();
                System.out.println("Select Project Plan: ");

                Map<Integer, ProjectPlan> map = generateProjectPlanMenu();

                System.out.println();
                maxSize = map.size() + 1;
                input = getIntegerInput(maxSize);

                if (input != maxSize) {
                    // show task
                    ProjectPlan projectPlan = map.get(input);
                    taskController.test();
                    taskController.showTaskMainMenu(projectPlan);
                }

            } while (input != maxSize);
        } else {
            showMessage(NO_EXISTING_PROJECT_PLAN);
        }
        System.out.println();
    }

    private void removeProjectPlan() {
        List<ProjectPlan> projectPlans = projectPlanAndTaskService.getAllProjectPlans();
        if (!projectPlans.isEmpty()) {

            int input;
            int maxSize;

            do {
                System.out.println();
                System.out.println("Select Project Plan (to remove): ");

                Map<Integer, ProjectPlan> map = generateProjectPlanMenu();

                System.out.println();
                maxSize = map.size() + 1;
                input = getIntegerInput(maxSize);

                if (input != maxSize && showConfirmationMessage()) {
                    ProjectPlan projectPlan = map.get(input);
                    projectPlanAndTaskService.remove(projectPlan.getId());
                    System.out.println();
                    System.out.println("Project Plan '" + projectPlan.getProjectName() + "' successfully removed!");
                }

            } while (input != maxSize);
        } else {
            showMessage(NO_EXISTING_PROJECT_PLAN);
        }
        System.out.println();
    }

    private void generateSchedule() {
        List<ProjectPlan> projectPlans = projectPlanAndTaskService.getAllProjectPlans();
        if (!projectPlans.isEmpty()) {

            int input;
            int maxSize;

            do {
                System.out.println();
                System.out.println("Select Project Plan to generate schedule: ");

                Map<Integer, ProjectPlan> map = generateProjectPlanMenu();

                System.out.println();
                maxSize = map.size() + 1;
                input = getIntegerInput(maxSize);

                if (input != maxSize) {
                    // show task
                    ProjectPlan projectPlan = projectPlanAndTaskService.getProjectPlan(map.get(input).getId());

                    Comparator<Task> sortByDateStart = Comparator.comparing(Task::getDateStart);
                    Comparator<Task> sortByDateEnd = Comparator.comparing(Task::getDateEnd);
                    List<Comparator<Task>> comparators = Stream.of(sortByDateStart, sortByDateEnd)
                            .collect(Collectors.toList());

                    System.out.println();
                    System.out.println("PROJECT PLAN: " + projectPlan.getProjectName());
                    if (projectPlan.getTaskList().isEmpty())
                        System.out.println("No Project Task List!");
                    else
                        projectPlan.getTaskList().stream()
                                .sorted(new ChainComparator(comparators))
                                .forEach(task -> System.out.println(task.toString()));
                }

            } while (input != maxSize);
        } else {
            showMessage(NO_EXISTING_PROJECT_PLAN);
        }
        System.out.println();
    }

    private Map<Integer, ProjectPlan> generateProjectPlanMenu() {
        List<ProjectPlan> projectPlans = projectPlanAndTaskService.getAllProjectPlans();
        int maxSize = projectPlans.size() + 1;
        int cnt = 1;

        Map<Integer, ProjectPlan> map = new HashMap<>();
        for (ProjectPlan projectPlan : projectPlans) {
            map.put(cnt, projectPlan);

            cnt++;
        }

        for (Map.Entry<Integer, ProjectPlan> entry : map.entrySet()) {
            ProjectPlan projectPlan = entry.getValue();
            System.out.println(generateMenuItem(entry.getKey(),
                    "ID[" + projectPlan.getId() + "] " + projectPlan.getProjectName()));
        }
        System.out.println(generateMenuItem(maxSize, "Back"));

        return map;
    }

}
