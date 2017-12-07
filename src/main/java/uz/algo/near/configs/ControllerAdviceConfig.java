package uz.algo.near.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import uz.algo.near.security.SecurityUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice(basePackages = {"uz.algo.near.controllers"})
public class ControllerAdviceConfig {

    @Autowired
    private SecurityUtil securityUtil;

    @ModelAttribute
    public void addUserAttribute(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        model.addAttribute("user", securityUtil.getCurrentUser());
    }

}
