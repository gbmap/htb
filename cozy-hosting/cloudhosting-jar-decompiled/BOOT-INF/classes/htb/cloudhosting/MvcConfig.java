package htb.cloudhosting;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
   public void addViewControllers(ViewControllerRegistry registry) {
      registry.addViewController("/admin").setViewName("admin");
      registry.addViewController("/addhost").setViewName("addhost");
      registry.addViewController("/index").setViewName("index");
      registry.addViewController("/").setViewName("index");
      registry.addViewController("/login").setViewName("login");
   }
}
