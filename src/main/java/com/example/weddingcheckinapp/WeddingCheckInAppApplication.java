package com.example.weddingcheckinapp;

import com.example.weddingcheckinapp.service.InvitationImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WeddingCheckInAppApplication {

    @Autowired
    private InvitationImportService invitationImportService;

    public static void main(String[] args) {
        SpringApplication.run(WeddingCheckInAppApplication.class, args);
    }

    @Bean
    public CommandLineRunner run() {
        return args -> {
            try {
                invitationImportService.importInvitations("src/main/resources/Cleaned_Invitation_List.xlsx");
                System.out.println("Einladungen erfolgreich importiert.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}
