package com.payme.users.service;

import com.payme.users.model.User;
import com.payme.users.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: "+email));

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getUsername())
                .password(u.getHashedPassword())
                .authorities(u.getRole().name())
                .disabled(!u.isEnabled())
                .accountLocked(u.isAccountLocked())
                .build();

    }
}
