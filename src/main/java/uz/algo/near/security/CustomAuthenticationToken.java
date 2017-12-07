package uz.algo.near.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import uz.algo.near.domains.User;

import java.util.Collection;

public class CustomAuthenticationToken implements Authentication {

    private User user;

    public CustomAuthenticationToken(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return user;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    public String getName() {
        return user.getEmail();
    }

    public void setUser(User user) {
        this.user = user;
    }

}