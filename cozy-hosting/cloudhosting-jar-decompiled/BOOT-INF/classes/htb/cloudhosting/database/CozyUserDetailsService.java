package htb.cloudhosting.database;

import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CozyUserDetailsService implements UserDetailsService {
   @Autowired
   private UserRepository userRepository;

   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      CozyUser user = (CozyUser)this.userRepository.findById(username).orElseThrow(() -> new UsernameNotFoundException("No such user"));
      return new User(username, user.getPassword(), Collections.singletonList(new SimpleGrantedAuthority(user.getRole())));
   }
}
