package it.ai.polito.esercitazione3_2.esercitazione3_2.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {


        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/auth/signin").permitAll()
                .antMatchers(HttpMethod.GET, "/notification/**").permitAll()
                .antMatchers(HttpMethod.GET, "/API/courses/**").permitAll()
                .antMatchers(HttpMethod.POST, "/API/courses/**").hasRole("TEACHER")
                .antMatchers(HttpMethod.GET, "/API/students/{id}/**").hasRole("STUDENT")
                .antMatchers(HttpMethod.GET, "/API/students/courses/**").hasRole("STUDENT")
                .antMatchers(HttpMethod.POST, "/API/students/courses/**").hasRole("STUDENT")
                .antMatchers(HttpMethod.GET, "/API/students/**").permitAll()
                .antMatchers(HttpMethod.POST, "/API/students/**").permitAll()
                .antMatchers(HttpMethod.GET, "/API/professors").permitAll()
                .antMatchers(HttpMethod.POST, "/API/professors/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider))
               ;

    }

}
