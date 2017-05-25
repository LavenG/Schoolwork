/* Student Name: Nissan Azizov */
/* N number: 13870969 */

/* Clean up any previous homework answers */
DROP TABLE IF EXISTS ANSWER01;
DROP TABLE IF EXISTS ANSWER02;
DROP TABLE IF EXISTS ANSWER03;
DROP TABLE IF EXISTS ANSWER04;
DROP TABLE IF EXISTS ANSWER05;

/* Clean up any previous version */
DROP TABLE IF EXISTS PRODUCTUSAGE;
DROP TABLE IF EXISTS CONTAINS;
DROP TABLE IF EXISTS ITEM;
DROP TABLE IF EXISTS ORDERS;
DROP TABLE IF EXISTS STORED;
DROP TABLE IF EXISTS WAREHOUSE;
DROP TABLE IF EXISTS PRODUCT;
DROP TABLE IF EXISTS CUSTOMER;
DROP TABLE IF EXISTS VENDOR;
DROP TABLE IF EXISTS SHIPPER;
DROP TABLE IF EXISTS COMPANY;

/* Define tables */
CREATE TABLE COMPANY(
	EIN CHAR(16) PRIMARY KEY,
	NAME CHAR(24) NOT NULL
);

CREATE TABLE SHIPPER(
	EIN CHAR(16) PRIMARY KEY,
	PRICEPERLB FLOAT,
	FOREIGN KEY(EIN) REFERENCES COMPANY(EIN) ON DELETE CASCADE
);

CREATE TABLE CUSTOMER(
	EIN CHAR(16) PRIMARY KEY,
	CREDIT FLOAT,
	FOREIGN KEY(EIN) REFERENCES COMPANY(EIN) ON DELETE CASCADE
);

CREATE TABLE VENDOR(
	EIN CHAR(16) PRIMARY KEY,
	FOREIGN KEY(EIN) REFERENCES COMPANY(EIN) ON DELETE CASCADE
);

CREATE TABLE WAREHOUSE(
	SHIPPEREIN CHAR(16),
	WAREHOUSENAME CHAR(24),
	STATE CHAR(16),
	CITY CHAR(16),
	PRIMARY KEY(SHIPPEREIN, WAREHOUSENAME),
	FOREIGN KEY(SHIPPEREIN) REFERENCES SHIPPER(EIN) ON DELETE CASCADE
);

CREATE TABLE PRODUCT(
	PRODUCTNAME CHAR(24),
	PRODUCTID CHAR(16),
	PRICE FLOAT NOT NULL,
	WEIGHTINLB FLOAT NOT NULL,
	EIN CHAR(16),
	PRIMARY KEY(PRODUCTNAME, PRODUCTID),
	FOREIGN KEY(EIN) REFERENCES VENDOR(EIN) ON DELETE CASCADE
);

CREATE TABLE STORED(
	SHIPPEREIN CHAR(16),
	WAREHOUSENAME CHAR(24),
	PRODUCTNAME CHAR(24),
	PRODUCTID CHAR(16),
	QUANTITY INT,
	PRIMARY KEY(SHIPPEREIN, WAREHOUSENAME, PRODUCTNAME, PRODUCTID),
	FOREIGN KEY(SHIPPEREIN, WAREHOUSENAME) REFERENCES WAREHOUSE(SHIPPEREIN, WAREHOUSENAME) ON DELETE CASCADE,
	FOREIGN KEY(PRODUCTNAME, PRODUCTID) REFERENCES PRODUCT(PRODUCTNAME, PRODUCTID) ON DELETE CASCADE
);

CREATE TABLE ORDERS(
	CUSTOMEREIN CHAR(16),
	SHIPPEREIN CHAR(16),
	WAREHOUSENAME CHAR(24),
	PRODUCTNAME CHAR(24),
	PRODUCTID CHAR(16),
	QUANTITY INT,
	PRIMARY KEY(CUSTOMEREIN, SHIPPEREIN, WAREHOUSENAME, PRODUCTNAME, PRODUCTID),
	FOREIGN KEY(CUSTOMEREIN) REFERENCES CUSTOMER(EIN) ON DELETE CASCADE,
	FOREIGN KEY(SHIPPEREIN, WAREHOUSENAME, PRODUCTNAME, PRODUCTID) REFERENCES STORED(SHIPPEREIN, WAREHOUSENAME, PRODUCTNAME, PRODUCTID) ON DELETE CASCADE
);

