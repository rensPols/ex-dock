CREATE OR REPLACE FUNCTION check_root_url() RETURNS trigger
	LANGUAGE plpgsql
	AS $$BEGIN
	IF (SELECT COUNT(*) FROM url_keys WHERE (url_key = '/' AND upper_key != '/') OR (url_key != '/' AND upper_key='/')) > 0
	THEN RAISE EXCEPTION 'The root is only allowed to be combined with the root!';
	END IF;
	RETURN NEW;
END;$$;

CREATE TRIGGER after_insert_url_keys
AFTER INSERT ON url_keys
FOR EACH ROW
EXECUTE FUNCTION check_root_url();

INSERT INTO url_keys (url_key, upper_key, page_type) VALUES ('/', '/', 'product'::p_type);

INSERT INTO text_pages (name, short_text, text) VALUES ('Test', 'short Test', 'text Test');

SELECT * FROM text_pages;

INSERT INTO text_page_urls (url_key, upper_key, text_pages_id) VALUES ('/', '/', 1);

INSERT INTO categories (upper_category, name, short_description, description) 
VALUES (NULL, 'test category', 'a test category', 'a category for testing purposes');

SELECT * FROM categories;

INSERT INTO category_urls (url_key, upper_key, category_id) VALUES ('/', '/', 2);

INSERT INTO products (name, short_name, description, short_description)
VALUES ('test product', 'tp', 'a product for testing purposes', 'a test product');

SELECT * FROM products;

INSERT INTO product_urls (url_key, upper_key, product_id)
VALUES ('/', '/', 1);

SELECT * FROM url_keys uk
INNER JOIN text_page_urls tpu ON uk.url_key = tpu.url_key AND uk.upper_key = tpu.upper_key
INNER JOIN text_pages tp ON tp.text_pages_id = tpu.text_pages_id
INNER JOIN category_urls cu ON uk.url_key = cu.url_key AND uk.upper_key = cu.upper_key
INNER JOIN categories c ON c.category_id = cu.category_id
INNER JOIN product_urls pu ON uk.url_key = pu.url_key AND uk.upper_key = pu.url_key
INNER JOIN products p ON p.product_id = pu.product_id;