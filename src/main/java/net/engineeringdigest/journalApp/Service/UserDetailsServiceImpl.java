package net.engineeringdigest.journalApp.Service;

import net.engineeringdigest.journalApp.Entity.User;
import net.engineeringdigest.journalApp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userByUserName = userRepository.findByUserName(username);
        if(userByUserName == null) {
            throw new UsernameNotFoundException("Username did not found: " + username);
        }
        // now we need to convert user that we have into UserDetails
        return org.springframework.security.core.userdetails.User.builder()
                .username(userByUserName.getUserName())
                .password(userByUserName.getPassword())
                .roles(userByUserName.getRoles().toArray(new String[0]))
                .build();
    }
}
