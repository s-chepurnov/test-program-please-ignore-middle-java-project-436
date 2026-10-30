create table if not exists categories (
    id serial primary key,
    slug text not null unique,
    name text not null
);

create table if not exists products (
    id serial primary key,
    slug text not null unique,
    name text not null,
    description text not null,
    price_amount integer not null check (price_amount >= 0),
    category_id integer not null references categories (id),
    image_url text,
    available boolean not null default true
);

create table if not exists users (
    id serial primary key,
    email text not null unique,
    password_hash text not null
);

create table if not exists promos (
    id serial primary key,
    title text not null,
    text text not null,
    product_id integer not null unique references products (id)
);

create table if not exists orders (
    id serial primary key,
    user_id integer not null references users (id),
    status text not null default 'paid',
    shipping_method text not null,
    recipient_name text not null,
    phone text not null,
    address text,
    total_amount integer not null,
    created_at timestamptz not null default now()
);

create table if not exists order_items (
    id serial primary key,
    order_id integer not null references orders (id) on delete cascade,
    product_id integer not null,
    product_name text not null,
    price_amount integer not null,
    quantity integer not null check (quantity > 0)
);

create index if not exists orders_user_id_idx on orders (user_id);
create index if not exists order_items_order_id_idx on order_items (order_id);
