package com.kraftadmin.kraft_mongo_documents;


import com.kraftadmin.annotations.DisplayField;
import com.kraftadmin.annotations.InternalAdminResource;
import com.kraftadmin.annotations.KraftAdminResource;
import com.kraftadmin.constants.PerformableAction;
import com.kraftadmin.constants.UserActionType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Setter
@Getter
//@Document(collection = "kraft_user_actions")
@KraftAdminResource(
        name = "User Actions",
        actions = {PerformableAction.READ})
@InternalAdminResource
public class AdminUserActionDocument {

    @Id
    private String id;

    @Enumerated()
    private UserActionType action;

    //    @DBRef
    @DisplayField("name")
    private AdminUserDocument adminUser;

    private Subject subject;

    @CreatedDate
    private LocalDateTime createdAt;

    public AdminUserActionDocument() {
    }
}

