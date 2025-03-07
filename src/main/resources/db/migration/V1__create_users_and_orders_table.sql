
CREATE TABLE users (
    id                  BINARY(16)                  PRIMARY KEY,
    name                VARCHAR(255)                NOT NULL,
    dob                 DATE NOT NULL,
    phone_number        VARCHAR(15)                 NOT NULL            UNIQUE,
    gender              ENUM('MALE', 'FEMALE')      NOT NULL,
    point               INTEGER
);

CREATE TABLE orders (
    id                  BIGINT AUTO_INCREMENT       PRIMARY KEY,
    phone_number        VARCHAR(15)                 NOT NULL,
    customer_name       VARCHAR(255)                NOT NULL,
    address             VARCHAR(255)                NOT NULL,
    status              ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'CANCELED')      NOT NULL,
    payment_method      ENUM('CASH', 'BANK_TRANSFER', 'CREDIT_CARD')                NOT NULL,
    user_id             BINARY(16)                  NOT NULL,
    CONSTRAINT          fk_user_id                  FOREIGN KEY (user_id) REFERENCES users(id)
);
