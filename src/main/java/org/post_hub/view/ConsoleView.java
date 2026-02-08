package org.post_hub.view;

import org.post_hub.controller.LabelController;
import org.post_hub.controller.PostController;
import org.post_hub.controller.WriterController;
import org.post_hub.model.Label;
import org.post_hub.model.Post;
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
                case "2" -> handlePostMenu();
                case "3" -> handleLabelMenu();
                case "0" -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void handlePostMenu() {
        while (true) {
            System.out.println("\n--- POST MENU ---");
            System.out.println("1. Create Post");
            System.out.println("2. Show All Posts");
            System.out.println("3. Get Post by ID");
            System.out.println("4. Edit Post");
            System.out.println("5. Delete Post (set status=DELETED)");
            System.out.println("0. Back to Main Menu");
            System.out.print("Select: ");

            String input = scanner.nextLine();
            switch (input) {
                case "1" -> createPost();
                case "2" -> showAllPosts();
                case "3" -> getPostById();
                case "4" -> editPost();
                case "5" -> deletePost();
                case "0" -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void handleLabelMenu() {
        while (true) {
            System.out.println("\n--- LABEL MENU ---");
            System.out.println("1. Create Label");
            System.out.println("2. Show All Labels");
            System.out.println("3. Get Label by ID");
            System.out.println("4. Edit Label");
            System.out.println("5. Delete Label");
            System.out.println("0. Back to Main Menu");
            System.out.print("Select: ");

            String input = scanner.nextLine();
            switch (input) {
                case "1" -> createLabel();
                case "2" -> showAllLabels();
                case "3" -> getLabelById();
                case "4" -> editLabel();
                case "5" -> deleteLabel();
                case "0" -> { return; }
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

    private void createPost() {
        System.out.print("Enter content: ");
        String content = scanner.nextLine();

        System.out.print("Enter writer ID (blank for null): ");
        String writerInput = scanner.nextLine().trim();
        Long writerId = null;
        if (!writerInput.isEmpty()) {
            try {
                writerId = Long.parseLong(writerInput);
            } catch (NumberFormatException e) {
                System.out.println("Invalid writer ID format. Using null.");
            }
        }

        Post created = postController.createPost(content, writerId);
        System.out.println("Created: " + created);
    }

    private void showAllPosts() {
        List<Post> posts = postController.getAll();
        if (posts.isEmpty()) {
            System.out.println("No posts found.");
        } else {
            posts.forEach(System.out::println);
        }
    }

    private void getPostById() {
        System.out.print("Enter ID: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());
            Post post = postController.getById(id);
            if (post != null) {
                System.out.println(post);
            } else {
                System.out.println("Post not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        }
    }

    private void editPost() {
        System.out.print("Enter ID to edit: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());
            System.out.print("Enter new content: ");
            String content = scanner.nextLine();

            Post updated = postController.updatePost(id, content);
            if (updated != null) {
                System.out.println("Updated: " + updated);
            } else {
                System.out.println("Update failed. Post not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }
    }

    private void deletePost() {
        System.out.print("Enter ID to delete: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());
            postController.deletePost(id);
            System.out.println("Post deleted (status set to DELETED if existed).");
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }
    }

    private void createLabel() {
        System.out.print("Enter label name: ");
        String name = scanner.nextLine();
        Label created = labelController.createLabel(name);
        System.out.println("Created: " + created);
    }

    private void showAllLabels() {
        List<Label> labels = labelController.getAll();
        if (labels.isEmpty()) {
            System.out.println("No labels found.");
        } else {
            labels.forEach(System.out::println);
        }
    }

    private void getLabelById() {
        System.out.print("Enter ID: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());
            Label label = labelController.getById(id);
            if (label != null) {
                System.out.println(label);
            } else {
                System.out.println("Label not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        }
    }

    private void editLabel() {
        System.out.print("Enter ID to edit: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());
            System.out.print("Enter new name: ");
            String name = scanner.nextLine();

            Label updated = labelController.updateLabel(id, name);
            if (updated != null) {
                System.out.println("Updated: " + updated);
            } else {
                System.out.println("Update failed. Label not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }
    }

    private void deleteLabel() {
        System.out.print("Enter ID to delete: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());
            labelController.deleteLabel(id);
            System.out.println("Label deleted (if existed).");
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
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