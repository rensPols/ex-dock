--
-- PostgreSQL database dump
--

-- Dumped from database version 16.1
-- Dumped by pg_dump version 16.1

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

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: backend_permissions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.backend_permissions (
    user_id integer NOT NULL,
    user_permissions integer NOT NULL,
    server_settings integer NOT NULL,
    template integer NOT NULL,
    category_content integer NOT NULL,
    category_products integer NOT NULL,
    product_content integer NOT NULL,
    product_price integer NOT NULL,
    product_warehouse integer NOT NULL,
    text_pages integer NOT NULL,
    "API_KEY" character varying(128)
);


ALTER TABLE public.backend_permissions OWNER TO postgres;

--
-- Name: custom_product_attributes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.custom_product_attributes (
    attribute_key character varying(64) NOT NULL,
    scope integer NOT NULL,
    name character varying(64) NOT NULL,
    type integer NOT NULL,
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
    bool bit(1) NOT NULL
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
    attribute_key character varying(64) NOT NULL
);


ALTER TABLE public.eav_website_float OWNER TO postgres;

--
-- Name: eav_website_int; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.eav_website_int (
    product_id integer NOT NULL,
    website_id integer NOT NULL,
    attribute_key character varying(64) NOT NULL
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
    attribute_key character varying(64) NOT NULL
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


ALTER SEQUENCE public.products_product_id_seq OWNER TO postgres;

--
-- Name: products_product_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.products_product_id_seq OWNED BY public.products.product_id;


--
-- Name: server_data; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.server_data (
    key character varying(45) NOT NULL,
    value text NOT NULL
);


ALTER TABLE public.server_data OWNER TO postgres;

--
-- Name: store_view; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.store_view (
    store_view_id integer NOT NULL,
    website_id integer NOT NULL
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


ALTER SEQUENCE public.store_view_store_view_id_seq OWNER TO postgres;

--
-- Name: store_view_store_view_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.store_view_store_view_id_seq OWNED BY public.store_view.store_view_id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    user_id integer NOT NULL,
    email character varying(320) NOT NULL,
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


ALTER SEQUENCE public.users_user_id_seq OWNER TO postgres;

--
-- Name: users_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_user_id_seq OWNED BY public.users.user_id;


--
-- Name: websites; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.websites (
    website_id integer NOT NULL
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


ALTER SEQUENCE public.websites_website_id_seq OWNER TO postgres;

--
-- Name: websites_website_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.websites_website_id_seq OWNED BY public.websites.website_id;


--
-- Name: products product_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products ALTER COLUMN product_id SET DEFAULT nextval('public.products_product_id_seq'::regclass);


--
-- Name: store_view store_view_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.store_view ALTER COLUMN store_view_id SET DEFAULT nextval('public.store_view_store_view_id_seq'::regclass);


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

COPY public.eav_global_bool (product_id, attribute_key, bool) FROM stdin;
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
-- Data for Name: server_data; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.server_data (key, value) FROM stdin;
\.


--
-- Data for Name: store_view; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.store_view (store_view_id, website_id) FROM stdin;
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (user_id, email, password) FROM stdin;
\.


--
-- Data for Name: websites; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.websites (website_id) FROM stdin;
\.


--
-- Name: products_product_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.products_product_id_seq', 1, false);


--
-- Name: store_view_store_view_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.store_view_store_view_id_seq', 1, false);


--
-- Name: users_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.users_user_id_seq', 1, false);


--
-- Name: websites_website_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.websites_website_id_seq', 1, false);


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
-- Name: server_data server_data_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.server_data
    ADD CONSTRAINT server_data_pkey PRIMARY KEY (key);


--
-- Name: store_view store_view_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.store_view
    ADD CONSTRAINT store_view_pkey PRIMARY KEY (store_view_id);


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
-- Name: backend_permissions FK_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.backend_permissions
    ADD CONSTRAINT "FK_1" FOREIGN KEY (user_id) REFERENCES public.users(user_id) NOT VALID;


--
-- Name: eav FK_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav
    ADD CONSTRAINT "FK_1" FOREIGN KEY (product_id) REFERENCES public.products(product_id) NOT VALID;


--
-- Name: eav_global_bool FK_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_global_bool
    ADD CONSTRAINT "FK_1" FOREIGN KEY (product_id) REFERENCES public.products(product_id) NOT VALID;


--
-- Name: eav_global_float FK_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_global_float
    ADD CONSTRAINT "FK_1" FOREIGN KEY (product_id) REFERENCES public.products(product_id) NOT VALID;


--
-- Name: eav_global_int FK_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_global_int
    ADD CONSTRAINT "FK_1" FOREIGN KEY (product_id) REFERENCES public.products(product_id) NOT VALID;


--
-- Name: eav_global_money FK_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_global_money
    ADD CONSTRAINT "FK_1" FOREIGN KEY (product_id) REFERENCES public.products(product_id) NOT VALID;


--
-- Name: eav_global_multi_select FK_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_global_multi_select
    ADD CONSTRAINT "FK_1" FOREIGN KEY (product_id) REFERENCES public.products(product_id) NOT VALID;


--
-- Name: eav_global_string FK_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_global_string
    ADD CONSTRAINT "FK_1" FOREIGN KEY (product_id) REFERENCES public.products(product_id) NOT VALID;


--
-- Name: eav_store_view_bool FK_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_bool
    ADD CONSTRAINT "FK_1" FOREIGN KEY (product_id) REFERENCES public.products(product_id) NOT VALID;


--
-- Name: eav_store_view_float FK_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_float
    ADD CONSTRAINT "FK_1" FOREIGN KEY (product_id) REFERENCES public.products(product_id) NOT VALID;


--
-- Name: eav_store_view_int FK_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_int
    ADD CONSTRAINT "FK_1" FOREIGN KEY (product_id) REFERENCES public.products(product_id) NOT VALID;


--
-- Name: eav_store_view_money FK_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_money
    ADD CONSTRAINT "FK_1" FOREIGN KEY (product_id) REFERENCES public.products(product_id) NOT VALID;


--
-- Name: eav_store_view_multi_select FK_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_multi_select
    ADD CONSTRAINT "FK_1" FOREIGN KEY (product_id) REFERENCES public.products(product_id) NOT VALID;


--
-- Name: eav_store_view_string FK_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_string
    ADD CONSTRAINT "FK_1" FOREIGN KEY (product_id) REFERENCES public.products(product_id) NOT VALID;


--
-- Name: eav_website_bool FK_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_bool
    ADD CONSTRAINT "FK_1" FOREIGN KEY (product_id) REFERENCES public.products(product_id) NOT VALID;


--
-- Name: eav_website_float FK_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_float
    ADD CONSTRAINT "FK_1" FOREIGN KEY (product_id) REFERENCES public.products(product_id) NOT VALID;


--
-- Name: eav_website_int FK_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_int
    ADD CONSTRAINT "FK_1" FOREIGN KEY (product_id) REFERENCES public.products(product_id) NOT VALID;


--
-- Name: eav_website_money FK_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_money
    ADD CONSTRAINT "FK_1" FOREIGN KEY (product_id) REFERENCES public.products(product_id) NOT VALID;


--
-- Name: eav_website_multi_select FK_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_multi_select
    ADD CONSTRAINT "FK_1" FOREIGN KEY (product_id) REFERENCES public.products(product_id) NOT VALID;


--
-- Name: eav_website_string FK_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_string
    ADD CONSTRAINT "FK_1" FOREIGN KEY (product_id) REFERENCES public.products(product_id) NOT VALID;


--
-- Name: multi_select_attributes_bool FK_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.multi_select_attributes_bool
    ADD CONSTRAINT "FK_1" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key) NOT VALID;


--
-- Name: multi_select_attributes_float FK_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.multi_select_attributes_float
    ADD CONSTRAINT "FK_1" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key) NOT VALID;


--
-- Name: multi_select_attributes_int FK_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.multi_select_attributes_int
    ADD CONSTRAINT "FK_1" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key) NOT VALID;


