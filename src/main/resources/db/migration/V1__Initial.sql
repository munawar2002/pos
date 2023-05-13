CREATE TABLE USERS (
ID INT AUTO_INCREMENT PRIMARY KEY,
username VARCHAR(100),
password VARCHAR(255) not null,
ACTIVE TINYINT(1) DEFAULT 1,
first_name VARCHAR(100) not null,
last_name VARCHAR(100) not null,
email VARCHAR(100) ,
password_hint VARCHAR(255),
created_by VARCHAR(100),
created_at TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP,
updated_by VARCHAR(100),
updated_at TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP,
terminal_id INT,
UNIQUE(username)
);

CREATE TABLE ROLE (
ID INT AUTO_INCREMENT PRIMARY KEY,
NAME VARCHAR(200),
ACTIVE TINYINT(1) DEFAULT 1,
created_by VARCHAR(100),
created_at TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP,
updated_by VARCHAR(100),
updated_at TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP,
terminal_id INT,
UNIQUE(NAME)
);

CREATE TABLE USER_ROLE (
ID INT AUTO_INCREMENT PRIMARY KEY,
USER_ID INT,
ROLE_ID INT,
created_by VARCHAR(100),
created_at TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP,
updated_by VARCHAR(100),
updated_at TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP,
terminal_id INT,
UNIQUE(USER_ID, ROLE_ID)
);

INSERT INTO USERS (ID, username, password, first_name, last_name, email, password_hint, created_by, updated_by)
 values (1, 'admin', '123', 'Admin', 'Munawar', 'abc@gmail.com', '123', 'munawar', 'java');

INSERT INTO ROLE (ID, NAME, CREATED_BY) VALUES (1, 'ADMIN', 'admin'),
 (2, 'RECEPTIONIST', 'admin'), (3, 'SUPER_ADMIN', 'admin');

INSERT INTO USER_ROLE (USER_ID, ROLE_ID) VALUES (1, 3);