package com.example.dailyrutine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DailyRoutineApplication {

    public static void main(String[] args) {
        SpringApplication.run(DailyRoutineApplication.class, args);
    }

}
