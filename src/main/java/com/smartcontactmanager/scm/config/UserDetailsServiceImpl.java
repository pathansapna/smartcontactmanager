package com.smartcontactmanager.scm.config;

import com.smartcontactmanager.scm.dao.UserRepository;
import com.smartcontactmanager.scm.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        //    Fetching user from database
        User user = userRepository.getUserByUserName(s);

        if(user==null){
            throw new UsernameNotFoundException("Could not found user !!");
        }

        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        return customUserDetails;
    }
}
