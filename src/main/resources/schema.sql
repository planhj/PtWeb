CREATE TABLE IF NOT EXISTS user (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    avatar VARCHAR(255) NOT NULL,
                                    create_at DATETIME(6) NOT NULL,
                                    custom_title VARCHAR(255) NOT NULL,
                                    download_bandwidth VARCHAR(255) NOT NULL,
                                    downloaded BIGINT NOT NULL,
                                    email VARCHAR(255) NOT NULL,
                                    invite_slot INT NOT NULL,
                                    passkey VARCHAR(255) NOT NULL,
                                    password VARCHAR(255) NOT NULL,
                                    personal_access_token VARCHAR(255) NOT NULL,
                                    privacy_level VARCHAR(10) NOT NULL,
                                    real_downloaded BIGINT NOT NULL,
                                    real_uploaded BIGINT NOT NULL,
                                    score DECIMAL(38, 2) NOT NULL,
                                    seeding_time BIGINT NOT NULL,
                                    signature VARCHAR(255) NOT NULL,
                                    upload_bandwidth VARCHAR(255) NOT NULL,
                                    uploaded BIGINT NOT NULL,
                                    username VARCHAR(255) NOT NULL,
                                    CONSTRAINT UK2v3v0uxl1rke2bks4g123axwq UNIQUE (passkey),
                                    CONSTRAINT UKob8kqyqqgmefl0aco34akdtpe UNIQUE (email),
                                    CONSTRAINT UKsb8bbouer5wak8vyiiy4pf2bx UNIQUE (username)
);

CREATE TABLE IF NOT EXISTS settings (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        setting_key VARCHAR(255) NOT NULL,
                                        setting_value MEDIUMTEXT NOT NULL,
                                        CONSTRAINT UKswd05dvj4ukvw5q135bpbbfae UNIQUE (setting_key)
);

CREATE TABLE IF NOT EXISTS login_history (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             ip_address VARCHAR(255) NULL,
                                             login_time DATETIME(6) NOT NULL,
                                             login_type VARCHAR(20) NOT NULL,
                                             user_agent VARCHAR(255) NULL,
                                             user_id BIGINT NOT NULL
);

CREATE INDEX IDX3lft44makrxommxm63k7xj77d ON login_history (login_time);

CREATE TABLE IF NOT EXISTS peers (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     download_speed BIGINT NOT NULL,
                                     downloaded BIGINT NOT NULL,
                                     info_hash VARCHAR(255) NOT NULL,
                                     ip VARCHAR(255) NOT NULL,
                                     to_go BIGINT NOT NULL,
                                     partial_seeder BIT NOT NULL,
                                     passkey VARCHAR(255) NOT NULL,
                                     peer_id VARCHAR(255) NOT NULL,
                                     port INT NOT NULL,
                                     seeder BIT NOT NULL,
                                     seeding_time BIGINT NOT NULL,
                                     update_at DATETIME(6) NOT NULL,
                                     upload_speed BIGINT NOT NULL,
                                     uploaded BIGINT NOT NULL,
                                     user_agent VARCHAR(255) NOT NULL,
                                     user_id BIGINT NULL,
                                     CONSTRAINT UKoa8l3xqdvxr898mosks3hq3cb UNIQUE (ip, port, info_hash),
                                     CONSTRAINT FK77m1r08bpaft9mvughtmnfdxi FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE INDEX  IDXmmvk33liy7j5u9e4qhxw2d7h5 ON peers (update_at);
