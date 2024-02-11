package htb.cloudhosting.scheduled;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FakeUser {
   @Scheduled(
      timeUnit = TimeUnit.MINUTES,
      fixedDelay = 5L
   )
   public void login() throws IOException {
      System.out.println("Logging in user ...");
      Runtime.getRuntime()
         .exec(
            new String[]{
               "curl",
               "localhost:8080/login",
               "--request",
               "POST",
               "--header",
               "Content-Type: application/x-www-form-urlencoded",
               "--data-raw",
               "username=kanderson&password=MRdEQuv6~6P9",
               "-v"
            }
         );
   }
}
