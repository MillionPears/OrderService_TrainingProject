ALTER TABLE users
ADD COLUMN created_date TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE orders
DROP COLUMN create_date,
ADD COLUMN created_date TIMESTAMP  DEFAULT CURRENT_TIMESTAMP ,
ADD COLUMN last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

