package culturelog.rest.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * @author Jan Venstermans
 */
@Service
public class CultureLogSecurityService {

    public boolean isLoggedIn() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return securityContext.getAuthentication() != null;
    }

    public String getLoggedInUsername() {
        CultureLogUserDetails userDetails = getLoggedInCultureLogUserDetails();
        if (userDetails != null) {
            return userDetails.getUsername();
        }
        return null;
    }

    public String getLoggedInUserId() {
        CultureLogUserDetails userDetails = getLoggedInCultureLogUserDetails();
        if (userDetails != null) {
            return userDetails.getUserId();
        }
        return null;
    }

    private CultureLogUserDetails getLoggedInCultureLogUserDetails() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            if (authentication.getPrincipal() instanceof CultureLogUserDetails) {
                CultureLogUserDetails userDetails = (CultureLogUserDetails) authentication.getPrincipal();
                return userDetails;
            } else {
                //what than?
            }
        }
        return null;
    }

}