CREATE TABLE PRODUCTUSAGE(
	PRODUCTUSAGEVALUE FLOAT,
	PRODUCTNAME CHAR(24),
	PRODUCTID CHAR(16),
	PRIMARY KEY(PRODUCTUSAGEVALUE, PRODUCTNAME, PRODUCTID),
	FOREIGN KEY(PRODUCTNAME, PRODUCTID) REFERENCES PRODUCT(PRODUCTNAME, PRODUCTID) ON DELETE CASCADE
);


CREATE TABLE ITEM(
	ITEMID CHAR(16) PRIMARY KEY,
	ITEMSIZE INT,
	SECONDARY CHAR(16),
	FOREIGN KEY(SECONDARY) REFERENCES ITEM(ITEMID) ON DELETE CASCADE
);

CREATE TABLE CONTAINS(
	PRODUCTNAME CHAR(24),
	PRODUCTID CHAR(16),
	ITEMID CHAR(16),
	PRIMARY KEY(PRODUCTNAME, PRODUCTID, ITEMID),
	FOREIGN KEY(PRODUCTNAME, PRODUCTID) REFERENCES PRODUCT(PRODUCTNAME, PRODUCTID) ON DELETE CASCADE,
	FOREIGN KEY(ITEMID) REFERENCES ITEM(ITEMID) ON DELETE CASCADE
);

/* Generate data */
INSERT INTO COMPANY VALUES('078-05-1120', 'IBM');
INSERT INTO COMPANY VALUES('917-34-6302', 'DELL');
INSERT INTO COMPANY VALUES('078-05-1123', 'HP');
INSERT INTO COMPANY VALUES('078-05-1130', 'APPLE');
INSERT INTO COMPANY VALUES('077-02-1330', 'GOOGLE');
INSERT INTO COMPANY VALUES('079-04-1120', 'ASUS');
INSERT INTO COMPANY VALUES('378-35-1108', 'LENOVO');
INSERT INTO COMPANY VALUES('278-05-1120', 'SAMSUNG');
INSERT INTO COMPANY VALUES('348-75-1450', 'ACER');
INSERT INTO COMPANY VALUES('256-90-4576', 'SONY');
INSERT INTO COMPANY VALUES('025-45-1111', 'TOSHIBA');
INSERT INTO COMPANY VALUES('025-59-1919', 'INTEL');
INSERT INTO COMPANY VALUES('567-45-2351', 'AMD');
INSERT INTO COMPANY VALUES('100-40-0011', 'QUAlCOMM');
INSERT INTO COMPANY VALUES('100-40-0012', 'AMAZON');
INSERT INTO COMPANY VALUES('111-21-3379', 'DHL');
INSERT INTO COMPANY VALUES('113-21-3856', 'USPS');
INSERT INTO COMPANY VALUES('108-34-9999', 'NEX EXPRESS');
INSERT INTO COMPANY VALUES('108-64-2222', 'AMEX');
INSERT INTO COMPANY VALUES('256-42-7755', 'DHL');
INSERT INTO COMPANY VALUES('798-99-0537', 'FLATRATE');
INSERT INTO COMPANY VALUES('776-87-0587', 'MTS LOGISTICS');
INSERT INTO COMPANY VALUES('234-90-9037', 'K INTERATIONAL TRANSPORT');
INSERT INTO COMPANY VALUES('874-87-9367', 'NY SHIPPING');
INSERT INTO COMPANY VALUES('546-23-0888', 'HIGHMARK INC.');
INSERT INTO COMPANY VALUES('098-29-9898', 'BT GROUP PLC');
INSERT INTO COMPANY VALUES('129-03-7643', 'APLICOR INC.');
INSERT INTO COMPANY VALUES('194-74-7823', 'MSFT');
INSERT INTO COMPANY VALUES('983-44-7897', 'VERIZON');
INSERT INTO COMPANY VALUES('397-89-7633', 'SUN');
INSERT INTO COMPANY VALUES('993-44-7897', 'NASA');
INSERT INTO COMPANY VALUES('997-89-7633', 'SALESFORCE');
INSERT INTO COMPANY VALUES('999-88-3312', 'CITI');
INSERT INTO COMPANY VALUES('999-98-5698', 'BOA');

