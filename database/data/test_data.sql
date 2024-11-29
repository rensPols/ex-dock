INSERT INTO products (name, short_name, description, short_description)
  VALUES ('Test Product 1', 'TP1', 'Test description for Product 1',
          'Test desc P1');
INSERT INTO products (name, short_name, description, short_description)
  VALUES ('Test Product 2', 'TP2', 'Test description for Product 2',
          'Test desc P2');

INSERT INTO products_seo (product_id, meta_title, meta_description, meta_keywords, page_index)
  VALUES (1, 'Test Product 1', 'Test description for Product 1',
          'test keywords', 'index, follow'::p_index);
INSERT INTO products_seo (product_id, meta_title, meta_description, meta_keywords, page_index)
  VALUES (2, 'Test Product 2', 'Test description for Product 2',
          'test keywords', 'index, follow'::p_index);

INSERT INTO products_pricing (product_id, price, sale_price, cost_price)
  VALUES (1, 10.99, 8.99, 7.99);
INSERT INTO products_pricing (product_id, price, sale_price, cost_price)
  VALUES (2, 15.99, 12.99, 10.99);

INSERT INTO categories (category_id, upper_category, name, short_description, description)
  VALUES (1, NULL, 'Test Category 1', 'Test category 1',
          'Test category 1 desc');
INSERT INTO categories (category_id, upper_category, name, short_description, description)
  VALUES (2, 1, 'Test Subcategory 1', 'Test subcategory 1',
          'Test subcategory 1 desc');

INSERT INTO categories_products (product_id, category_id)
  VALUES (1, 1);
INSERT INTO categories_products (product_id, category_id)
  VALUES (2, 2);
