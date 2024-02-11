package htb.cloudhosting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CozyHostingApp {
   public static void main(String[] args) {
      SpringApplication.run(CozyHostingApp.class, args);
   }
}
