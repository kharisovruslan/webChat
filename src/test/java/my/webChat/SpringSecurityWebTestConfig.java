package my.webChat;

import my.webChat.data.Role;
import my.webChat.data.User;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Arrays;
import java.util.Collections;

@TestConfiguration
public class SpringSecurityWebTestConfig {
    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        User basicUser = new User("user1", "password");
        basicUser.setId(1L);
        basicUser.setRoles(Collections.singleton(Role.USER));
        basicUser.setEnable(true);
        User managerUser = new User("user2", "password");
        managerUser.setId(2L);
        managerUser.setRoles(Collections.singleton(Role.USER));
        managerUser.setEnable(true);
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return basicUser;
            }
        };
    }
}
