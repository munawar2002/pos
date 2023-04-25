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
CATEGORY_ID INT,
PRODUCT_COMPANY_ID INT,
BUY_PRICE INT,
SELL_PRICE INT,
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

insert into PRODUCT_CATEGORY (name, description) values ('category1', 'description1');