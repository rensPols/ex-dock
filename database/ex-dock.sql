--
-- PostgreSQL database dump
--

-- Dumped from database version 14.13
-- Dumped by pg_dump version 14.13

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: index; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.index AS ENUM (
  'index, follow',
  'index, nofollow',
  'noindex, follow',
  'noindex nofollow'
  );


ALTER TYPE public.index OWNER TO postgres;

--
-- Name: p_index; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.p_index AS ENUM (
  'index, follow',
  'index, nofollow',
  'noindex, follow',
  'noindex nofollow'
  );


ALTER TYPE public.p_index OWNER TO postgres;

--
-- Name: p_type; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.p_type AS ENUM (
  'product',
  'category',
  'text_page'
  );


ALTER TYPE public.p_type OWNER TO postgres;

CREATE TYPE public.b_permissions AS ENUM (
  'none',
  'read',
  'read-write',
  'write'
  );

ALTER TYPE public.b_permissions OWNER TO postgres;

CREATE TYPE public.cpa_type AS ENUM (
  'bool',
  'float',
  'int',
  'money',
  'string'
  );

ALTER TYPE public.cpa_type OWNER TO postgres;

--
-- Name: check_root_url(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.check_root_url() RETURNS trigger
  LANGUAGE plpgsql
AS $$BEGIN
  IF (SELECT COUNT(*) FROM url_keys WHERE (url_key = '/' AND upper_key != '/') OR (url_key != '/' AND upper_key='/')) > 0
  THEN RAISE EXCEPTION 'The root is only allowed to be combined with the root!';
  END IF;
  RETURN NEW;
END;$$;


ALTER FUNCTION public.check_root_url() OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: backend_permissions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.backend_permissions (
                                          user_id integer NOT NULL,
                                          user_permissions public.b_permissions NOT NULL,
                                          server_settings public.b_permissions NOT NULL,
                                          template public.b_permissions NOT NULL,
                                          category_content public.b_permissions NOT NULL,
                                          category_products public.b_permissions NOT NULL,
                                          product_content public.b_permissions NOT NULL,
                                          product_price public.b_permissions NOT NULL,
                                          product_warehouse public.b_permissions NOT NULL,
                                          text_pages public.b_permissions NOT NULL,
                                          "API_KEY" character varying(128)
);


ALTER TABLE public.backend_permissions OWNER TO postgres;

CREATE TABLE public.templates (
  template_key character varying(100) NOT NULL,
  template_data text NOT NULL,
  data_string text NOT NULL
);

ALTER TABLE public.templates OWNER TO postgres;

CREATE TABLE public.blocks (
                             template_key character varying(100) NOT NULL
);

ALTER TABLE public.blocks OWNER TO postgres;

--
-- Name: blocks_template_key_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

--
-- Name: url_keys; Type: TABLE; Schema: public; Owner: postgres
--

--
-- Name: categories; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.categories (
                                 category_id integer NOT NULL,
                                 upper_category integer,
                                 name character varying(100) NOT NULL,
                                 short_description text NOT NULL,
                                 description text NOT NULL
);


ALTER TABLE public.categories OWNER TO postgres;

--
-- Name: categories_category_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.categories_category_id_seq
  AS integer
  START WITH 1
  INCREMENT BY 1
  NO MINVALUE
  NO MAXVALUE
  CACHE 1;


ALTER TABLE public.categories_category_id_seq OWNER TO postgres;

--
-- Name: categories_category_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.categories_category_id_seq OWNED BY public.categories.category_id;


--
-- Name: categories_products; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.categories_products (
                                          category_id integer NOT NULL,
                                          product_id integer NOT NULL
);


ALTER TABLE public.categories_products OWNER TO postgres;

--
-- Name: categories_seo; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.categories_seo (
                                     category_id integer NOT NULL,
                                     meta_title text,
                                     meta_description text,
                                     meta_keywords text,
                                     page_index public.p_index NOT NULL
);


ALTER TABLE public.categories_seo OWNER TO postgres;

--
-- Name: category_urls; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.category_urls (
                                    url_key character varying(100) NOT NULL,
                                    upper_key character varying(100) NOT NULL,
                                    category_id integer NOT NULL
);


ALTER TABLE public.category_urls OWNER TO postgres;

--
-- Name: custom_product_attributes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.custom_product_attributes (
                                                attribute_key character varying(64) NOT NULL,
                                                scope integer NOT NULL,
                                                name character varying(64) NOT NULL,
                                                type public.cpa_type NOT NULL,
                                                multiselect bit(1) NOT NULL,
                                                required bit(1) NOT NULL
);


ALTER TABLE public.custom_product_attributes OWNER TO postgres;

--
-- Name: eav; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.eav (
                          product_id integer NOT NULL,
                          attribute_key character varying(64) NOT NULL
);


ALTER TABLE public.eav OWNER TO postgres;

--
-- Name: eav_global_bool; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.eav_global_bool (
                                      product_id integer NOT NULL,
                                      attribute_key character varying(64) NOT NULL,
                                      value bit(1) NOT NULL
);


ALTER TABLE public.eav_global_bool OWNER TO postgres;

--
-- Name: eav_global_float; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.eav_global_float (
                                       product_id integer NOT NULL,
                                       attribute_key character varying(64) NOT NULL,
                                       value double precision NOT NULL
);


ALTER TABLE public.eav_global_float OWNER TO postgres;

--
-- Name: eav_global_int; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.eav_global_int (
                                     product_id integer NOT NULL,
                                     attribute_key character varying(64) NOT NULL,
                                     value integer NOT NULL
);


ALTER TABLE public.eav_global_int OWNER TO postgres;

--
-- Name: eav_global_money; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.eav_global_money (
                                       product_id integer NOT NULL,
                                       attribute_key character varying(64) NOT NULL,
                                       value numeric(11,2) NOT NULL
);


ALTER TABLE public.eav_global_money OWNER TO postgres;

--
-- Name: eav_global_multi_select; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.eav_global_multi_select (
                                              product_id integer NOT NULL,
                                              attribute_key character varying(64) NOT NULL,
                                              value integer NOT NULL
);


ALTER TABLE public.eav_global_multi_select OWNER TO postgres;

--
-- Name: eav_global_string; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.eav_global_string (
                                        product_id integer NOT NULL,
                                        attribute_key character varying(64) NOT NULL,
                                        value text NOT NULL
);


ALTER TABLE public.eav_global_string OWNER TO postgres;

--
-- Name: eav_store_view_bool; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.eav_store_view_bool (
                                          product_id integer NOT NULL,
                                          store_view_id integer NOT NULL,
                                          attribute_key character varying(64) NOT NULL,
                                          value bit(1) NOT NULL
);


ALTER TABLE public.eav_store_view_bool OWNER TO postgres;

--
-- Name: eav_store_view_float; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.eav_store_view_float (
                                           product_id integer NOT NULL,
                                           store_view_id integer NOT NULL,
                                           attribute_key character varying(64) NOT NULL,
                                           value double precision NOT NULL
);


ALTER TABLE public.eav_store_view_float OWNER TO postgres;

--
-- Name: eav_store_view_int; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.eav_store_view_int (
                                         product_id integer NOT NULL,
                                         store_view_id integer NOT NULL,
                                         attribute_key character varying(64) NOT NULL,
                                         value integer NOT NULL
);


ALTER TABLE public.eav_store_view_int OWNER TO postgres;

--
-- Name: eav_store_view_money; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.eav_store_view_money (
                                           product_id integer NOT NULL,
                                           store_view_id integer NOT NULL,
                                           attribute_key character varying(64) NOT NULL,
                                           value numeric(11,2) NOT NULL
);


ALTER TABLE public.eav_store_view_money OWNER TO postgres;

--
-- Name: eav_store_view_multi_select; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.eav_store_view_multi_select (
                                                  product_id integer NOT NULL,
                                                  store_view_id integer NOT NULL,
                                                  attribute_key character varying(64) NOT NULL,
                                                  value integer NOT NULL
);


ALTER TABLE public.eav_store_view_multi_select OWNER TO postgres;

--
-- Name: eav_store_view_string; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.eav_store_view_string (
                                            product_id integer NOT NULL,
                                            store_view_id integer NOT NULL,
                                            attribute_key character varying(64) NOT NULL,
                                            value text NOT NULL
);


ALTER TABLE public.eav_store_view_string OWNER TO postgres;

--
-- Name: eav_website_bool; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.eav_website_bool (
                                       product_id integer NOT NULL,
                                       website_id integer NOT NULL,
                                       attribute_key character varying(64) NOT NULL,
                                       value bit(1) NOT NULL
);


ALTER TABLE public.eav_website_bool OWNER TO postgres;

--
-- Name: eav_website_float; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.eav_website_float (
                                        product_id integer NOT NULL,
                                        website_id integer NOT NULL,
                                        attribute_key character varying(64) NOT NULL,
                                        value float NOT NULL
);


ALTER TABLE public.eav_website_float OWNER TO postgres;

--
-- Name: eav_website_int; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.eav_website_int (
                                      product_id integer NOT NULL,
                                      website_id integer NOT NULL,
                                      attribute_key character varying(64) NOT NULL,
                                      value integer NOT NULL
);


ALTER TABLE public.eav_website_int OWNER TO postgres;

--
-- Name: eav_website_money; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.eav_website_money (
                                        product_id integer NOT NULL,
                                        website_id integer NOT NULL,
                                        attribute_key character varying(64) NOT NULL,
                                        value numeric(11,2) NOT NULL
);


ALTER TABLE public.eav_website_money OWNER TO postgres;

--
-- Name: eav_website_multi_select; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.eav_website_multi_select (
                                               product_id integer NOT NULL,
                                               website_id integer NOT NULL,
                                               attribute_key character varying(64) NOT NULL,
                                               value integer NOT NULL
);


ALTER TABLE public.eav_website_multi_select OWNER TO postgres;

--
-- Name: eav_website_string; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.eav_website_string (
                                         product_id integer NOT NULL,
                                         website_id integer NOT NULL,
                                         attribute_key character varying(64) NOT NULL,
                                         value text NOT NULL
);


ALTER TABLE public.eav_website_string OWNER TO postgres;

--
-- Name: multi_select_attributes_bool; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.multi_select_attributes_bool (
                                                   attribute_key character varying(64) NOT NULL,
                                                   option integer NOT NULL,
                                                   value bit(1) NOT NULL
);


ALTER TABLE public.multi_select_attributes_bool OWNER TO postgres;

--
-- Name: multi_select_attributes_float; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.multi_select_attributes_float (
                                                    attribute_key character varying(64) NOT NULL,
                                                    option integer NOT NULL,
                                                    value double precision NOT NULL
);


ALTER TABLE public.multi_select_attributes_float OWNER TO postgres;

--
-- Name: multi_select_attributes_int; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.multi_select_attributes_int (
                                                  attribute_key character varying(64) NOT NULL,
                                                  option integer NOT NULL,
                                                  value integer NOT NULL
);


ALTER TABLE public.multi_select_attributes_int OWNER TO postgres;

--
-- Name: multi_select_attributes_money; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.multi_select_attributes_money (
                                                    attribute_key character varying(64) NOT NULL,
                                                    option integer NOT NULL,
                                                    value numeric(11,2) NOT NULL
);


ALTER TABLE public.multi_select_attributes_money OWNER TO postgres;

--
-- Name: multi_select_attributes_string; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.multi_select_attributes_string (
                                                     attribute_key character varying(64) NOT NULL,
                                                     option integer NOT NULL,
                                                     value text NOT NULL
);


ALTER TABLE public.multi_select_attributes_string OWNER TO postgres;

--
-- Name: product_urls; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.product_urls (
                                   url_key character varying(100) NOT NULL,
                                   upper_key character varying(100) NOT NULL,
                                   product_id integer NOT NULL
);


ALTER TABLE public.product_urls OWNER TO postgres;

--
-- Name: products; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.products (
                               product_id integer NOT NULL,
                               name character varying(250) NOT NULL,
                               short_name character varying(100) NOT NULL,
                               description text NOT NULL,
                               short_description text NOT NULL
);


ALTER TABLE public.products OWNER TO postgres;

--
-- Name: products_pricing; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.products_pricing (
                                       product_id integer NOT NULL,
                                       price numeric(11,2) NOT NULL,
                                       sale_price numeric(11,2) NOT NULL,
                                       cost_price numeric(11,2) NOT NULL
);


ALTER TABLE public.products_pricing OWNER TO postgres;

--
-- Name: products_product_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.products_product_id_seq
  AS integer
  START WITH 1
  INCREMENT BY 1
  NO MINVALUE
  NO MAXVALUE
  CACHE 1;


ALTER TABLE public.products_product_id_seq OWNER TO postgres;

--
-- Name: products_product_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.products_product_id_seq OWNED BY public.products.product_id;


--
-- Name: products_seo; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.products_seo (
                                   product_id integer NOT NULL,
                                   meta_title text,
                                   meta_description text,
                                   meta_keywords text,
                                   page_index public.p_index NOT NULL
);


ALTER TABLE public.products_seo OWNER TO postgres;

--
-- Name: server_data; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.server_data (
                                  key character varying(45) NOT NULL,
                                  value text NOT NULL
);


ALTER TABLE public.server_data OWNER TO postgres;

--
-- Name: server_version; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.server_version (
                                     major integer NOT NULL,
                                     minor integer NOT NULL,
                                     patch integer NOT NULL,
                                     version_name character varying(64) NOT NULL,
                                     version_description text NOT NULL
);


ALTER TABLE public.server_version OWNER TO postgres;

--
-- Name: store_view; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.store_view (
                                 store_view_id integer NOT NULL,
                                 website_id integer NOT NULL,
                                 store_view_name character varying(255)
);


ALTER TABLE public.store_view OWNER TO postgres;

--
-- Name: store_view_store_view_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.store_view_store_view_id_seq
  AS integer
  START WITH 1
  INCREMENT BY 1
  NO MINVALUE
  NO MAXVALUE
  CACHE 1;


ALTER TABLE public.store_view_store_view_id_seq OWNER TO postgres;

--
-- Name: store_view_store_view_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.store_view_store_view_id_seq OWNED BY public.store_view.store_view_id;


--
-- Name: text_page_urls; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.text_page_urls (
                                     url_key character varying(100) NOT NULL,
                                     upper_key character varying(100) NOT NULL,
                                     text_pages_id integer NOT NULL
);


ALTER TABLE public.text_page_urls OWNER TO postgres;

--
-- Name: text_pages; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.text_pages (
                                 text_pages_id integer NOT NULL,
                                 name character varying(128) NOT NULL,
                                 short_text text NOT NULL,
                                 text text NOT NULL
);


ALTER TABLE public.text_pages OWNER TO postgres;

--
-- Name: text_pages_seo; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.text_pages_seo (
                                     text_pages_id integer NOT NULL,
                                     meta_title text,
                                     meta_description text,
                                     meta_keywords text,
                                     page_index public.p_index NOT NULL
);


ALTER TABLE public.text_pages_seo OWNER TO postgres;

--
-- Name: text_pages_text_pages_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.text_pages_text_pages_id_seq
  AS integer
  START WITH 1
  INCREMENT BY 1
  NO MINVALUE
  NO MAXVALUE
  CACHE 1;


ALTER TABLE public.text_pages_text_pages_id_seq OWNER TO postgres;

--
-- Name: text_pages_text_pages_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.text_pages_text_pages_id_seq OWNED BY public.text_pages.text_pages_id;


--
-- Name: url_keys; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.url_keys (
                               url_key character varying(100) NOT NULL,
                               upper_key character varying(100) NOT NULL,
                               page_type public.p_type NOT NULL
);


ALTER TABLE public.url_keys OWNER TO postgres;

--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
                            user_id integer NOT NULL,
                            email character varying(320) NOT NULL UNIQUE ,
                            password character varying(100) NOT NULL
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Name: users_user_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.users_user_id_seq
  AS integer
  START WITH 1
  INCREMENT BY 1
  NO MINVALUE
  NO MAXVALUE
  CACHE 1;


ALTER TABLE public.users_user_id_seq OWNER TO postgres;

--
-- Name: users_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_user_id_seq OWNED BY public.users.user_id;


--
-- Name: websites; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.websites (
                               website_id integer NOT NULL,
                               website_name character varying(255)
);


ALTER TABLE public.websites OWNER TO postgres;

--
-- Name: websites_website_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.websites_website_id_seq
  AS integer
  START WITH 1
  INCREMENT BY 1
  NO MINVALUE
  NO MAXVALUE
  CACHE 1;


ALTER TABLE public.websites_website_id_seq OWNER TO postgres;

--
-- Name: websites_website_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.websites_website_id_seq OWNED BY public.websites.website_id;


--
-- Name: categories category_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categories ALTER COLUMN category_id SET DEFAULT nextval('public.categories_category_id_seq'::regclass);


--
-- Name: products product_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products ALTER COLUMN product_id SET DEFAULT nextval('public.products_product_id_seq'::regclass);


--
-- Name: store_view store_view_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.store_view ALTER COLUMN store_view_id SET DEFAULT nextval('public.store_view_store_view_id_seq'::regclass);


--
-- Name: text_pages text_pages_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.text_pages ALTER COLUMN text_pages_id SET DEFAULT nextval('public.text_pages_text_pages_id_seq'::regclass);


--
-- Name: users user_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN user_id SET DEFAULT nextval('public.users_user_id_seq'::regclass);


--
-- Name: websites website_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.websites ALTER COLUMN website_id SET DEFAULT nextval('public.websites_website_id_seq'::regclass);


--
-- Data for Name: backend_permissions; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.backend_permissions (user_id, user_permissions, server_settings, template, category_content, category_products, product_content, product_price, product_warehouse, text_pages, "API_KEY") FROM stdin;
\.


--
-- Data for Name: categories; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.categories (category_id, upper_category, name, short_description, description) FROM stdin;
\.


--
-- Data for Name: categories_products; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.categories_products (category_id, product_id) FROM stdin;
\.


--
-- Data for Name: categories_seo; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.categories_seo (category_id, meta_title, meta_description, meta_keywords, page_index) FROM stdin;
\.


--
-- Data for Name: category_urls; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.category_urls (url_key, upper_key, category_id) FROM stdin;
\.


--
-- Data for Name: custom_product_attributes; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.custom_product_attributes (attribute_key, scope, name, type, multiselect, required) FROM stdin;
\.


--
-- Data for Name: eav; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.eav (product_id, attribute_key) FROM stdin;
\.


--
-- Data for Name: eav_global_bool; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.eav_global_bool (product_id, attribute_key, value) FROM stdin;
\.


--
-- Data for Name: eav_global_float; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.eav_global_float (product_id, attribute_key, value) FROM stdin;
\.


--
-- Data for Name: eav_global_int; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.eav_global_int (product_id, attribute_key, value) FROM stdin;
\.


--
-- Data for Name: eav_global_money; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.eav_global_money (product_id, attribute_key, value) FROM stdin;
\.


--
-- Data for Name: eav_global_multi_select; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.eav_global_multi_select (product_id, attribute_key, value) FROM stdin;
\.


--
-- Data for Name: eav_global_string; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.eav_global_string (product_id, attribute_key, value) FROM stdin;
\.


--
-- Data for Name: eav_store_view_bool; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.eav_store_view_bool (product_id, store_view_id, attribute_key, value) FROM stdin;
\.


--
-- Data for Name: eav_store_view_float; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.eav_store_view_float (product_id, store_view_id, attribute_key, value) FROM stdin;
\.


--
-- Data for Name: eav_store_view_int; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.eav_store_view_int (product_id, store_view_id, attribute_key, value) FROM stdin;
\.


--
-- Data for Name: eav_store_view_money; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.eav_store_view_money (product_id, store_view_id, attribute_key, value) FROM stdin;
\.


--
-- Data for Name: eav_store_view_multi_select; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.eav_store_view_multi_select (product_id, store_view_id, attribute_key, value) FROM stdin;
\.


--
-- Data for Name: eav_store_view_string; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.eav_store_view_string (product_id, store_view_id, attribute_key, value) FROM stdin;
\.


--
-- Data for Name: eav_website_bool; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.eav_website_bool (product_id, website_id, attribute_key, value) FROM stdin;
\.


--
-- Data for Name: eav_website_float; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.eav_website_float (product_id, website_id, attribute_key) FROM stdin;
\.


--
-- Data for Name: eav_website_int; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.eav_website_int (product_id, website_id, attribute_key) FROM stdin;
\.


--
-- Data for Name: eav_website_money; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.eav_website_money (product_id, website_id, attribute_key, value) FROM stdin;
\.


--
-- Data for Name: eav_website_multi_select; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.eav_website_multi_select (product_id, website_id, attribute_key) FROM stdin;
\.


--
-- Data for Name: eav_website_string; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.eav_website_string (product_id, website_id, attribute_key, value) FROM stdin;
\.


--
-- Data for Name: multi_select_attributes_bool; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.multi_select_attributes_bool (attribute_key, option, value) FROM stdin;
\.


--
-- Data for Name: multi_select_attributes_float; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.multi_select_attributes_float (attribute_key, option, value) FROM stdin;
\.


--
-- Data for Name: multi_select_attributes_int; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.multi_select_attributes_int (attribute_key, option, value) FROM stdin;
\.


--
-- Data for Name: multi_select_attributes_money; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.multi_select_attributes_money (attribute_key, option, value) FROM stdin;
\.


--
-- Data for Name: multi_select_attributes_string; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.multi_select_attributes_string (attribute_key, option, value) FROM stdin;
\.


--
-- Data for Name: product_urls; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.product_urls (url_key, upper_key, product_id) FROM stdin;
\.


--
-- Data for Name: products; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.products (product_id, name, short_name, description, short_description) FROM stdin;
\.


--
-- Data for Name: products_pricing; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.products_pricing (product_id, price, sale_price, cost_price) FROM stdin;
\.


--
-- Data for Name: products_seo; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.products_seo (product_id, meta_title, meta_description, meta_keywords, page_index) FROM stdin;
\.


--
-- Data for Name: server_data; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.server_data (key, value) FROM stdin;
\.


--
-- Data for Name: server_version; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.server_version (major, minor, patch, version_name, version_description) FROM stdin;
\.


--
-- Data for Name: store_view; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.store_view (store_view_id, website_id, store_view_name) FROM stdin;
\.


--
-- Data for Name: text_page_urls; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.text_page_urls (url_key, upper_key, text_pages_id) FROM stdin;
\.


--
-- Data for Name: text_pages; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.text_pages (text_pages_id, name, short_text, text) FROM stdin;
\.


--
-- Data for Name: text_pages_seo; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.text_pages_seo (text_pages_id, meta_title, meta_description, meta_keywords, page_index) FROM stdin;
\.


--
-- Data for Name: url_keys; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.url_keys (url_key, upper_key, page_type) FROM stdin;
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (user_id, email, password) FROM stdin;
\.


--
-- Data for Name: websites; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.websites (website_id, website_name) FROM stdin;
\.


--
-- Name: categories_category_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.categories_category_id_seq', 1, false);


--
-- Name: products_product_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.products_product_id_seq', 1, false);


--
-- Name: store_view_store_view_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.store_view_store_view_id_seq', 1, false);


--
-- Name: text_pages_text_pages_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.text_pages_text_pages_id_seq', 1, false);


--
-- Name: users_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.users_user_id_seq', 1, false);


--
-- Name: websites_website_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.websites_website_id_seq', 1, false);


--
-- Name: url_keys UK_1; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.url_keys
  ADD CONSTRAINT "UK_1" UNIQUE (url_key);


--
-- Name: categories categories_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categories
  ADD CONSTRAINT categories_pkey PRIMARY KEY (category_id);


--
-- Name: categories_products categories_products_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categories_products
  ADD CONSTRAINT categories_products_pkey PRIMARY KEY (category_id, product_id);


--
-- Name: categories_seo categories_seo_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categories_seo
  ADD CONSTRAINT categories_seo_pkey PRIMARY KEY (category_id);


--
-- Name: category_urls category_urls_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.category_urls
  ADD CONSTRAINT category_urls_pkey PRIMARY KEY (url_key, upper_key);


--
-- Name: custom_product_attributes custom_product_attributes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.custom_product_attributes
  ADD CONSTRAINT custom_product_attributes_pkey PRIMARY KEY (attribute_key);


--
-- Name: eav_global_bool eav_global_bool_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_global_bool
  ADD CONSTRAINT eav_global_bool_pkey PRIMARY KEY (product_id, attribute_key);


--
-- Name: eav_global_float eav_global_float_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_global_float
  ADD CONSTRAINT eav_global_float_pkey PRIMARY KEY (product_id, attribute_key);


--
-- Name: eav_global_int eav_global_int_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_global_int
  ADD CONSTRAINT eav_global_int_pkey PRIMARY KEY (product_id, attribute_key);


--
-- Name: eav_global_money eav_global_money_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_global_money
  ADD CONSTRAINT eav_global_money_pkey PRIMARY KEY (product_id, attribute_key);


--
-- Name: eav_global_multi_select eav_global_multi_select_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_global_multi_select
  ADD CONSTRAINT eav_global_multi_select_pkey PRIMARY KEY (product_id, attribute_key);


--
-- Name: eav_global_string eav_global_string_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_global_string
  ADD CONSTRAINT eav_global_string_pkey PRIMARY KEY (product_id, attribute_key);


--
-- Name: eav eav_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav
  ADD CONSTRAINT eav_pkey PRIMARY KEY (product_id, attribute_key);


--
-- Name: eav_store_view_bool eav_store_view_bool_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_bool
  ADD CONSTRAINT eav_store_view_bool_pkey PRIMARY KEY (product_id, store_view_id, attribute_key);


--
-- Name: eav_store_view_float eav_store_view_float_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_float
  ADD CONSTRAINT eav_store_view_float_pkey PRIMARY KEY (product_id, store_view_id, attribute_key);


--
-- Name: eav_store_view_int eav_store_view_int_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_int
  ADD CONSTRAINT eav_store_view_int_pkey PRIMARY KEY (product_id, store_view_id, attribute_key);


--
-- Name: eav_store_view_money eav_store_view_money_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_money
  ADD CONSTRAINT eav_store_view_money_pkey PRIMARY KEY (product_id, store_view_id, attribute_key);


--
-- Name: eav_store_view_multi_select eav_store_view_multi_select_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_multi_select
  ADD CONSTRAINT eav_store_view_multi_select_pkey PRIMARY KEY (product_id, store_view_id, attribute_key);


--
-- Name: eav_store_view_string eav_store_view_string_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_string
  ADD CONSTRAINT eav_store_view_string_pkey PRIMARY KEY (product_id, store_view_id, attribute_key);


--
-- Name: eav_website_bool eav_website_bool_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_bool
  ADD CONSTRAINT eav_website_bool_pkey PRIMARY KEY (product_id, website_id, attribute_key);


--
-- Name: eav_website_float eav_website_float_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_float
  ADD CONSTRAINT eav_website_float_pkey PRIMARY KEY (product_id, website_id, attribute_key);


--
-- Name: eav_website_int eav_website_int_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_int
  ADD CONSTRAINT eav_website_int_pkey PRIMARY KEY (product_id, website_id, attribute_key);


--
-- Name: eav_website_money eav_website_money_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_money
  ADD CONSTRAINT eav_website_money_pkey PRIMARY KEY (product_id, website_id, attribute_key);


--
-- Name: eav_website_multi_select eav_website_multi_select_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_multi_select
  ADD CONSTRAINT eav_website_multi_select_pkey PRIMARY KEY (product_id, website_id, attribute_key);


--
-- Name: eav_website_string eav_website_string_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_string
  ADD CONSTRAINT eav_website_string_pkey PRIMARY KEY (product_id, website_id, attribute_key);


--
-- Name: multi_select_attributes_bool multi_select_attributes_bool_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.multi_select_attributes_bool
  ADD CONSTRAINT multi_select_attributes_bool_pkey PRIMARY KEY (attribute_key, option);


--
-- Name: multi_select_attributes_float multi_select_attributes_float_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.multi_select_attributes_float
  ADD CONSTRAINT multi_select_attributes_float_pkey PRIMARY KEY (attribute_key, option);


--
-- Name: multi_select_attributes_int multi_select_attributes_int_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.multi_select_attributes_int
  ADD CONSTRAINT multi_select_attributes_int_pkey PRIMARY KEY (attribute_key, option);


--
-- Name: multi_select_attributes_money multi_select_attributes_money_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.multi_select_attributes_money
  ADD CONSTRAINT multi_select_attributes_money_pkey PRIMARY KEY (attribute_key, option);


--
-- Name: multi_select_attributes_string multi_select_attributes_string_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.multi_select_attributes_string
  ADD CONSTRAINT multi_select_attributes_string_pkey PRIMARY KEY (attribute_key, option);


--
-- Name: product_urls product_urls_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product_urls
  ADD CONSTRAINT product_urls_pkey PRIMARY KEY (url_key, upper_key);


--
-- Name: products products_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products
  ADD CONSTRAINT products_pkey PRIMARY KEY (product_id);


--
-- Name: products_pricing products_pricing_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products_pricing
  ADD CONSTRAINT products_pricing_pkey PRIMARY KEY (product_id);


--
-- Name: products_seo products_seo_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products_seo
  ADD CONSTRAINT products_seo_pkey PRIMARY KEY (product_id);


--
-- Name: server_data server_data_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.server_data
  ADD CONSTRAINT server_data_pkey PRIMARY KEY (key);


--
-- Name: server_version server_version_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.server_version
  ADD CONSTRAINT server_version_pkey PRIMARY KEY (major, minor, patch);


--
-- Name: store_view store_view_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.store_view
  ADD CONSTRAINT store_view_pkey PRIMARY KEY (store_view_id);


--
-- Name: text_page_urls text_page_urls_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.text_page_urls
  ADD CONSTRAINT text_page_urls_pkey PRIMARY KEY (url_key, upper_key);


--
-- Name: text_pages text_pages_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.text_pages
  ADD CONSTRAINT text_pages_pkey PRIMARY KEY (text_pages_id);


--
-- Name: text_pages_seo text_pages_seo_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.text_pages_seo
  ADD CONSTRAINT text_pages_seo_pkey PRIMARY KEY (text_pages_id);


--
-- Name: url_keys url_keys_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.url_keys
  ADD CONSTRAINT url_keys_pkey PRIMARY KEY (url_key, upper_key);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
  ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);


