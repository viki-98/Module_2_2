package org.post_hub;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.post_hub.config.DatabaseConnection;
import org.post_hub.controller.LabelController;
import org.post_hub.controller.PostController;
import org.post_hub.controller.WriterController;
import org.post_hub.repository.impl.JdbcLabelRepositoryImpl;
import org.post_hub.repository.impl.JdbcPostRepositoryImpl;
import org.post_hub.repository.impl.JdbcWriterRepositoryImpl;
import org.post_hub.service.LabelService;
import org.post_hub.service.PostService;
import org.post_hub.service.WriterService;
import org.post_hub.view.ConsoleView;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        runLiquibase();

        JdbcWriterRepositoryImpl writerRepo = new JdbcWriterRepositoryImpl();
        JdbcPostRepositoryImpl postRepo = new JdbcPostRepositoryImpl();
        JdbcLabelRepositoryImpl labelRepo = new JdbcLabelRepositoryImpl();

        WriterService writerService = new WriterService(writerRepo);
        PostService postService = new PostService(postRepo);
        LabelService labelService = new LabelService(labelRepo);

        WriterController writerController = new WriterController(writerService);
        PostController postController = new PostController(postService);
        LabelController labelController = new LabelController(labelService);

        ConsoleView view = new ConsoleView(writerController, postController, labelController);

        System.out.println("Application started!");
        view.start();
    }

    private static void runLiquibase() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase(
                    "db/changelog/db.changelog-master.yaml",
                    new ClassLoaderResourceAccessor(),
                    database);
            liquibase.update("");
            System.out.println("Migration is successful");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}