INSERT INTO SHIPPER VALUES('111-21-3379', 1.89);
INSERT INTO SHIPPER VALUES('113-21-3856', 1.12);
INSERT INTO SHIPPER VALUES('108-34-9999', 0.98);
INSERT INTO SHIPPER VALUES('256-42-7755', 1.79);
INSERT INTO SHIPPER VALUES('798-99-0537', 2.13);
INSERT INTO SHIPPER VALUES('776-87-0587', 1.66);
INSERT INTO SHIPPER VALUES('234-90-9037', 1.78);
INSERT INTO SHIPPER VALUES('874-87-9367', 1.93);

INSERT INTO CUSTOMER VALUES('078-05-1120', 6.72);
INSERT INTO CUSTOMER VALUES('917-34-6302', 4.33);
INSERT INTO CUSTOMER VALUES('078-05-1123', 5.12);
INSERT INTO CUSTOMER VALUES('078-05-1130', null);
INSERT INTO CUSTOMER VALUES('079-04-1120', 6.23);
INSERT INTO CUSTOMER VALUES('378-35-1108', 7.24);
INSERT INTO CUSTOMER VALUES('278-05-1120', 3.12);
INSERT INTO CUSTOMER VALUES('348-75-1450', 8.11);
INSERT INTO CUSTOMER VALUES('256-90-4576', null);
INSERT INTO CUSTOMER VALUES('025-45-1111', 6.16);
INSERT INTO CUSTOMER VALUES('025-59-1919', 5.73);
INSERT INTO CUSTOMER VALUES('567-45-2351', null);
INSERT INTO CUSTOMER VALUES('100-40-0011', 7.13);

INSERT INTO VENDOR VALUES('100-40-0012');
INSERT INTO VENDOR VALUES('546-23-0888');
INSERT INTO VENDOR VALUES('098-29-9898');
INSERT INTO VENDOR VALUES('129-03-7643');
INSERT INTO VENDOR VALUES('194-74-7823');
INSERT INTO VENDOR VALUES('983-44-7897');
INSERT INTO VENDOR VALUES('397-89-7633');
INSERT INTO VENDOR VALUES('993-44-7897');
INSERT INTO VENDOR VALUES('997-89-7633');

INSERT INTO WAREHOUSE VALUES('111-21-3379', 'Americold Logistics', 'GE', 'Atlanta');
INSERT INTO WAREHOUSE VALUES('113-21-3856', 'Lineage Logistics', 'CA', 'Colton');
INSERT INTO WAREHOUSE VALUES('108-34-9999', 'Millard', 'Nebraska', 'Omaba');
INSERT INTO WAREHOUSE VALUES('798-99-0537', 'Freezer', 'NJ', 'Chatham');
INSERT INTO WAREHOUSE VALUES('776-87-0587', 'VersaCold', 'Indiana', 'Fort Wayne');
INSERT INTO WAREHOUSE VALUES('234-90-9037', 'Interstate Warehousing', null, 'Vancouver');
INSERT INTO WAREHOUSE VALUES('776-87-0587', 'Burnis Logistics', 'Deleware', 'Milford');
INSERT INTO WAREHOUSE VALUES('234-90-9037', 'US Cold Storage, Inc.', 'NJ', 'Voorhees');
INSERT INTO WAREHOUSE VALUES('234-90-9037', 'Cloverleaf, Inc.', 'IOWA', 'Sioux City');
INSERT INTO WAREHOUSE VALUES('798-99-0537', 'Nordic Logistic, LLC', 'GE', 'ATL');
INSERT INTO WAREHOUSE VALUES('798-99-0537', 'Columbia Colstor, Inc', 'WA', 'Moses Lake');
INSERT INTO WAREHOUSE VALUES('113-21-3856', 'Congebec Logistics, Inc', null, 'Quebec City');
INSERT INTO WAREHOUSE VALUES('113-21-3856', 'Frialsa Frigorificos De', 'Mexico', 'Tialnepantla Edo');
INSERT INTO WAREHOUSE VALUES('234-90-9037', 'Hanson Logistics.', 'NJ', 'Jersey City');