--
-- Name: websites websites_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.websites
  ADD CONSTRAINT websites_pkey PRIMARY KEY (website_id);

ALTER TABLE ONLY public.templates
  ADD CONSTRAINT templates_pkey PRIMARY KEY (template_key);

ALTER TABLE ONLY public.blocks
  ADD CONSTRAINT blocks_pkey PRIMARY KEY (template_key);

CREATE INDEX "fki_FK_72" ON public.templates USING btree (template_key);

CREATE INDEX "fki_FK_73" ON public.blocks USING btree (template_key);


--
-- Name: fki_FK_1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX "fki_FK_1" ON public.backend_permissions USING btree (user_id);


--
-- Name: fki_FK_2; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX "fki_FK_2" ON public.eav USING btree (attribute_key);


--
-- Name: fki_FK_3; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX "fki_FK_3" ON public.eav_store_view_bool USING btree (attribute_key);


--
-- Name: fki_FK_61; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX "fki_FK_61" ON public.categories_seo USING btree (category_id);


--
-- Name: fki_FK_62; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX "fki_FK_62" ON public.url_keys USING btree (upper_key);


--
-- Name: fki_FK_67; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX "fki_FK_67" ON public.categories USING btree (upper_category);


--
-- Name: url_keys after_insert_url_keys; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER after_insert_url_keys AFTER INSERT ON public.url_keys FOR EACH ROW EXECUTE FUNCTION public.check_root_url();


