package i202;

import java.util.Scanner;

public class LibraryManagement {
    public static void main(String[] args) {
        Library lib = new Library();
        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n==== LIBRARY MENU ====");
            System.out.println("1. Add Book");
            System.out.println("2. Add Member");
            System.out.println("3. Borrow Book");
            System.out.println("4. Return Book");
            System.out.println("5. Display Available Books");
            System.out.println("6. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1 -> lib.addBook();
                case 2 -> lib.addMember();
                case 3 -> lib.borrowBook();
                case 4 -> lib.returnBook();
                case 5 -> lib.showAvailableBooks();
                case 6 -> System.out.println("Exiting... 👋");
                default -> System.out.println("Invalid choice!");
            }
        } while (choice != 6);
    }
}
