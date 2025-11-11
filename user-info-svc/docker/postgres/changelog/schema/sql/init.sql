create schema if not exists "user_info";

create type user_role as enum('volunteer', 'moderator');

create table if not exists "user_info".users(
    id bigserial primary key,
    login text not null unique,
    password text not null,
    personal_email text unique,
    mai_email text unique,
    full_name text,
    institute text,
    student_group text,
    birth_date date,
    clothing_size text,
    social jsonb not null default '{}'::jsonb,
    contact_email text,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    role user_role default 'volunteer'
);

create extension if not exists pg_trgm;

create index if not exists users_full_name_trgm_idx on "user_info".users using gin (lower(full_name) gin_trgm_ops);
create index if not exists users_student_group_ci_idx on "user_info".users (lower(student_group));