--
-- Name: backend_permissions FK_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.backend_permissions
  ADD CONSTRAINT "FK_1" FOREIGN KEY (user_id) REFERENCES public.users(user_id);


--
-- Name: multi_select_attributes_money FK_10; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.multi_select_attributes_money
  ADD CONSTRAINT "FK_10" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key);


--
-- Name: multi_select_attributes_bool FK_11; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.multi_select_attributes_bool
  ADD CONSTRAINT "FK_11" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key);


--
-- Name: eav_global_string FK_12; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_global_string
  ADD CONSTRAINT "FK_12" FOREIGN KEY (product_id) REFERENCES public.products(product_id);


--
-- Name: eav_global_string FK_13; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_global_string
  ADD CONSTRAINT "FK_13" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key);


--
-- Name: eav_global_int FK_14; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_global_int
  ADD CONSTRAINT "FK_14" FOREIGN KEY (product_id) REFERENCES public.products(product_id);


--
-- Name: eav_global_int FK_15; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_global_int
  ADD CONSTRAINT "FK_15" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key);


--
-- Name: eav_global_float FK_16; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_global_float
  ADD CONSTRAINT "FK_16" FOREIGN KEY (product_id) REFERENCES public.products(product_id);


--
-- Name: eav_global_float FK_17; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_global_float
  ADD CONSTRAINT "FK_17" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key);


