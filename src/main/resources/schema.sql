CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password VARCHAR(100) NOT NULL
);

CREATE TABLE messages (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          sender_id BIGINT NOT NULL,
                          recipient_id BIGINT,
                          group_id BIGINT,
                          content VARCHAR(500),
                          sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE group_chat (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL
);
