package uz.algo.near.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import uz.algo.near.repositories.UserRepository;
import uz.algo.near.security.CustomAuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class AuthController {

    private ProviderSignInUtils providerSignInUtils;
    private UserRepository userRepository;

    @Autowired
    public AuthController(ConnectionFactoryLocator connectionFactoryLocator,
                          UsersConnectionRepository usersConnectionRepository,
                          UserRepository userRepository) {
        this.userRepository = userRepository;
        providerSignInUtils = new ProviderSignInUtils(connectionFactoryLocator, usersConnectionRepository);
    }

    @RequestMapping("/signin")
    public String signIn(WebRequest webRequest, HttpServletRequest request, HttpServletResponse response) {
        Connection<?> facebookConnection = providerSignInUtils.getConnectionFromSession(webRequest);
        if (facebookConnection != null) {
            Facebook facebook = (Facebook) facebookConnection.getApi();
            User userProfile = facebook.fetchObject("me", User.class, "email");
            uz.algo.near.domains.User user = userRepository.findByEmail(userProfile.getEmail());
            if (user == null) {
                user = new uz.algo.near.domains.User();
                user.setEmail(userProfile.getEmail());
            }
            user.setDisplayName(facebookConnection.getDisplayName());
            user.setProfileImage(facebookConnection.getImageUrl());
            userRepository.save(user);
            CustomAuthenticationToken authenticationToken = new CustomAuthenticationToken(user);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            return "redirect:/";
        }
        return "sign-in";
    }

}