--
-- Name: eav_global_money FK_18; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_global_money
  ADD CONSTRAINT "FK_18" FOREIGN KEY (product_id) REFERENCES public.products(product_id);


--
-- Name: eav_global_money FK_19; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_global_money
  ADD CONSTRAINT "FK_19" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key);


--
-- Name: eav FK_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav
  ADD CONSTRAINT "FK_2" FOREIGN KEY (product_id) REFERENCES public.products(product_id);


--
-- Name: eav_global_bool FK_20; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_global_bool
  ADD CONSTRAINT "FK_20" FOREIGN KEY (product_id) REFERENCES public.products(product_id);


--
-- Name: eav_global_bool FK_21; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_global_bool
  ADD CONSTRAINT "FK_21" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key);


--
-- Name: eav_global_multi_select FK_22; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_global_multi_select
  ADD CONSTRAINT "FK_22" FOREIGN KEY (product_id) REFERENCES public.products(product_id);


--
-- Name: store_view FK_22_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.store_view
  ADD CONSTRAINT "FK_22_1" FOREIGN KEY (website_id) REFERENCES public.websites(website_id);


--
-- Name: eav_global_multi_select FK_23; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_global_multi_select
  ADD CONSTRAINT "FK_23" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key);


--
-- Name: eav_website_bool FK_23_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_bool
  ADD CONSTRAINT "FK_23_1" FOREIGN KEY (product_id) REFERENCES public.products(product_id);


