package com.amalitech.kanbantaskmanagement;

import org.springframework.boot.SpringApplication;
import org.testcontainers.utility.TestcontainersConfiguration;

public class TestKanbanTaskManagementApplication {

    public static void main(String[] args) {
        SpringApplication.from(KanbanTaskManagement::main).with(TestcontainersConfiguration.class).run(args);
    }

}

