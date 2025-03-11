
CREATE TABLE users (
    id                  BINARY(16)                  PRIMARY KEY,
    name                VARCHAR(255)                NOT NULL,
    dob                 DATE NOT NULL,
    phone_number        VARCHAR(15)                 NOT NULL            UNIQUE,
    gender              ENUM('MALE', 'FEMALE')      NOT NULL,
    point               INTEGER,
    created_date        TIMESTAMP                   DEFAULT CURRENT_TIMESTAMP,
    last_modified_date  TIMESTAMP                   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version             BIGINT
);

CREATE TABLE orders (
    id                  BIGINT AUTO_INCREMENT       PRIMARY KEY,
    phone_number        VARCHAR(15)                 NOT NULL,
    customer_name       VARCHAR(255)                NOT NULL,
    address             VARCHAR(255)                NOT NULL,
    note                VARCHAR(255),
    status              ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'CANCELED')      NOT NULL,
    payment_method      ENUM('CASH', 'BANK_TRANSFER', 'CREDIT_CARD')                NOT NULL,
    user_id             BINARY(16)                  NOT NULL,
    created_date        TIMESTAMP                   DEFAULT CURRENT_TIMESTAMP,
    last_modified_date  TIMESTAMP                   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version             BIGINT,
    CONSTRAINT          fk_user_id                  FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_users_name_gender ON users(name,gender);
CREATE INDEX idx_users_dob ON users(dob);