--
-- Name: eav_website_bool FK_24; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_bool
  ADD CONSTRAINT "FK_24" FOREIGN KEY (website_id) REFERENCES public.websites(website_id);


--
-- Name: eav_website_bool FK_25; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_bool
  ADD CONSTRAINT "FK_25" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key);


--
-- Name: eav_website_float FK_26; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_float
  ADD CONSTRAINT "FK_26" FOREIGN KEY (product_id) REFERENCES public.products(product_id);


--
-- Name: eav_website_float FK_27; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_float
  ADD CONSTRAINT "FK_27" FOREIGN KEY (website_id) REFERENCES public.websites(website_id);


--
-- Name: eav_website_float FK_28; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_float
  ADD CONSTRAINT "FK_28" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key);


--
-- Name: eav_website_int FK_29; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_int
  ADD CONSTRAINT "FK_29" FOREIGN KEY (product_id) REFERENCES public.products(product_id);


--
-- Name: eav FK_3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav
  ADD CONSTRAINT "FK_3" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key);


--
-- Name: eav_website_int FK_30; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_int
  ADD CONSTRAINT "FK_30" FOREIGN KEY (website_id) REFERENCES public.websites(website_id);


--
-- Name: eav_website_int FK_31; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_int
  ADD CONSTRAINT "FK_31" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key);


