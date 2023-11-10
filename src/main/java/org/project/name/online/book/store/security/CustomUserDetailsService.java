package org.project.name.online.book.store.security;

import lombok.RequiredArgsConstructor;
import org.project.name.online.book.store.exception.EntityNotFoundException;
import org.project.name.online.book.store.repository.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(
                () -> new EntityNotFoundException("Login or password is incorrect"));
    }
}
