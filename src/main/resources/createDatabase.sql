create table users(
    id int generated by default as identity primary key,
    username varchar(100) not null,
    email varchar(255) not null,
    password varchar(255) not null
);

CREATE TABLE posts (
                       id int generated by default as identity primary key,
                       title VARCHAR(255) NOT NULL,
                       text TEXT NOT NULL,
                       image BYTEA,
                       user_id INTEGER NOT NULL,
                       created_at TIMESTAMP,
                       FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE followers (
                                 id int generated by default as identity primary key,
                                 sender_id INTEGER NOT NULL REFERENCES users(id),
                                 receiver_id INTEGER NOT NULL REFERENCES users(id),
                                 created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE friendships (
                             id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                             user1_id INTEGER NOT NULL REFERENCES users(id),
                             user2_id INTEGER NOT NULL REFERENCES users(id),
                             created_at TIMESTAMP DEFAULT NOW()
);