--
-- Name: eav_website_money FK_32; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_money
  ADD CONSTRAINT "FK_32" FOREIGN KEY (product_id) REFERENCES public.products(product_id);


--
-- Name: eav_website_money FK_33; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_money
  ADD CONSTRAINT "FK_33" FOREIGN KEY (website_id) REFERENCES public.websites(website_id);


--
-- Name: eav_website_money FK_34; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_money
  ADD CONSTRAINT "FK_34" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key);


--
-- Name: eav_website_multi_select FK_35; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_multi_select
  ADD CONSTRAINT "FK_35" FOREIGN KEY (product_id) REFERENCES public.products(product_id);


--
-- Name: eav_website_multi_select FK_36; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_multi_select
  ADD CONSTRAINT "FK_36" FOREIGN KEY (website_id) REFERENCES public.websites(website_id);


--
-- Name: eav_website_multi_select FK_37; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_multi_select
  ADD CONSTRAINT "FK_37" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key);


--
-- Name: eav_website_string FK_38; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_string
  ADD CONSTRAINT "FK_38" FOREIGN KEY (product_id) REFERENCES public.products(product_id);


--
-- Name: eav_website_string FK_39; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_string
  ADD CONSTRAINT "FK_39" FOREIGN KEY (website_id) REFERENCES public.websites(website_id);


