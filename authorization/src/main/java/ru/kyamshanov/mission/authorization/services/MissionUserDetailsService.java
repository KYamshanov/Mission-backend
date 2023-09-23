package ru.kyamshanov.mission.authorization.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.kyamshanov.mission.authorization.entities.UserEntity;
import ru.kyamshanov.mission.authorization.repositories.UserRepository;

import java.util.Optional;

@Service
public class MissionUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);

        UserEntity user = optionalUser.orElseThrow(() -> new UsernameNotFoundException(username));

        return new org.springframework.security.core.userdetails.User(user.getId(), user.getPassword(), user.getEnabled(), true, true, true, AuthorityUtils.NO_AUTHORITIES);
    }
}
