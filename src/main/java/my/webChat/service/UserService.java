package my.webChat.service;

import my.webChat.data.Role;
import my.webChat.data.User;
import my.webChat.data.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    UserRepository repository;
    @Autowired
    PasswordEncoder encoder;

    public boolean addUser(User user) {
        Optional<User> optionalUser = repository.findByUsername(user.getUsername());
        if ((user.getRoles() == null) || (user.getRoles().isEmpty())) {
            if (repository.findAll().size() == 0) {
                user.setRoles(Set.of(Role.ADMIN, Role.USER));
            } else {
                user.setRoles(Set.of(Role.USER));
            }
        }
        if (optionalUser.isEmpty()) {
            user.setEnable(true);
            user.setPassword(encoder.encode(user.getPassword()));
            user.setToken(UUID.randomUUID());
            repository.save(user);
            return true;
        }
        return false;
    }

    public void updateVisited(User user) {
        user.setVisited(LocalDateTime.now());
        repository.save(user);
    }

    public void updatePassword(User user, String password) {
        if (StringUtils.hasText(password)) {
            user.setPassword(encoder.encode(password));
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User userD = (User) userDetails;
            if(userD.getId().equals(user.getId())) {
                userD.setPassword(user.getPassword());
            }
            repository.save(user);
        }
    }

    public User getByUserName(String name) {
        return repository.findByUsername(name)
                .orElseThrow(() -> new IllegalArgumentException("user " + name + " not found"));
    }

    public List<String> getUsersNames(User exceptUser) {
        return repository.findAll().stream()
                .filter(u -> u.getUsername().compareToIgnoreCase(exceptUser.getUsername()) != 0)
                .map(User::getUsername).collect(Collectors.toList());
    }

    public List<User> getUsers(User exceptUser) {
        return repository.findAll().stream()
                .filter(u -> u.getUsername().compareToIgnoreCase(exceptUser.getUsername()) != 0)
                .sorted(Comparator.comparing(User::getUsername))
                .collect(Collectors.toList());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Error find user"));
    }

    public Set<User> getUsers() {
        return repository.findAll().stream().collect(Collectors.toSet());
    }

    public void updateUserName(User user, String username) {
        if (StringUtils.hasText(username)) {
            user.setUsername(username);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User userD = (User) userDetails;
            if(userD.getId().equals(user.getId())) {
                userD.setUsername(user.getUsername());
            }
            repository.save(user);
        }
    }

    public void updateToken(User user) {
        user.setToken(UUID.randomUUID());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User userD = (User) userDetails;
        if(userD.getId().equals(user.getId())) {
            userD.setToken(user.getToken());
        }
        repository.save(user);
    }

    public User getUserByToken(UUID token) {
        User user = repository.findByToken(token).
                orElseThrow(() -> new IllegalArgumentException("User with token " + token + " not found"));
        if(!user.isEnabled()) {
            throw new IllegalArgumentException("User with token " + token + " is disable");
        }
        return user;
    }
}
