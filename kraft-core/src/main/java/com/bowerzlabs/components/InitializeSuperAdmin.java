package com.bowerzlabs.components;

import com.bowerzlabs.config.KraftrProperties;
import com.bowerzlabs.constants.Role;
import com.bowerzlabs.models.kraftmodels.AdminUser;
import com.bowerzlabs.repository.kraftrepos.KraftAdminUsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
//@ConditionalOnProperty(name = "kraft.kraft-jpa.enabled", havingValue = "true")
public class InitializeSuperAdmin implements CommandLineRunner {
    private final KraftAdminUsersRepository kraftAdminUsersRepository;
    private final KraftrProperties adminProperties;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public InitializeSuperAdmin(KraftAdminUsersRepository kraftAdminUsersRepository, KraftrProperties adminProperties) {
        this.kraftAdminUsersRepository = kraftAdminUsersRepository;
        this.adminProperties = adminProperties;
    }

    /**
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        Optional<AdminUser> adminUser = kraftAdminUsersRepository.findByRole(Role.SUPER_ADMIN);
        // create super-admin if not present
        if (adminUser.isEmpty()){
            AdminUser adminUser1 = new AdminUser();
            adminUser1.setRole(Role.SUPER_ADMIN);
            adminUser1.setUsername(adminProperties.getAdminUsername());
            adminUser1.setName("Nyadero Brian Odhiambo");
            adminUser1.setPassword(passwordEncoder.encode(adminProperties.getPassword()));
            kraftAdminUsersRepository.save(adminUser1);
        }
    }
}
