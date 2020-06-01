CREATE TABLE expense.category
(
    id integer NOT NULL DEFAULT nextval('expense.category_id_seq'::regclass),
    name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    parent_id integer,
    CONSTRAINT category_pkey PRIMARY KEY (id),
    CONSTRAINT uk_46ccwnsi9409t36lurvtyljak UNIQUE (name),
    CONSTRAINT fk_category_parent_id FOREIGN KEY (parent_id)
        REFERENCES expense.category (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE expense.expense
(
    id bigint NOT NULL DEFAULT nextval('expense.expense_id_seq'::regclass),
    count integer NOT NULL,
    local_date date NOT NULL,
    note character varying(255) COLLATE pg_catalog."default",
    total_price numeric(19,2) NOT NULL,
    unit_price numeric(19,2) NOT NULL,
    category_id integer NOT NULL,
    CONSTRAINT expense_pkey PRIMARY KEY (id),
    CONSTRAINT fk_category_id FOREIGN KEY (category_id)
        REFERENCES expense.category (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)