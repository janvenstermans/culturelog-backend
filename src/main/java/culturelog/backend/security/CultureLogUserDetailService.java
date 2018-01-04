package culturelog.backend.security;

import culturelog.backend.domain.User;
import culturelog.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author Jan Venstermans
 */
@Service
public class CultureLogUserDetailService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final User user = userService.getByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new CultureLogUserDetails(user);
    }
}
