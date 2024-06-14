package ru.emitrohin.adminui.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.emitrohin.adminui.repository.AdminUserRepository;
import ru.emitrohin.data.model.AdminUser;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserDetailsService implements UserDetailsService {

    private static final String VAADIN_ROLE_PREFIX = "ROLE_";

    private final AdminUserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(u -> new User(u.getUsername(), u.getPassword(), getAuthorities(u) ))
                .orElseThrow(() -> new UsernameNotFoundException("No user present with username: " + username));
    }

    private static List<GrantedAuthority> getAuthorities(AdminUser user) {
        var role = new SimpleGrantedAuthority(VAADIN_ROLE_PREFIX + user.getRole().name());
        return List.of(role);
    }

}
