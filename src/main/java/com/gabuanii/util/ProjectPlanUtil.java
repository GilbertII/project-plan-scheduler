package com.gabuanii.util;

import com.gabuanii.entity.Task;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public final class ProjectPlanUtil {
    private static Scanner scan = new Scanner(System.in);

    public static void showMessage(String message) {
        System.out.println(message);
        promptAnyKey();
        System.out.println();
    }

    public static String generateMenuItem(Integer selector, String text) {
        return String.format("%d. %s", selector, text);
    }

    public static void promptAnyKey() {
        System.out.println();
        System.out.print("Press any key to continue...");
        scan.nextLine();
    }

    public static boolean showConfirmationMessage() {
        System.out.println();
        System.out.println("Are you sure?");
        System.out.println("1. Yes");
        System.out.println("2. Back");
        System.out.println();

        int input = getIntegerInput(2);

        return input == 1;
    }

    public static String getStringInput() {
        String textToReturn;
        boolean isEmpty;
        do {
            textToReturn = scan.nextLine();
            isEmpty = textToReturn.isEmpty();

            if (isEmpty) {
                System.out.println(" No value entered");
            }

        } while (isEmpty);

        return textToReturn;
    }

    public static int getIntegerInput(int maximum) {
        boolean valid;
        int input = 0;

        do {
            try {
                System.out.print("Select from 1-" + maximum+": ");
                input = Integer.valueOf(scan.nextLine());
                if (input <= 0 || input > maximum) {
                    valid = false;
                    System.out.println("Only accepts values 1-" + maximum);
                } else valid = true;
            } catch (NumberFormatException e) {
                System.out.println("Invalid Input");
                valid = false;
            }


        } while (!valid);

        return input;
    }

    public static class ChainComparator implements Comparator<Task> {
        private List<Comparator<Task>> comparators;

        public ChainComparator(List<Comparator<Task>> comparators) {
            this.comparators = comparators;
        }

        @Override
        public int compare(Task o1, Task o2) {
            int result;
            for (Comparator<Task> comparator : comparators) {
                if ((result = comparator.compare(o1, o2)) != 0)
                    return result;
            }
            return 0;
        }
    }

}
