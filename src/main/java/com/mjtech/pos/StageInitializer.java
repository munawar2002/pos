package com.mjtech.pos;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StageInitializer implements ApplicationListener<JavaFxApplication.StageReadyEvent> {
    @Value("classpath:/fxml/login.fxml")
    private Resource posResource;
    private String applicationTitle;
    private ConfigurableApplicationContext springContext;

    public StageInitializer(@Value("${spring.application.ui.title}") String applicationTitle, ConfigurableApplicationContext context) {
        this.applicationTitle = applicationTitle;
        this.springContext = context;
    }

    @Override
    public void onApplicationEvent(JavaFxApplication.StageReadyEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(posResource.getURL());
            loader.setControllerFactory(springContext::getBean);
            Parent parent = loader.load();
            Stage stage = event.getStage();
            stage.setScene(new Scene(parent));
            stage.setTitle(applicationTitle);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
