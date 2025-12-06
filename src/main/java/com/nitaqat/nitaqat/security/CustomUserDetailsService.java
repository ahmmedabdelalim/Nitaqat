package com.nitaqat.nitaqat.security;

import com.nitaqat.nitaqat.entity.User;
import com.nitaqat.nitaqat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // Only allow admin users
//        if (!"admin".equalsIgnoreCase(user.getRole())) {
//            throw new UsernameNotFoundException("Only admin users can log in");
//        }

        // ⚠️ CRITICAL CHANGE: Return CustomUserDetails instead of generic Spring User
        return new CustomUserDetails(user);
    }
}