--
-- Name: multi_select_attributes_money FK_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.multi_select_attributes_money
    ADD CONSTRAINT "FK_1" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key) NOT VALID;


--
-- Name: multi_select_attributes_string FK_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.multi_select_attributes_string
    ADD CONSTRAINT "FK_1" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key) NOT VALID;


--
-- Name: products_pricing FK_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products_pricing
    ADD CONSTRAINT "FK_1" FOREIGN KEY (product_id) REFERENCES public.products(product_id) NOT VALID;


--
-- Name: store_view FK_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.store_view
    ADD CONSTRAINT "FK_1" FOREIGN KEY (website_id) REFERENCES public.websites(website_id) NOT VALID;


--
-- Name: eav FK_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav
    ADD CONSTRAINT "FK_2" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key) NOT VALID;


--
-- Name: eav_global_bool FK_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_global_bool
    ADD CONSTRAINT "FK_2" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key) NOT VALID;


--
-- Name: eav_global_float FK_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_global_float
    ADD CONSTRAINT "FK_2" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key) NOT VALID;


--
-- Name: eav_global_int FK_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_global_int
    ADD CONSTRAINT "FK_2" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key) NOT VALID;


--
-- Name: eav_global_money FK_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_global_money
    ADD CONSTRAINT "FK_2" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key) NOT VALID;


