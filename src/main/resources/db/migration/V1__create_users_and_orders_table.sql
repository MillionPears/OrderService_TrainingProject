
CREATE TABLE users (
    user_id BINARY(16) PRIMARY KEY,   -- UUID lưu dưới dạng BINARY(16)
    name VARCHAR(255) NOT NULL,
    dob DATE NOT NULL ,
    phone_number VARCHAR(15) NOT NULL UNIQUE,
    gender ENUM('MALE', 'FEMALE') NOT NULL,
    point INTEGER
);


CREATE TABLE orders (
    order_id BINARY(16) PRIMARY KEY,
    create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    phone_number VARCHAR(15) NOT NULL ,
    address VARCHAR(255) NOT NULL,
    status VARCHAR(50) CHECK (status IN ('PENDING','PROCESSING','COMPLETED','CANCELED')),
    user_id BINARY(16) NOT NULL,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(user_id)
);