--
-- Name: eav_website_string FK_40; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_string
  ADD CONSTRAINT "FK_40" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key);


--
-- Name: eav_store_view_bool FK_41; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_bool
  ADD CONSTRAINT "FK_41" FOREIGN KEY (product_id) REFERENCES public.products(product_id);


--
-- Name: eav_store_view_bool FK_42; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_bool
  ADD CONSTRAINT "FK_42" FOREIGN KEY (store_view_id) REFERENCES public.store_view(store_view_id);


--
-- Name: eav_store_view_bool FK_43; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_bool
  ADD CONSTRAINT "FK_43" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key);


--
-- Name: eav_store_view_float FK_44; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_float
  ADD CONSTRAINT "FK_44" FOREIGN KEY (product_id) REFERENCES public.products(product_id);


--
-- Name: eav_store_view_float FK_45; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_float
  ADD CONSTRAINT "FK_45" FOREIGN KEY (store_view_id) REFERENCES public.store_view(store_view_id);


--
-- Name: eav_store_view_float FK_46; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_float
  ADD CONSTRAINT "FK_46" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key);


--
-- Name: eav_store_view_int FK_47; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_int
  ADD CONSTRAINT "FK_47" FOREIGN KEY (product_id) REFERENCES public.products(product_id);


--
-- Name: eav_store_view_int FK_48; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_int
  ADD CONSTRAINT "FK_48" FOREIGN KEY (store_view_id) REFERENCES public.store_view(store_view_id);


