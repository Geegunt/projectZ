package ru.mai.topit.volunteers.platform.userinfo.infrastructure.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.mai.topit.volunteers.platform.userinfo.domain.User;
import ru.mai.topit.volunteers.platform.userinfo.domain.vo.UserRole;
import ru.mai.topit.volunteers.platform.userinfo.infrastructure.repository.UserRepository;

import java.util.Collection;
import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var authorities = Collections.singletonList(new SimpleGrantedAuthority(UserRole.VOLUNTEER.name()));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getLogin())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}