INSERT INTO PRODUCT VALUES('Books', '9527-24-8096', 9.9, 1.8, '100-40-0012');
INSERT INTO PRODUCT VALUES('Kindle', '1321-11-8899', 49, 0.8, '100-40-0012');
INSERT INTO PRODUCT VALUES('PC machine', '2527-24-8016', 199, 19.2, '100-40-0012');
INSERT INTO PRODUCT VALUES('Fire phone', '2333-44-9989', 99, 1.2, '100-40-0012');
INSERT INTO PRODUCT VALUES('Nut', '1537-24-8221', 18.9, 1.8, null);
INSERT INTO PRODUCT VALUES('Laptop', '2618-99-0505', 399, 6.6, '098-29-9898');
INSERT INTO PRODUCT VALUES('mobile phone', '2348-09-0445', 99, 0.8, '098-29-9898');
INSERT INTO PRODUCT VALUES('Car', '7756-01-3465', 10999, 2999, null);
INSERT INTO PRODUCT VALUES('Glasses', '1731-01-2425', 1418, 0.3, '129-03-7643');
INSERT INTO PRODUCT VALUES('XBOX', '2651-01-2126', 199, 2.01, '194-74-7823');
INSERT INTO PRODUCT VALUES('Routers', '3631-13-2351', 86, 1.00, '983-44-7897');
INSERT INTO PRODUCT VALUES('SERVERS', '9135-54-9238', 982, 30, '397-89-7633');
INSERT INTO PRODUCT VALUES('Storage Device', '9156-12-9238', 1480, 30, '397-89-7633');
INSERT INTO PRODUCT VALUES('GPS', '3235-65-7764', 500, 0.3, '993-44-7897');
INSERT INTO PRODUCT VALUES('CRM', '2398-89-1234', 1999, 0, '997-89-7633');
INSERT INTO PRODUCT VALUES('ERP', '2377-19-6534', 999, 0, '997-89-7633');
INSERT INTO PRODUCT VALUES('Bicycle', '0015-21-3921', 101, 98, null);
INSERT INTO PRODUCT VALUES('Music CDs', '1016-51-3919', 29, 0.5, null);

INSERT INTO STORED VALUES('111-21-3379', 'Americold Logistics', 'Kindle', '1321-11-8899', 200);
INSERT INTO STORED VALUES('111-21-3379', 'Americold Logistics', 'Books', '9527-24-8096', 1000);
INSERT INTO STORED VALUES('798-99-0537', 'Columbia Colstor, Inc', 'Fire phone', '2333-44-9989', 600);
INSERT INTO STORED VALUES('798-99-0537', 'Freezer', 'Laptop', '2618-99-0505', 1200);
INSERT INTO STORED VALUES('776-87-0587', 'VersaCold', 'mobile phone', '2348-09-0445', 1600);
INSERT INTO STORED VALUES('776-87-0587', 'VersaCold', 'Glasses', '1731-01-2425', 200);
INSERT INTO STORED VALUES('776-87-0587', 'VersaCold', 'Nut', '1537-24-8221', 6000);
INSERT INTO STORED VALUES('776-87-0587', 'Burnis Logistics', 'mobile phone', '2348-09-0445', 600);
INSERT INTO STORED VALUES('234-90-9037', 'US Cold Storage, Inc.', 'Nut', '1537-24-8221', 110000);
INSERT INTO STORED VALUES('234-90-9037', 'Cloverleaf, Inc.', 'Routers', '3631-13-2351', 2400);
INSERT INTO STORED VALUES('798-99-0537', 'Nordic Logistic, LLC', 'Storage Device', '9156-12-9238', 80);
INSERT INTO STORED VALUES('798-99-0537', 'Columbia Colstor, Inc', 'Nut', '1537-24-8221', 6000);
INSERT INTO STORED VALUES('234-90-9037', 'US Cold Storage, Inc.', 'mobile phone', '2348-09-0445', 1000);
INSERT INTO STORED VALUES('113-21-3856', 'Frialsa Frigorificos De', 'GPS', '3235-65-7764', 220);
INSERT INTO STORED VALUES('113-21-3856', 'Congebec Logistics, Inc', 'Bicycle', '0015-21-3921', 460);
INSERT INTO STORED VALUES('113-21-3856', 'Frialsa Frigorificos De', 'Music CDs', '1016-51-3919', 8700);
INSERT INTO STORED VALUES('234-90-9037', 'Cloverleaf, Inc.', 'Glasses', '1731-01-2425', 400);
INSERT INTO STORED VALUES('798-99-0537', 'Freezer', 'Books', '9527-24-8096', 2100);
INSERT INTO STORED VALUES('111-21-3379', 'Americold Logistics', 'Nut', '1537-24-8221', 20400);

