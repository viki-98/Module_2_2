package org.post_hub.view;

import org.post_hub.controller.LabelController;
import org.post_hub.controller.PostController;
import org.post_hub.controller.WriterController;
import org.post_hub.model.Writer;

import java.util.List;
import java.util.Scanner;

public class ConsoleView {
    private final WriterController writerController;
    private final PostController postController;
    private final LabelController labelController;
    private final Scanner scanner;

    public ConsoleView(WriterController writerController,
                       PostController postController,
                       LabelController labelController) {
        this.writerController = writerController;
        this.postController = postController;
        this.labelController = labelController;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("1. Writer Operations");
            System.out.println("2. Post Operations");
            System.out.println("3. Label Operations");
            System.out.println("0. Exit");
            System.out.print("Select an option: ");

            String input = scanner.nextLine();
            switch (input) {
                case "1" -> handleWriterMenu();
                case "2" -> System.out.println("Post menu not implemented yet."); // Д/З
                case "3" -> System.out.println("Label menu not implemented yet."); // Д/З
                case "0" -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void handleWriterMenu() {
        while (true) {
            System.out.println("\n--- WRITER MENU ---");
            System.out.println("1. Create Writer");
            System.out.println("2. Show All Writers");
            System.out.println("3. Get Writer by ID");
            System.out.println("4. Edit Writer");
            System.out.println("5. Delete Writer");
            System.out.println("0. Back to Main Menu");
            System.out.print("Select: ");

            String input = scanner.nextLine();
            switch (input) {
                case "1" -> createWriter();
                case "2" -> showAllWriters();
                case "3" -> getWriterById();
                case "4" -> editWriter();
                case "5" -> deleteWriter();
                case "0" -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void createWriter() {
        System.out.print("Enter First Name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter Last Name: ");
        String lastName = scanner.nextLine();
        Writer created = writerController.createWriter(firstName, lastName);
        System.out.println("Created: " + created);
    }

    private void showAllWriters() {
        List<Writer> writers = writerController.getAll();
        if (writers.isEmpty()) {
            System.out.println("No writers found.");
        } else {
            writers.forEach(System.out::println);
        }
    }

    private void getWriterById() {
        System.out.print("Enter ID: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());
            Writer writer = writerController.getById(id);
            if (writer != null) {
                System.out.println(writer);
            } else {
                System.out.println("Writer not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        }
    }

    private void editWriter() {
        System.out.print("Enter ID to edit: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());
            System.out.print("Enter new First Name: ");
            String firstName = scanner.nextLine();
            System.out.print("Enter new Last Name: ");
            String lastName = scanner.nextLine();

            Writer updated = writerController.updateWriter(id, firstName, lastName);
            if (updated != null) {
                System.out.println("Updated: " + updated);
            } else {
                System.out.println("Update failed. Writer not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }
    }

    private void deleteWriter() {
        System.out.print("Enter ID to delete: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());
            writerController.deleteWriter(id);
            System.out.println("Writer deleted (if existed).");
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }
    }
}