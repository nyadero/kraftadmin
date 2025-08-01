CREATE TABLE kraft_admin_users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50),
    avatar VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- no ON UPDATE in Postgres
);

CREATE TABLE kraft_user_actions (
    id BIGSERIAL PRIMARY KEY,
    action VARCHAR(50),
    admin_user_id BIGINT,
    table_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_admin_user FOREIGN KEY (admin_user_id) REFERENCES kraft_admin_users(id)
);


-- DisplayFieldsPreference Table
CREATE TABLE display_field_preferences (
    id VARCHAR(255) PRIMARY KEY,
    fields TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