INSERT INTO ORDERS VALUES('078-05-1120', '111-21-3379', 'Americold Logistics', 'Books', '9527-24-8096', 80);
INSERT INTO ORDERS VALUES('078-05-1120', '798-99-0537', 'Freezer', 'Laptop', '2618-99-0505',  130);
INSERT INTO ORDERS VALUES('078-05-1123', '798-99-0537', 'Freezer', 'Laptop', '2618-99-0505',  400);
INSERT INTO ORDERS VALUES('378-35-1108', '776-87-0587', 'VersaCold', 'mobile phone', '2348-09-0445', 720);
INSERT INTO ORDERS VALUES('256-90-4576', '234-90-9037', 'Cloverleaf, Inc.', 'Routers', '3631-13-2351', 140);
INSERT INTO ORDERS VALUES('378-35-1108', '798-99-0537', 'Freezer', 'Books', '9527-24-8096', 180);
INSERT INTO ORDERS VALUES('378-35-1108', '798-99-0537', 'Freezer', 'Laptop', '2618-99-0505', 40);
INSERT INTO ORDERS VALUES('025-59-1919', '776-87-0587', 'VersaCold', 'mobile phone', '2348-09-0445', 270);
INSERT INTO ORDERS VALUES('025-59-1919', '113-21-3856', 'Frialsa Frigorificos De', 'GPS', '3235-65-7764', 210);
INSERT INTO ORDERS VALUES('567-45-2351', '111-21-3379', 'Americold Logistics', 'Kindle', '1321-11-8899', 198);
INSERT INTO ORDERS VALUES('567-45-2351', '113-21-3856', 'Frialsa Frigorificos De', 'Music CDs', '1016-51-3919', 2800);
INSERT INTO ORDERS VALUES('348-75-1450', '113-21-3856', 'Congebec Logistics, Inc', 'Bicycle', '0015-21-3921', 360);

INSERT INTO PRODUCTUSAGE VALUES(77.6, 'PC machine', '2527-24-8016');
INSERT INTO PRODUCTUSAGE VALUES(37.6, 'PC machine', '2527-24-8016');
INSERT INTO PRODUCTUSAGE VALUES(97.5, 'Fire phone', '2333-44-9989');
INSERT INTO PRODUCTUSAGE VALUES(32, 'Books', '9527-24-8096');
INSERT INTO PRODUCTUSAGE VALUES(21, 'Books', '9527-24-8096');
INSERT INTO PRODUCTUSAGE VALUES(52, 'Books', '9527-24-8096');
INSERT INTO PRODUCTUSAGE VALUES(92, 'mobile phone', '2348-09-0445');
INSERT INTO PRODUCTUSAGE VALUES(97, 'mobile phone', '2348-09-0445');
INSERT INTO PRODUCTUSAGE VALUES(66, 'Storage Device', '9156-12-9238');
INSERT INTO PRODUCTUSAGE VALUES(89, 'GPS', '3235-65-7764');
INSERT INTO PRODUCTUSAGE VALUES(81.1, 'Bicycle', '0015-21-3921');
INSERT INTO PRODUCTUSAGE VALUES(88.4, 'Music CDs', '1016-51-3919');
INSERT INTO PRODUCTUSAGE VALUES(74.5, 'Music CDs', '1016-51-3919');

INSERT INTO ITEM VALUES('0003', 2, null);
INSERT INTO ITEM VALUES('0004', 7, null);
INSERT INTO ITEM VALUES('0009', 11, null);
INSERT INTO ITEM VALUES('0019', 31, null);
INSERT INTO ITEM VALUES('0021', 23, null);
INSERT INTO ITEM VALUES('0016', 9, null);
INSERT INTO ITEM VALUES('0048', 87, null);
INSERT INTO ITEM VALUES('0024', 34, null);
INSERT INTO ITEM VALUES('0026', 37, null);
INSERT INTO ITEM VALUES('0001', 6, '0003');
INSERT INTO ITEM VALUES('0002', 8, '0004');
INSERT INTO ITEM VALUES('0007', 12, '0009');
INSERT INTO ITEM VALUES('0017', 22, '0021');
INSERT INTO ITEM VALUES('0018', 32, '0019');
INSERT INTO ITEM VALUES('0058', 30, '0019');
INSERT INTO ITEM VALUES('0038', 12, '0009');

