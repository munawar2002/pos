package com.mjtech.pos;

import javafx.application.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;

@SpringBootApplication(scanBasePackages = { "com.mjtech.pos"})
public class PosApplication {

	public static void main(String[] args) {
		Application.launch(JavaFxApplication.class, args);
	}
}
