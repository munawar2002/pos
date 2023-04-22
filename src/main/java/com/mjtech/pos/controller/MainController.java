package com.mjtech.pos.controller;

import javafx.application.Platform;

public class MainController {


    public void newOrderForm() {
        System.out.println("----_ Selected new order form");
    }

    public void productForm() {
        System.out.println("----_ Selected product form");
    }

    public void closeApplication() {
        Platform.exit();
    }

}