INSERT INTO CONTAINS VALUES('Books', '9527-24-8096','0001');
INSERT INTO CONTAINS VALUES('Books', '9527-24-8096','0002');
INSERT INTO CONTAINS VALUES('Books', '9527-24-8096','0009');
INSERT INTO CONTAINS VALUES('Laptop', '2618-99-0505','0017');
INSERT INTO CONTAINS VALUES('Laptop', '2618-99-0505', '0021');
INSERT INTO CONTAINS VALUES('Fire phone', '2333-44-9989','0018');
INSERT INTO CONTAINS VALUES('Fire phone', '2333-44-9989','0019');
INSERT INTO CONTAINS VALUES('mobile phone', '2348-09-0445','0019');
INSERT INTO CONTAINS VALUES('mobile phone', '2348-09-0445','0018');
INSERT INTO CONTAINS VALUES('XBOX', '2651-01-2126','0016');
INSERT INTO CONTAINS VALUES('Storage Device', '9156-12-9238','0058');
INSERT INTO CONTAINS VALUES('Car', '7756-01-3465','0048');
INSERT INTO CONTAINS VALUES('Bicycle', '0015-21-3921','0009');
INSERT INTO CONTAINS VALUES('Books', '9527-24-8096','0024');
INSERT INTO CONTAINS VALUES('Bicycle', '0015-21-3921','0026');

/*********************************
 INSERT YOUR SOLUTIONS
*********************************/

/* 1.Produce table Answer01 (WAREHOUSENAME, PRODUCTNAME, TOTAL_QUANTITY) from Table ORDER, which produces possible aggregations based on subsets of {WAREHOUSENAME,PRODUCTNAME} using ROLLUP*/

/* (b) CREATE TABLE ANSWER01 as */
CREATE TABLE ANSWER01
AS
	SELECT WAREHOUSENAME, PRODUCTNAME,
				 SUM(QUANTITY) AS TOTAL_QUANTITY
	FROM ORDERS
	GROUP BY WAREHOUSENAME, PRODUCTNAME
	WITH ROLLUP;

/* 2.Produce table Answer02 (WAREHOUSENAME, PRODUCTNAME, TOTAL_QUANTITY) from Table ORDER, which produces possible aggregations based on subsets of {PRODUCTNAME, WAREHOUSENAME} using ROLLUP*/

/* (c) CREATE TABLE ANSWER02 as */
CREATE TABLE ANSWER02
AS
	SELECT WAREHOUSENAME, PRODUCTNAME,
				 sum(QUANTITY) AS TOTAL_QUANTITY
	FROM ORDERS
	GROUP BY PRODUCTNAME, WAREHOUSENAME
	WITH ROLLUP;

/* 3.Produce table Answer03 (PRODUCTUSAGEVALUE, PRODUCTNAME, PRODUCTID) after inserting a ProductUsage record : ProductUsageValue = '90', ProductName = 'XBOX' and ProductID = '2651-01-2126'*/

/* (d) CREATE TABLE ANSWER03 as */
INSERT INTO PRODUCTUSAGE
VALUES(90, 'XBOX', '2651-01-2126');

CREATE TABLE ANSWER03
AS
	SELECT *
	FROM PRODUCTUSAGE
	ORDER BY PRODUCTUSAGEVALUE ASC;

/* 4.Produce table Answer04(PRODUCTNAME, PRODUCTID, PRICE, WEIGHTINLB, EIN) after deleting a Product record : ProductName = 'Books' and ProductID = '9527-24-8096' */

/* (e) CREATE TABLE ANSWER04 as */
DELETE FROM PRODUCT
WHERE PRODUCTNAME = 'Books';

CREATE TABLE ANSWER04
AS
	SELECT *
	FROM PRODUCT
	ORDER BY PRODUCTNAME ASC;

/* 5.Produce table Answer05(ITEMID, ITEMSIZE, SECONDARY) after updating an Item record : change ItemSize from 9 to 19 where ItemID is 0016 */

/* (f) CREATE TABLE ANSWER05 as */
UPDATE ITEM
SET ITEMSIZE = 19
WHERE ITEMID = 0016;

CREATE TABLE ANSWER05
AS
	SELECT *
	FROM ITEM
	ORDER BY ITEMID ASC;

SELECT * FROM ANSWER01;
SELECT * FROM ANSWER02;
SELECT * FROM ANSWER03;
SELECT * FROM ANSWER04;
SELECT * FROM ANSWER05;
