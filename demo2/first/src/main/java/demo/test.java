package demo;

import java.util.Scanner;

public class test {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to continue?");
        String input = scanner.nextLine();
        while (input.equals("1")) {
            System.out.println("hello");
            System.out.println("Do you want to continue?");
            input = scanner.nextLine();
        }
        scanner.close();
    }
}