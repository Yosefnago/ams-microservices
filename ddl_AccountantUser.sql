CREATE TABLE accountant_users
(
    id            BIGINT       NOT NULL,
    first_name    VARCHAR(25)  NOT NULL,
    last_name     VARCHAR(25)  NOT NULL,
    username      VARCHAR(25)  NOT NULL,
    business_name VARCHAR(255) NOT NULL,
    tax_id        VARCHAR(255) NULL,
    password      VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NULL,
    phone         VARCHAR(255) NULL,
    `role`        VARCHAR(255) NOT NULL,
    CONSTRAINT pk_accountant_users PRIMARY KEY (id)
);

ALTER TABLE accountant_users
    ADD CONSTRAINT uc_accountant_users_email UNIQUE (email);

ALTER TABLE accountant_users
    ADD CONSTRAINT uc_accountant_users_phone UNIQUE (phone);

ALTER TABLE accountant_users
    ADD CONSTRAINT uc_accountant_users_username UNIQUE (username);