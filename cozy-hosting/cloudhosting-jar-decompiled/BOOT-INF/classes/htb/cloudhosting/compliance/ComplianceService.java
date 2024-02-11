package htb.cloudhosting.compliance;

import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ComplianceService {
   private final Pattern HOST_PATTERN = Pattern.compile(
      "^(?=.{1,255}$)[0-9A-Za-z](?:(?:[0-9A-Za-z]|-){0,61}[0-9A-Za-z])?(?:\\.[0-9A-Za-z](?:(?:[0-9A-Za-z]|-){0,61}[0-9A-Za-z])?)*\\.?$"
   );

   @RequestMapping(
      method = {RequestMethod.POST},
      path = {"/executessh"}
   )
   public void executeOverSsh(@RequestParam("username") String username, @RequestParam("host") String host, HttpServletResponse response) throws IOException {
      StringBuilder rbuilder = new StringBuilder("/admin?error=");

      try {
         this.validateHost(host);
         this.validateUserName(username);
         Process process = Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c", String.format("ssh -o ConnectTimeout=1 %s@%s", username, host)});
         new BufferedReader(new InputStreamReader(process.getErrorStream())).lines().forEach(line -> {
            if (!line.startsWith("Pseudo-terminal")) {
               rbuilder.append(line);
            }
         });
      } catch (IllegalArgumentException var10) {
         rbuilder.append(var10.getMessage());
      } catch (Exception var11) {
         rbuilder.append("ssh: Cannot connect to the host");
      } finally {
         response.sendRedirect(rbuilder.toString());
      }
   }

   private void validateUserName(String username) {
      if (username.contains(" ")) {
         throw new IllegalArgumentException("Username can't contain whitespaces!");
      }
   }

   private void validateHost(String host) {
      if (!this.HOST_PATTERN.matcher(host).matches()) {
         throw new IllegalArgumentException("Invalid hostname!");
      }
   }
}
