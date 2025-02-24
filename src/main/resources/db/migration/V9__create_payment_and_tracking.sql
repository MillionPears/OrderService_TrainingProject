CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    method VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    payment_date TIMESTAMP,
    transaction_id VARCHAR(255),
    version BIGINT,
    created_date TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

CREATE TABLE user_activities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BINARY(16) NOT NULL,
    action VARCHAR(100) NOT NULL,
    endpoint VARCHAR(255) NOT NULL,
    request_data TEXT,
    response_data TEXT,
    duration BIGINT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT,
    created_date TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

