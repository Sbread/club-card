CREATE TABLE IF NOT EXISTS ClubMembers (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    firstName VARCHAR(255),
    lastName VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    phoneNumber VARCHAR(20),
    privilege VARCHAR(20),
    isLocked boolean NOT NULL,
    role TEXT[]
);

CREATE TABLE IF NOT EXISTS RefreshTokens (
    id SERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    expiry_date TIMESTAMP WITH TIME ZONE NOT NULL,
    club_member_id INTEGER,
    FOREIGN KEY (club_member_id) REFERENCES ClubMembers(id)
);

CREATE TABLE IF NOT EXISTS BlacklistTokens (
    id SERIAL PRIMARY KEY,
    token VARCHAR(255)
);