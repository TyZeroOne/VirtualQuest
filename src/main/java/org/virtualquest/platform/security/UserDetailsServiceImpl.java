package org.virtualquest.platform.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.virtualquest.platform.model.Users;
import org.virtualquest.platform.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found or deleted"));

        List<GrantedAuthority> authorities = new ArrayList<>();

        // Добавляем основную роль пользователя
        authorities.add(new SimpleGrantedAuthority(user.getRoles().getName().name()));

        // Добавляем блокировки как дополнительные authorities
        if (user.isBanned()) {
            authorities.add(new SimpleGrantedAuthority("BANNED"));
        }
        if (!user.isCanPostReviews()) {
            authorities.add(new SimpleGrantedAuthority("REVIEWS_BLOCKED"));
        }

        return new User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}