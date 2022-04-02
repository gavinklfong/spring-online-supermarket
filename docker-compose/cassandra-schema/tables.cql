-- Create a keyspace
CREATE KEYSPACE IF NOT EXISTS supermarket WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : '1' };

-- Create customer table
CREATE TABLE IF NOT EXISTS supermarket.customer (
customer_id uuid,
name text,
telephone text,
email text,
addresses map<text,text>,
default_address_key text,
PRIMARY KEY (customer_id)
);

-- Insert sample customers
INSERT INTO supermarket.customer (customer_id, name, telephone, email, addresses, default_address_key)
VALUES (7febc928-a5d0-40d5-ad71-ef7ebe2f2fe3, 'Peter Chan', '012345678912345', 'peter@gmail.com', {'home': 'address 1'}, 'home');

INSERT INTO supermarket.customer (customer_id, name, telephone, email, addresses, default_address_key)
VALUES (28a76352-0102-45ae-81c7-aeb25cc228f9, 'John Lau', '012345678912345', 'john@gmail.com', {'home': 'address 1', 'office': 'address 2'}, 'home');
