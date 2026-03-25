package com.esun.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // 這是關鍵：它會自動掃描同層級及子層級的 Controller/Service/Entity
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

}