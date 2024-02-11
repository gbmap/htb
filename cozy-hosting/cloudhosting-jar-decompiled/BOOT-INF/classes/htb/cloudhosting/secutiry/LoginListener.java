package htb.cloudhosting.secutiry;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

@Component
@Endpoint(
   id = "sessions"
)
public class LoginListener implements ApplicationListener<InteractiveAuthenticationSuccessEvent>, HttpSessionListener {
   private final Map<String, String> sessionMap = new ConcurrentHashMap<>();

   public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
      String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
      UserDetails userDetails = (UserDetails)event.getAuthentication().getPrincipal();
      this.sessionMap.put(sessionId, userDetails.getUsername());
   }

   public void sessionCreated(HttpSessionEvent se) {
      if (this.sessionMap.size() < 10) {
         this.sessionMap.put(se.getSession().getId(), "UNAUTHORIZED");
      }
   }

   public void sessionDestroyed(HttpSessionEvent se) {
      String sessionId = se.getSession().getId();
      System.out.println("Removing session due to timeout - " + sessionId);
      this.sessionMap.remove(sessionId);
   }

   @ReadOperation
   public Map<String, String> getActiveSessions() {
      return this.sessionMap;
   }
}
