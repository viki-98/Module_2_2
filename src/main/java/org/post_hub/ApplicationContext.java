package org.post_hub;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.post_hub.config.DatabaseUtil;
import org.post_hub.controller.LabelController;
import org.post_hub.controller.PostController;
import org.post_hub.controller.WriterController;
import org.post_hub.service.LabelService;
import org.post_hub.service.PostService;
import org.post_hub.service.WriterService;
import org.post_hub.view.ConsoleView;

import java.sql.Connection;

public final class ApplicationContext {

    public void run() {
        runLiquibase();

        WriterService writerService = new WriterService();
        PostService postService = new PostService();
        LabelService labelService = new LabelService();

        WriterController writerController = new WriterController(writerService);
        PostController postController = new PostController(postService);
        LabelController labelController = new LabelController(labelService);

        ConsoleView view = new ConsoleView(writerController, postController, labelController);

        System.out.println("Application started!");
        view.start();
    }

    private void runLiquibase() {
        try (Connection connection = DatabaseUtil.getConnectionForLB()) {
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

