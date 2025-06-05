package com.bowerzlabs.admin_users;

import com.bowerzlabs.EntitiesScanner;
import com.bowerzlabs.constants.Status;
import com.bowerzlabs.events.UIEvent;
import com.bowerzlabs.models.kraftmodels.AdminUser;
import com.bowerzlabs.repository.kraftrepos.KraftAdminUsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/administrators")
public class KraftAdminUserController {
    private static final Logger log = LoggerFactory.getLogger(KraftAdminUserController.class);
    //    KraftAdminUsersRepository adminUserRepository = SpringContextHolder.getBean(KraftAdminUsersRepository.class);
    private final KraftAdminUsersRepository userRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final EntitiesScanner entitiesScanner;

    public KraftAdminUserController(KraftAdminUsersRepository kraftAdminUsersRepository, ApplicationEventPublisher applicationEventPublisher, EntitiesScanner entitiesScanner) {
        this.userRepository = kraftAdminUsersRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.entitiesScanner = entitiesScanner;
    }

    // get administrators
    @GetMapping
    private String administrators(
            Model model
    ) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id", "createdAt");
        List<AdminUser> adminUsers = userRepository.findAll();
//        if (!adminUsers.isEmpty()){
//            AdminUser firstItem = adminUsers.get(0);
//            Class<?> entityInstance = entitiesScanner.getEntityByName("AdminUsers");
//            log.info("entityInstance {} ", entityInstance);
//            log.info("first item class {}", firstItem.getClass().getSimpleName());
//            DbObjectSchema schema = new DbObjectSchema(entityInstance, firstItem);
//            List<String> fieldNames = new ArrayList<>(schema.getFieldsWithData().keySet());
//            model.addAttribute("fieldNames", fieldNames);
//        }
        log.info("administrators {}", adminUsers);
        model.addAttribute("administrators", adminUsers);
        model.addAttribute("size", adminUsers.size());
        return "admins/index";
    }

    // admin to add moderator user
//    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    @GetMapping("/add-admin")
    private String renderAddAdmin(
            Model model
    ) {
        return "admins/add-admin";
    }

    // save admin user
//    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    @PostMapping("/add-admin")
    private String saveAdminUser(
            @ModelAttribute AdminUser adminUser
    ) {
        log.info("Admin user {}", adminUser);
        try {
            AdminUser adminUser1 = new AdminUser();
            adminUser1.setName(adminUser.getName());
            adminUser1.setRole(adminUser.getRole());
            adminUser1.setUsername(adminUser.getUsername());
            adminUser1.setPassword(new BCryptPasswordEncoder(6).encode(adminUser.getPassword()));
            userRepository.save(adminUser1);
            applicationEventPublisher.publishEvent(new UIEvent(this, "Administrator added successfully", Status.Success));
        } catch (Exception e) {
            applicationEventPublisher.publishEvent(new UIEvent(this, "Error adding Administrator", Status.Error));
            throw new RuntimeException(e);
        }
        return "redirect:/admin/administrators";
    }

    // admin to remove moderator user
//    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/delete/{id}")
    private String deleteAdminUser(
            @PathVariable("id") Long id
    ) {
        try {
            Optional<AdminUser> adminUser = userRepository.findById(id.longValue());
            adminUser.ifPresent(userRepository::delete);
            applicationEventPublisher.publishEvent(new UIEvent(this, "Administrator deleted successfully", Status.Success));
        } catch (Exception e) {
            applicationEventPublisher.publishEvent(new UIEvent(this, "Error deleting Administrator", Status.Error));
            throw new RuntimeException(e);
        }
        return "redirect:/admin/administrators";
    }
    
}
