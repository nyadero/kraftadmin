package com.bowerzlabs.config;

import com.bowerzlabs.EntitiesScanner;
import com.bowerzlabs.EntityMetaModel;
import com.bowerzlabs.annotations.KraftAdminResource;
import com.bowerzlabs.models.kraftmodels.AdminUser;
import com.bowerzlabs.models.kraftmodels.ResourceMetadata;
import com.bowerzlabs.models.kraftmodels.ResourceName;
import com.bowerzlabs.utils.ResourceGrouper;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalControllerAdvice {
    private static final Logger log = LoggerFactory.getLogger(GlobalControllerAdvice.class);
    private final KraftProperties properties;
    private final HttpSession httpSession;
    private final EntitiesScanner entitiesScanner;
    private final EntityManager entityManager;


    public GlobalControllerAdvice(KraftProperties properties, HttpSession httpSession, EntitiesScanner entitiesScanner, EntityManager entityManager) {
        this.properties = properties;
        this.httpSession = httpSession;
        this.entitiesScanner = entitiesScanner;
        this.entityManager = entityManager;
    }


    @ModelAttribute
    public void addGlobalAttributes(Model model, @AuthenticationPrincipal AdminUser adminUser) {
//        log.info("entities1 {}", entitiesScanner.getAllEntityClasses());

//        List<Class<?>> entityClasses = entitiesScanner.getAllEntityClasses();
        List<EntityMetaModel> entityClasses = entitiesScanner.getAllEntityClasses();


        List<ResourceMetadata> entities = entityClasses
                .stream()
                .map(entity -> {
                    if (entity.getEntityClass().getJavaType().isAnnotationPresent(KraftAdminResource.class)) {
                        KraftAdminResource kraftAdminResource = entity.getEntityClass().getJavaType().getAnnotation(KraftAdminResource.class);
                        return new ResourceMetadata(
                                new ResourceName(kraftAdminResource.name(), entity.getEntityClass().getJavaType().getSimpleName()),
                                kraftAdminResource.group(),
                                kraftAdminResource.icon(),
                                kraftAdminResource.editable()
                        );
                    }
                    return new ResourceMetadata(new ResourceName(entity.getEntityClass().getJavaType().getSimpleName(), entity.getEntityClass().getJavaType().getSimpleName()));
                })
                .collect(Collectors.toList());


        Map<String, List<ResourceMetadata>> grouped = ResourceGrouper.groupResources(entitiesScanner.getAllEntityClasses());

//        grouped.forEach((group, resources) -> {
//            System.out.println(" Group: " + group);
//            for (ResourceMetadata res : resources) {
//                System.out.println("   - " + res);
//            }
//        });

        model.addAttribute("entities", entities);
        // Only pass groupedEntities if grouping exists
//        if (!grouped.isEmpty()) {
//            model.addAttribute("groupedEntities", grouped);
//        } else {
//            model.addAttribute("entities", entities);
//        }
        model.addAttribute("loggedinUser", adminUser);
        model.addAttribute("title", properties.getTitle());
        model.addAttribute("httpSession", httpSession.toString());
    }

//    @ModelAttribute
//    public void checkAdminSession(HttpServletRequest request, HttpServletResponse response, Authentication auth) throws IOException {
//        if (request.getRequestURI().startsWith("/admin")) {
//            if (auth == null || !(auth.getPrincipal() instanceof AdminUser)) {
//                request.getSession().invalidate();
//                response.sendRedirect("/admin/auth/login");
//            }
//        }
//    }

}
