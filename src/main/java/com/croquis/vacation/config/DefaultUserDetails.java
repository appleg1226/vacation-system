package com.croquis.vacation.config;

import com.croquis.vacation.domain.UserInfo;
import com.croquis.vacation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DefaultUserDetails implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<UserInfo> result = userRepository.findById(s);
        return result.orElseThrow(()->new UsernameNotFoundException("User " + s + " not found"));
    }
}
