CREATE TABLE USERS (
ID INT AUTO_INCREMENT PRIMARY KEY,
username VARCHAR(100),
password VARCHAR(255) not null,
first_name VARCHAR(100) not null,
last_name VARCHAR(100) not null,
email VARCHAR(100) ,
password_hint VARCHAR(255),
created_by VARCHAR(100),
created_at TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP,
updated_by VARCHAR(100),
updated_at TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP,
UNIQUE(username)
);

INSERT INTO USERS (username, password, first_name, last_name, email, password_hint, created_by, updated_by)
 values ('admin', '123', 'Admin', 'Munawar', 'abc@gmail.com', '123', 'munawar', 'java');