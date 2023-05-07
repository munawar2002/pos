CREATE TABLE PRODUCT_CATEGORY (
ID INT AUTO_INCREMENT PRIMARY KEY,
NAME VARCHAR(200) NOT NULL,
DESCRIPTION VARCHAR(200),
created_by VARCHAR(100),
created_at TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP,
updated_by VARCHAR(100),
updated_at TIMESTAMP
);

CREATE TABLE PRODUCT (
ID INT AUTO_INCREMENT PRIMARY KEY,
CODE VARCHAR(100) NOT NULL,
NAME VARCHAR(200) NOT NULL,
QUANTITY INT NOT NULL,
CATEGORY_ID INT,
PRODUCT_COMPANY_ID INT,
SUPPLIER_ID INT,
BUY_PRICE DECIMAL(12,2),
SELL_PRICE DECIMAL(12,2),
created_by VARCHAR(100),
created_at TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP,
updated_by VARCHAR(100),
updated_at TIMESTAMP
);

CREATE TABLE PRODUCT_PHOTO (
ID INT AUTO_INCREMENT PRIMARY KEY,
PRODUCT_ID INT NOT NULL,
IMAGE LONGBLOB,
created_by VARCHAR(100),
created_at TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP,
updated_by VARCHAR(100),
updated_at TIMESTAMP
);

CREATE TABLE SUPPLIER (
ID INT AUTO_INCREMENT PRIMARY KEY,
NAME VARCHAR(200) NOT NULL,
ADDRESS VARCHAR(255),
CONTACT_NO VARCHAR(40),
CONTACT_PERSON VARCHAR(200),
CONTACT_PERSON_NO VARCHAR(40),
created_by VARCHAR(100),
created_at TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP,
updated_by VARCHAR(100),
updated_at TIMESTAMP
);

CREATE TABLE PRODUCT_COMPANY (
ID INT AUTO_INCREMENT PRIMARY KEY,
NAME VARCHAR(200) NOT NULL,
ADDRESS VARCHAR(255),
CONTACT_NO VARCHAR(40),
CONTACT_PERSON VARCHAR(200),
CONTACT_PERSON_NO VARCHAR(40),
created_by VARCHAR(100),
created_at TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP,
updated_by VARCHAR(100),
updated_at TIMESTAMP
);

insert into PRODUCT_CATEGORY (name, description) values ('category1', 'description1');
insert into SUPPLIER (name, address, contact_no) values ('supplier1', 'address1', '2818888555');
insert into PRODUCT_COMPANY (name, address, contact_no) values ('company1', 'company address1', '5219999666');