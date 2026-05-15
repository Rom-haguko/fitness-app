package com.fitness.fitnessapp;

import org.springframework.boot.SpringApplication;

public class TestFitnessAppApplication {

    public static void main(String[] args) {
        SpringApplication.from(FitnessAppApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
