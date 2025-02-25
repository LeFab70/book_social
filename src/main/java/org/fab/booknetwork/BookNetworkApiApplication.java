package org.fab.booknetwork;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.fab.booknetwork.role.Role;
import org.fab.booknetwork.role.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@OpenAPIDefinition(info = @Info(title = "Book Network API", version = "1.0", description = "API de gestion des livres"))

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync

public class BookNetworkApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookNetworkApiApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(RoleRepository roleRepository){
        return args -> {
            if(roleRepository.findByName("USER").isEmpty())
                roleRepository.save(
                        Role.builder().name("USER").build()

                );
        };
    }
}
