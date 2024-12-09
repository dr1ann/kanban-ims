
package com.ims.ims;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.stereotype.Component;
// import com.ims.ims.Entities.User;
// import com.ims.ims.Repositories.UserRepository;

@SpringBootApplication
public class ImsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImsApplication.class, args);
    }
}

// @Component // This annotation allows Spring to detect and register this class as a bean
// class DatabaseSeeder implements CommandLineRunner {

//     @Autowired
//     private UserRepository userRepository;

//     // Injecting BCryptPasswordEncoder
//     @Autowired
//     private BCryptPasswordEncoder passwordEncoder;

//     @Override
//     public void run(String... args) throws Exception {
//         createUsers();
//     }

//     private void createUsers() {
//         User user = new User();
//         user.setFirstName("James");
//         user.setLastName("Denoy");
//         user.setUsername("admin");
//         user.setEmail("admin@gmail.com");
//         user.setPassword(passwordEncoder.encode("admin123")); 

//         // Save the user to the database
//         userRepository.save(user);
//     }
// }
