package uz.algo.near.security;

import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import uz.algo.near.domains.User;

import javax.annotation.PostConstruct;

@Component
public class SecurityUtil {

    public User getCurrentUser() {
        if (SecurityContextHolder.getContext()
                .getAuthentication() instanceof CustomAuthenticationToken) {
            return (User) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
        } else if (SecurityContextHolder.getContext()
                .getAuthentication() instanceof RememberMeAuthenticationToken) {
            return ((CustomUserDetails) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal()).getUser();
        } else {
            return null;
        }
    }

}
