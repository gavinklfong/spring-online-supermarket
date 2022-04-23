-- Create a keyspace
CREATE KEYSPACE IF NOT EXISTS supermarket WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : '1' };

-- Define Address user-defined type
CREATE TYPE supermarket.address_type (
street_address text,
city text,
country text,
zipcode text
);

-- Create customer table
CREATE TABLE IF NOT EXISTS supermarket.customer (
customer_id uuid,
name text,
telephone text,
email text,
default_address_key text,
PRIMARY KEY (customer_id)
);

CREATE TABLE IF NOT EXISTS supermarket.customer_address (
customer_id uuid,
address_key text,
address frozen<address_type>,
PRIMARY KEY (customer_id, address_key)
);

-- Create shopping basket table
CREATE TABLE IF NOT EXISTS supermarket.shopping_basket (
customer_id uuid,
products frozen<map<uuid,int>>,
PRIMARY KEY (customer_id)
);

-- Create delivery timeslot
CREATE TABLE IF NOT EXISTS supermarket.delivery_timeslot (
delivery_date date,
start_time time,
delivery_team_id int,
reserved_by_customer_id uuid,
reservation_expiry timestamp,
order_id uuid,
PRIMARY KEY (delivery_date, start_time, delivery_team_id)
);

-- Create orders
CREATE TABLE IF NOT EXISTS supermarket.order_by_customer (
customer_id uuid,
order_id uuid,
delivery_address_key text,
products frozen<map<uuid,int>>,
PRIMARY KEY (customer_id, order_id)
);

-- Insert sample customers
INSERT INTO supermarket.customer (customer_id, name, telephone, email, default_address_key)
VALUES (28a76352-0102-45ae-81c7-aeb25cc228f9, 'John', '012345678912345', 'john@gmail.com', 'HOME');
INSERT INTO supermarket.customer_address (customer_id, address_key, address)
VALUES (28a76352-0102-45ae-81c7-aeb25cc228f9, 'HOME', {street_address: '2284 Lenna Lodge', city: 'Longmont', country: 'Belgium', zipcode: '05874'});
INSERT INTO supermarket.customer_address (customer_id, address_key, address)
VALUES (28a76352-0102-45ae-81c7-aeb25cc228f9, 'OFFICE', {street_address: '700 Hintz Causeway', city: 'Escondido', country: 'Belgium', zipcode: '70540'});

INSERT INTO supermarket.customer (customer_id, name, telephone, email, default_address_key)
VALUES (7febc928-a5d0-40d5-ad71-ef7ebe2f2fe3, 'Peter', '012345678912345', 'peter@gmail.com','HOME');
INSERT INTO supermarket.customer_address (customer_id, address_key, address)
VALUES (7febc928-a5d0-40d5-ad71-ef7ebe2f2fe3, 'HOME', {street_address: '1059 Ardella Center', city: 'Dundalk', country: 'Dominican Republic', zipcode: '47368'});

-- Insert sample basket record
INSERT INTO supermarket.shopping_basket (customer_id, products)
VALUES (7febc928-a5d0-40d5-ad71-ef7ebe2f2fe3, {54641f90-e6a0-49f9-8d32-eb526e914779: 2, 8586dfc3-a049-41a9-a6f9-9cbbdf9e185a: 1});

-- Insert sample delivery timeslot
INSERT INTO supermarket.delivery_timeslot (delivery_date, start_time, delivery_team_id, reserved_by_customer_id, reservation_expiry, order_id)
VALUES ('2022-02-15', '09:00:00', 1, 00000000-0000-0000-0000-000000000000, '1970-01-01T00:00:00.001+0000', 00000000-0000-0000-0000-000000000000);
INSERT INTO supermarket.delivery_timeslot (delivery_date, start_time, delivery_team_id, reserved_by_customer_id, reservation_expiry, order_id)
VALUES ('2022-02-15', '09:00:00', 2, 00000000-0000-0000-0000-000000000000, '1970-01-01T00:00:00.001+0000', 00000000-0000-0000-0000-000000000000);
INSERT INTO supermarket.delivery_timeslot (delivery_date, start_time, delivery_team_id, reserved_by_customer_id, reservation_expiry, order_id)
VALUES ('2022-02-15', '12:00:00', 1, 00000000-0000-0000-0000-000000000000, '1970-01-01T00:00:00.001+0000', 00000000-0000-0000-0000-000000000000);
INSERT INTO supermarket.delivery_timeslot (delivery_date, start_time, delivery_team_id, reserved_by_customer_id, reservation_expiry, order_id)
VALUES ('2022-02-15', '12:00:00', 2, 00000000-0000-0000-0000-000000000000, '1970-01-01T00:00:00.001+0000', 00000000-0000-0000-0000-000000000000);
INSERT INTO supermarket.delivery_timeslot (delivery_date, start_time, delivery_team_id, reserved_by_customer_id, reservation_expiry, order_id)
VALUES ('2022-02-16', '12:00:00', 1, 00000000-0000-0000-0000-000000000000, '1970-01-01T00:00:00.001+0000', 00000000-0000-0000-0000-000000000000);
INSERT INTO supermarket.delivery_timeslot (delivery_date, start_time, delivery_team_id, reserved_by_customer_id, reservation_expiry, order_id)
VALUES ('2022-02-16', '12:00:00', 2, 00000000-0000-0000-0000-000000000000, '1970-01-01T00:00:00.001+0000', 00000000-0000-0000-0000-000000000000);
INSERT INTO supermarket.delivery_timeslot (delivery_date, start_time, delivery_team_id, reserved_by_customer_id, reservation_expiry, order_id)
VALUES ('2022-02-16', '16:00:00', 1, 00000000-0000-0000-0000-000000000000, '1970-01-01T00:00:00.001+0000', 00000000-0000-0000-0000-000000000000);
INSERT INTO supermarket.delivery_timeslot (delivery_date, start_time, delivery_team_id, reserved_by_customer_id, reservation_expiry, order_id)
VALUES ('2022-02-16', '16:00:00', 2, 28a76352-0102-45ae-81c7-aeb25cc228f9, '1970-01-01T00:00:00.001+0000', b2171636-d55e-4be9-86d4-6cf9ae01bc64);
INSERT INTO supermarket.delivery_timeslot (delivery_date, start_time, delivery_team_id, reserved_by_customer_id, reservation_expiry, order_id)
VALUES ('2022-02-20', '09:00:00', 1, 28a76352-0102-45ae-81c7-aeb25cc228f9, '1970-01-01T00:00:00.001+0000', 43ac87f9-5d1b-418a-9504-495fe03ea7ad);


INSERT INTO supermarket.order_by_customer (customer_id, order_id, delivery_address_key, products)
VALUES (28a76352-0102-45ae-81c7-aeb25cc228f9, b2171636-d55e-4be9-86d4-6cf9ae01bc64, 'HOME',
{19ea899d-a265-42a3-a2b9-10282716a2d3: 2, 38cc650a-406a-4090-9d90-25e0d3e555ce: 5});

INSERT INTO supermarket.order_by_customer (customer_id, order_id, delivery_address_key, products)
VALUES (28a76352-0102-45ae-81c7-aeb25cc228f9, 43ac87f9-5d1b-418a-9504-495fe03ea7ad, 'OFFICE',
{70f0c9d6-2401-43f8-a5cd-945e0eae2a7b: 10});

