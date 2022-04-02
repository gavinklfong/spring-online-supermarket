-- Create a keyspace
CREATE KEYSPACE IF NOT EXISTS supermarket WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : '1' };

-- Create customer table
CREATE TABLE IF NOT EXISTS supermarket.customer (
customer_id uuid,
name text,
telephone text,
email text,
addresses frozen<map<text,text>>,
default_address_key text,
PRIMARY KEY (customer_id)
);

-- Create shopping basket table
CREATE TABLE IF NOT EXISTS supermarket.shopping_basket (
customer_id uuid,
products frozen<map<uuid,int>>,
PRIMARY KEY (customer_id)
);


-- Insert sample customers
INSERT INTO supermarket.customer (customer_id, name, telephone, email, addresses, default_address_key)
VALUES (7febc928-a5d0-40d5-ad71-ef7ebe2f2fe3, 'Peter', '012345678912345', 'peter@gmail.com', {'home': 'address 1'}, 'home');

INSERT INTO supermarket.customer (customer_id, name, telephone, email, addresses, default_address_key)
VALUES (28a76352-0102-45ae-81c7-aeb25cc228f9, 'John', '012345678912345', 'john@gmail.com', {'home': 'address 1', 'office': 'address 2'}, 'home');

-- Insert sample basket record
INSERT INTO supermarket.shopping_basket (customer_id, products)
VALUES (7febc928-a5d0-40d5-ad71-ef7ebe2f2fe3, {54641f90-e6a0-49f9-8d32-eb526e914779: 2, 8586dfc3-a049-41a9-a6f9-9cbbdf9e185a: 1});
