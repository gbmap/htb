package htb.cloudhosting.secutiry;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer.AuthorizedUrl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      http.addFilterBefore(
         (request, response, chain) -> {
            if (SecurityContextHolder.getContext().getAuthentication() != null
               && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
               && ((HttpServletRequest)request).getRequestURI().equals("/login")) {
               System.out.println("user is authenticated but trying to access login page, redirecting to /");
               ((HttpServletResponse)response).sendRedirect("/admin");
            }
   
            chain.doFilter(request, response);
         },
         DefaultLoginPageGeneratingFilter.class
      );
      http.csrf().disable();
      ((HttpSecurity)http.authorizeHttpRequests(requests -> ((AuthorizedUrl)requests.requestMatchers(new String[]{"/admin"})).hasAuthority("User"))
            .authorizeHttpRequests(
               requests -> ((AuthorizedUrl)((AuthorizedUrl)requests.requestMatchers(new String[]{"/**"})).permitAll().anyRequest()).authenticated()
            )
            .httpBasic()
            .and())
         .formLogin(form -> ((FormLoginConfigurer)form.defaultSuccessUrl("/admin", true)).loginPage("/login").permitAll())
         .logout(LogoutConfigurer::permitAll);
      return (SecurityFilterChain)http.build();
   }

   @Bean
   protected PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
   }
}
