package demo;

import java.util.Scanner;

public class test {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to continue?(y or n)");
        String input = scanner.nextLine();
        while (input.equals("y")) {
            System.out.println("hello");
            System.out.println("Do you want to continue?");
            input = scanner.nextLine();
        }
        scanner.close();
    }
}