--
-- Name: eav_global_multi_select FK_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_global_multi_select
    ADD CONSTRAINT "FK_2" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key) NOT VALID;


--
-- Name: eav_global_string FK_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_global_string
    ADD CONSTRAINT "FK_2" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key) NOT VALID;


--
-- Name: eav_store_view_bool FK_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_bool
    ADD CONSTRAINT "FK_2" FOREIGN KEY (store_view_id) REFERENCES public.store_view(store_view_id) NOT VALID;


--
-- Name: eav_store_view_float FK_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_float
    ADD CONSTRAINT "FK_2" FOREIGN KEY (store_view_id) REFERENCES public.store_view(store_view_id) NOT VALID;


--
-- Name: eav_store_view_int FK_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_int
    ADD CONSTRAINT "FK_2" FOREIGN KEY (store_view_id) REFERENCES public.store_view(store_view_id) NOT VALID;


--
-- Name: eav_store_view_money FK_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_money
    ADD CONSTRAINT "FK_2" FOREIGN KEY (store_view_id) REFERENCES public.store_view(store_view_id) NOT VALID;


--
-- Name: eav_store_view_multi_select FK_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_multi_select
    ADD CONSTRAINT "FK_2" FOREIGN KEY (store_view_id) REFERENCES public.store_view(store_view_id) NOT VALID;


--
-- Name: eav_store_view_string FK_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_string
    ADD CONSTRAINT "FK_2" FOREIGN KEY (store_view_id) REFERENCES public.store_view(store_view_id) NOT VALID;


--
-- Name: eav_website_bool FK_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_bool
    ADD CONSTRAINT "FK_2" FOREIGN KEY (website_id) REFERENCES public.websites(website_id) NOT VALID;


--
-- Name: eav_website_float FK_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_float
    ADD CONSTRAINT "FK_2" FOREIGN KEY (website_id) REFERENCES public.websites(website_id) NOT VALID;


--
-- Name: eav_website_int FK_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_int
    ADD CONSTRAINT "FK_2" FOREIGN KEY (website_id) REFERENCES public.websites(website_id) NOT VALID;


--
-- Name: eav_website_money FK_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_money
    ADD CONSTRAINT "FK_2" FOREIGN KEY (website_id) REFERENCES public.websites(website_id) NOT VALID;


--
-- Name: eav_website_multi_select FK_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_multi_select
    ADD CONSTRAINT "FK_2" FOREIGN KEY (website_id) REFERENCES public.websites(website_id) NOT VALID;


--
-- Name: eav_website_string FK_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_string
    ADD CONSTRAINT "FK_2" FOREIGN KEY (website_id) REFERENCES public.websites(website_id) NOT VALID;


--
-- Name: eav_store_view_bool FK_3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_bool
    ADD CONSTRAINT "FK_3" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key) NOT VALID;


--
-- Name: eav_store_view_float FK_3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_float
    ADD CONSTRAINT "FK_3" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key) NOT VALID;


--
-- Name: eav_store_view_int FK_3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_int
    ADD CONSTRAINT "FK_3" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key) NOT VALID;


--
-- Name: eav_store_view_money FK_3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_money
    ADD CONSTRAINT "FK_3" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key) NOT VALID;


--
-- Name: eav_store_view_multi_select FK_3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_multi_select
    ADD CONSTRAINT "FK_3" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key) NOT VALID;


--
-- Name: eav_store_view_string FK_3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_store_view_string
    ADD CONSTRAINT "FK_3" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key) NOT VALID;


--
-- Name: eav_website_bool FK_3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_bool
    ADD CONSTRAINT "FK_3" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key) NOT VALID;


--
-- Name: eav_website_float FK_3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_float
    ADD CONSTRAINT "FK_3" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key) NOT VALID;


--
-- Name: eav_website_int FK_3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_int
    ADD CONSTRAINT "FK_3" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key) NOT VALID;


--
-- Name: eav_website_money FK_3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_money
    ADD CONSTRAINT "FK_3" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key) NOT VALID;


--
-- Name: eav_website_multi_select FK_3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_multi_select
    ADD CONSTRAINT "FK_3" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key) NOT VALID;


--
-- Name: eav_website_string FK_3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eav_website_string
    ADD CONSTRAINT "FK_3" FOREIGN KEY (attribute_key) REFERENCES public.custom_product_attributes(attribute_key) NOT VALID;


--
-- PostgreSQL database dump complete
--