--
-- Name: eav_store_view_int FK_49; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_int
  ADD CONSTRAINT "FK_49" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key);


--
-- Name: eav_store_view_money FK_50; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_money
  ADD CONSTRAINT "FK_50" FOREIGN KEY (product_id) REFERENCES public.products(product_id);


--
-- Name: eav_store_view_money FK_51; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_money
  ADD CONSTRAINT "FK_51" FOREIGN KEY (store_view_id) REFERENCES public.store_view(store_view_id);


--
-- Name: eav_store_view_money FK_52; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_money
  ADD CONSTRAINT "FK_52" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key);


--
-- Name: eav_store_view_multi_select FK_53; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_multi_select
  ADD CONSTRAINT "FK_53" FOREIGN KEY (product_id) REFERENCES public.products(product_id);


--
-- Name: eav_store_view_multi_select FK_54; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_multi_select
  ADD CONSTRAINT "FK_54" FOREIGN KEY (store_view_id) REFERENCES public.store_view(store_view_id);


--
-- Name: eav_store_view_multi_select FK_55; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_multi_select
  ADD CONSTRAINT "FK_55" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key);


--
-- Name: eav_store_view_string FK_56; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_string
  ADD CONSTRAINT "FK_56" FOREIGN KEY (product_id) REFERENCES public.products(product_id);


--
-- Name: eav_store_view_string FK_57; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_string
  ADD CONSTRAINT "FK_57" FOREIGN KEY (store_view_id) REFERENCES public.store_view(store_view_id);


--
-- Name: eav_store_view_string FK_58; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_string
  ADD CONSTRAINT "FK_58" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key);


--
-- Name: categories_products FK_59; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categories_products
  ADD CONSTRAINT "FK_59" FOREIGN KEY (category_id) REFERENCES public.categories(category_id);


--
-- Name: multi_select_attributes_string FK_6; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.multi_select_attributes_string
  ADD CONSTRAINT "FK_6" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key);


--
-- Name: categories_products FK_60; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categories_products
  ADD CONSTRAINT "FK_60" FOREIGN KEY (product_id) REFERENCES public.products(product_id);


--
-- Name: categories_seo FK_61; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categories_seo
  ADD CONSTRAINT "FK_61" FOREIGN KEY (category_id) REFERENCES public.categories(category_id);


--
-- Name: url_keys FK_62; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.url_keys
  ADD CONSTRAINT "FK_62" FOREIGN KEY (upper_key) REFERENCES public.url_keys(url_key);


--
-- Name: product_urls FK_63; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product_urls
  ADD CONSTRAINT "FK_63" FOREIGN KEY (url_key, upper_key) REFERENCES public.url_keys(url_key, upper_key);


--
-- Name: product_urls FK_64; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product_urls
  ADD CONSTRAINT "FK_64" FOREIGN KEY (product_id) REFERENCES public.products(product_id);


--
-- Name: category_urls FK_65; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.category_urls
  ADD CONSTRAINT "FK_65" FOREIGN KEY (url_key, upper_key) REFERENCES public.url_keys(url_key, upper_key);


--
-- Name: category_urls FK_66; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.category_urls
  ADD CONSTRAINT "FK_66" FOREIGN KEY (category_id) REFERENCES public.categories(category_id);


--
-- Name: categories FK_67; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categories
  ADD CONSTRAINT "FK_67" FOREIGN KEY (upper_category) REFERENCES public.categories(category_id);


--
-- Name: products_seo FK_68; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products_seo
  ADD CONSTRAINT "FK_68" FOREIGN KEY (product_id) REFERENCES public.products(product_id);


--
-- Name: text_pages_seo FK_69; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.text_pages_seo
  ADD CONSTRAINT "FK_69" FOREIGN KEY (text_pages_id) REFERENCES public.text_pages(text_pages_id);


--
-- Name: products_pricing FK_7; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products_pricing
  ADD CONSTRAINT "FK_7" FOREIGN KEY (product_id) REFERENCES public.products(product_id);


--
-- Name: text_page_urls FK_70; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.text_page_urls
  ADD CONSTRAINT "FK_70" FOREIGN KEY (url_key, upper_key) REFERENCES public.url_keys(url_key, upper_key);


--
-- Name: text_page_urls FK_71; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.text_page_urls
  ADD CONSTRAINT "FK_71" FOREIGN KEY (text_pages_id) REFERENCES public.text_pages(text_pages_id);


--
-- Name: multi_select_attributes_int FK_8; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.multi_select_attributes_int
  ADD CONSTRAINT "FK_8" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key);


--
-- Name: multi_select_attributes_float FK_9; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.multi_select_attributes_float
  ADD CONSTRAINT "FK_9" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key);

ALTER TABLE ONLY public.blocks
  ADD CONSTRAINT "FK_72" FOREIGN KEY (template_key) REFERENCES public.templates(template_key);

--


--
-- PostgreSQL database dump complete
--

