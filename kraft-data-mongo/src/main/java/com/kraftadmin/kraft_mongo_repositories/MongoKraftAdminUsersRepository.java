package com.kraftadmin.kraft_mongo_repositories;

import com.kraftadmin.kraft_mongo_documents.AdminUserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoKraftAdminUsersRepository extends MongoRepository<AdminUserDocument, String> {
}