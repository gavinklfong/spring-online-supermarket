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
CREATE TABLE IF NOT EXISTS supermarket.customers (
id uuid,
name text,
telephone text,
email text,
default_address_key text,
PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS supermarket.customer_addresses (
customer_id uuid,
address_key text,
address frozen<address_type>,
PRIMARY KEY (customer_id, address_key)
);

-- Create shopping basket table
CREATE TABLE IF NOT EXISTS supermarket.shopping_basket_by_customer (
customer_id uuid,
products frozen<map<uuid,int>>,
PRIMARY KEY (customer_id)
);

-- Create delivery timeslot
CREATE TABLE IF NOT EXISTS supermarket.delivery_timeslots (
  delivery_date date,
  start_time time,
  delivery_team_id uuid,
  confirmed boolean,
  reserved_by_customer_id uuid,
  reservation_expiry timestamp,
  PRIMARY KEY (delivery_date, start_time, delivery_team_id)
);

-- Create orders tables
CREATE TABLE IF NOT EXISTS supermarket.orders_by_customer (
  customer_id uuid,
  order_id uuid,
  submission_time timestamp,
  products map<uuid, int>,
  status text,
  delivery_address_key text,
  currency text,
  item_subtotal double,
  postage_packing_fee double,
  vat double,
  promotional_discount double,
  remarks text,
  PRIMARY KEY (customer_id, order_id, submission_time)
)
WITH CLUSTERING ORDER BY (order_id ASC, submission_time DESC);

CREATE TABLE IF NOT EXISTS supermarket.orders_by_delivery_date (
  delivery_date date,
  delivery_team_id uuid,
  start_time time,
  order_id uuid,
  customer_id uuid,
  delivery_address_key text,
  status text,
  products map<uuid, int>,
  remarks text,
  PRIMARY KEY ((delivery_date, delivery_team_id), start_time)
)
WITH CLUSTERING ORDER BY (start_time ASC);

-- Insert sample customers
INSERT INTO supermarket.customers (id, name, telephone, email, default_address_key)
VALUES (28a76352-0102-45ae-81c7-aeb25cc228f9, 'John', '012345678912345', 'john@gmail.com', 'HOME');
INSERT INTO supermarket.customer_addresses (customer_id, address_key, address)
VALUES (28a76352-0102-45ae-81c7-aeb25cc228f9, 'HOME', {street_address: '2284 Lenna Lodge', city: 'Longmont', country: 'Belgium', zipcode: '05874'});
INSERT INTO supermarket.customer_addresses (customer_id, address_key, address)
VALUES (28a76352-0102-45ae-81c7-aeb25cc228f9, 'OFFICE', {street_address: '700 Hintz Causeway', city: 'Escondido', country: 'Belgium', zipcode: '70540'});

INSERT INTO supermarket.customers (id, name, telephone, email, default_address_key)
VALUES (7febc928-a5d0-40d5-ad71-ef7ebe2f2fe3, 'Peter', '012345678912345', 'peter@gmail.com','HOME');
INSERT INTO supermarket.customer_addresses (customer_id, address_key, address)
VALUES (7febc928-a5d0-40d5-ad71-ef7ebe2f2fe3, 'HOME', {street_address: '1059 Ardella Center', city: 'Dundalk', country: 'Dominican Republic', zipcode: '47368'});

-- Insert sample basket record
INSERT INTO supermarket.shopping_basket_by_customer (customer_id, products)
VALUES (7febc928-a5d0-40d5-ad71-ef7ebe2f2fe3, {54641f90-e6a0-49f9-8d32-eb526e914779: 2, 8586dfc3-a049-41a9-a6f9-9cbbdf9e185a: 1});

-- Insert sample delivery timeslot
INSERT INTO supermarket.delivery_timeslots (delivery_date, start_time, delivery_team_id, reserved_by_customer_id, reservation_expiry, confirmed)
VALUES ('2022-02-15', '09:00:00', e0681835-b273-4455-b42a-41687cecfb60, 28a76352-0102-45ae-81c7-aeb25cc228f9, '2022-02-05T15:00:00.000+0000', true);
INSERT INTO supermarket.delivery_timeslots (delivery_date, start_time, delivery_team_id, reserved_by_customer_id, reservation_expiry, confirmed)
VALUES ('2022-02-15', '09:00:00', f53a21cb-f056-499b-a6b2-3832232fa6e6, 00000000-0000-0000-0000-000000000000, '1970-01-01T00:00:00.001+0000', false);
INSERT INTO supermarket.delivery_timeslots (delivery_date, start_time, delivery_team_id, reserved_by_customer_id, reservation_expiry, confirmed)
VALUES ('2022-02-15', '12:00:00', e0681835-b273-4455-b42a-41687cecfb60, 00000000-0000-0000-0000-000000000000, '1970-01-01T00:00:00.001+0000', false);
INSERT INTO supermarket.delivery_timeslots (delivery_date, start_time, delivery_team_id, reserved_by_customer_id, reservation_expiry, confirmed)
VALUES ('2022-02-15', '12:00:00', f53a21cb-f056-499b-a6b2-3832232fa6e6, 00000000-0000-0000-0000-000000000000, '1970-01-01T00:00:00.001+0000', false);
INSERT INTO supermarket.delivery_timeslots (delivery_date, start_time, delivery_team_id, reserved_by_customer_id, reservation_expiry, confirmed)
VALUES ('2022-02-16', '12:00:00', e0681835-b273-4455-b42a-41687cecfb60, 00000000-0000-0000-0000-000000000000, '1970-01-01T00:00:00.001+0000', false);
INSERT INTO supermarket.delivery_timeslots (delivery_date, start_time, delivery_team_id, reserved_by_customer_id, reservation_expiry, confirmed)
VALUES ('2022-02-16', '12:00:00', f53a21cb-f056-499b-a6b2-3832232fa6e6, 00000000-0000-0000-0000-000000000000, '1970-01-01T00:00:00.001+0000', false);
INSERT INTO supermarket.delivery_timeslots (delivery_date, start_time, delivery_team_id, reserved_by_customer_id, reservation_expiry, confirmed)
VALUES ('2022-02-16', '16:00:00', e0681835-b273-4455-b42a-41687cecfb60, 00000000-0000-0000-0000-000000000000, '1970-01-01T00:00:00.001+0000', false);
INSERT INTO supermarket.delivery_timeslots (delivery_date, start_time, delivery_team_id, reserved_by_customer_id, reservation_expiry, confirmed)
VALUES ('2022-02-16', '16:00:00', f53a21cb-f056-499b-a6b2-3832232fa6e6, 00000000-0000-0000-0000-000000000000, '1970-01-01T00:00:00.001+0000', false);

-- Insert sample order records
INSERT INTO supermarket.orders_by_customer
(customer_id, order_id, submission_time, products, status, delivery_address_key,
currency, item_subtotal, postage_packing_fee, vat, promotional_discount, remarks)
VALUES
(28a76352-0102-45ae-81c7-aeb25cc228f9, e877568a-2ce6-480e-ab31-9138f3d3b23c, '2022-02-05T14:30:55.000+0000',
{54641f90-e6a0-49f9-8d32-eb526e914779: 2, 8586dfc3-a049-41a9-a6f9-9cbbdf9e185a: 1},
'SUBMITTED', 'HOME', 'GBP', 132, 3, 20, 10, 'sample order remarks');

INSERT INTO supermarket.orders_by_delivery_date
(delivery_date, delivery_team_id, start_time, order_id, customer_id,
delivery_address_key, status, products, remarks)
VALUES
('2022-02-15', e0681835-b273-4455-b42a-41687cecfb60, '09:00:00',
e877568a-2ce6-480e-ab31-9138f3d3b23c,
28a76352-0102-45ae-81c7-aeb25cc228f9, 'HOME', 'SUBMITTED',
{54641f90-e6a0-49f9-8d32-eb526e914779: 2, 8586dfc3-a049-41a9-a6f9-9cbbdf9e185a: 1},
'sample order remarks');