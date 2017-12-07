package uz.algo.near.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.social.security.SpringSocialConfigurer;
import uz.algo.near.security.CustomAuthenticationProvider;
import uz.algo.near.security.CustomUserDetailService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomAuthenticationProvider authenticationProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .formLogin()
                .loginPage("/signin")
//                .failureUrl("/signin?param.error=bad_credentials")
                .permitAll()
                .and()
                .logout()
                .deleteCookies("JSESSIONID")
                .and()
                .authorizeRequests()
                .antMatchers("/webjars/**", "/assets/**").permitAll()
                .antMatchers("/**").authenticated()
                .and()
                .logout().logoutSuccessUrl("/signin?logout")
                .logoutRequestMatcher(new AntPathRequestMatcher("/signout", "GET"))
                .permitAll()
                .and()
                .apply(new SpringSocialConfigurer().alwaysUsePostLoginUrl(true));
        http.csrf().disable();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }

}
