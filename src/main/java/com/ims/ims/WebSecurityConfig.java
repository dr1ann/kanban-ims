package com.ims.ims;

import com.ims.ims.Services.CustomUserDetailsService; // Import your CustomUser DetailsService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/", "/login", "/css/output.tailwind.css", "/js/toggle-password.js", "/images/logo.png",
                                "/images/smallogo.png", "/static/favicon.ico").permitAll()
                        .anyRequest().authenticated())
                .csrf(csrf -> csrf.disable())

                .formLogin((form) -> form
                                .loginPage("/login")
                                .successHandler((request, response, authentication) -> {
                                    response.sendRedirect("/dashboard");
                                })
                                .permitAll()
                )
                .logout((logout) -> logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/login?logout")
                                .permitAll()
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return customUserDetailsService;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService);
    }
}