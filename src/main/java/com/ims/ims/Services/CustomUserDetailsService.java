package com.ims.ims.Services;

import com.ims.ims.Entities.CustomUserDetails;
import com.ims.ims.Entities.User;
import com.ims.ims.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmail(input);

        if (!optionalUser.isPresent()) {
            optionalUser = userRepository.findByUsername(input);
        }

        User user = optionalUser.orElseThrow(() -> 
            new UsernameNotFoundException("User not found with email or username: " + input)
        );

        // Map User to CustomUserDetails
        Collection<? extends GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> (GrantedAuthority) () -> role) // Convert role strings to GrantedAuthority
                .collect(Collectors.toList());

        return new CustomUserDetails(user.getFirstName(), user.getLastName(), user.getUsername(), user.getPassword(), user.getEmail(), authorities);
    }
}
