package gdg.hongik.loca.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AdminEmailWhitelist {

    private final Set<String> adminEmails;

    public AdminEmailWhitelist(
            @Value("${admin.emails:}") String adminEmails
    ) {
        this.adminEmails = Arrays.stream(adminEmails.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(email -> !email.isBlank())
                .collect(Collectors.toSet());
    }

    public boolean contains(String email) {
        return email != null
                && adminEmails.contains(email.trim().toLowerCase());
    }
}