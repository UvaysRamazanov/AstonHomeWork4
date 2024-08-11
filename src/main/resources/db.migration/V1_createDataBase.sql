CREATE TABLE Author (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    genre VARCHAR(255),
    age INT CHECK (age > 0),
    user_type VARCHAR(50) NOT NULL,
    pen_name VARCHAR(255),
    biography TEXT NOT NULL -- Специфическое поле для Author
);

CREATE TABLE Person (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    age INT CHECK (age > 0),
    email VARCHAR(255) UNIQUE,
    user_type VARCHAR(50) NOT NULL, -- discriminator column
    pen_name VARCHAR(255) -- специфическое поле для Authors, может быть NULL
);


CREATE TABLE Book (
    id bigserial PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    author_id int references Author(id) on delete set null,
    year INT NOT NULL
);

CREATE TABLE Book_Person (
    person_id bigserial,
    book_id bigserial,
    PRIMARY KEY (person_id, book_id),
    foreign key (person_id) references Person(id) on delete cascade,
    foreign key (book_id) references Book(id) on delete cascade
);

CREATE SEQUENCE user_sequence START WITH 1 INCREMENT BY 1;

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    genre VARCHAR(255),
    age INT CHECK (age > 0),
    user_type VARCHAR(50) NOT NULL, -- discriminator column
        -- Add any other common fields that could be needed for all types
        -- Additional fields specific to each type can be nullable
    pen_name VARCHAR(255), -- specific for Authors
    editor_specialty VARCHAR(255) -- specific for Editors
);

CREATE TABLE Publication (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    author_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    editor_id BIGINT REFERENCES users(id) ON DELETE SET NULL
);
