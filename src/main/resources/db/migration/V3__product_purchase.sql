CREATE TABLE PURCHASE_ORDER (
ID INT AUTO_INCREMENT PRIMARY KEY,
SUPPLIER_ID INT,
ORDER_DATE DATE,
AMOUNT DECIMAL(12,2),
PAYMENT_TYPE VARCHAR(200),
ORDER_STATUS VARCHAR(200),
PAYMENT_STATUS VARCHAR(200),
SHIPPING_DATE DATE,
SHIPPING_REQUIRED TINYINT(1) DEFAULT 0,
REMARKS VARCHAR(255),
CREATED_BY VARCHAR(100),
CREATED_AT TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP,
UPDATED_BY VARCHAR(100),
UPDATED_AT TIMESTAMP
);

CREATE TABLE PURCHASE_ORDER_DETAIL (
ID INT AUTO_INCREMENT PRIMARY KEY,
PURCHASE_ORDER_ID INT,
PRODUCT_ID INT,
BOX_PRICE DECIMAL(12,2),
BOX_QUANTITY INT,
QUANTITY_PER_BOX INT,
TOTAL_QUANTITY INT,
TOTAL_PRICE DECIMAL(12,2),
UNIT_PRICE DECIMAL(12,2),
REMARKS VARCHAR(255),
CREATED_BY VARCHAR(100),
CREATED_AT TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP,
UPDATED_BY VARCHAR(100),
UPDATED_AT TIMESTAMP
);

CREATE TABLE PURCHASE_INVOICE (
ID INT AUTO_INCREMENT PRIMARY KEY,
PURCHASE_ORDER_ID INT,
SUPPLIER_ID INT,
PAYMENT_TYPE VARCHAR(200),
PAID_AMOUNT DECIMAL(12,2),
TOTAL_AMOUNT DECIMAL(12,2),
INVOICE_DATE DATE,
REMARKS VARCHAR(255),
CREATED_BY VARCHAR(100),
CREATED_AT TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP,
UPDATED_BY VARCHAR(100),
UPDATED_AT TIMESTAMP
);