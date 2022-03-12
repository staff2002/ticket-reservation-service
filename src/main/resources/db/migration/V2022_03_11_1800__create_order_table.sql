CREATE TABLE `orders`
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    flight_id BIGINT,
    flight_status VARCHAR(50),
    status VARCHAR(50),
    amount DECIMAL(10,2),
    created_at TIMESTAMP
);
