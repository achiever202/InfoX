
-- Languages
CREATE TABLE IF NOT EXISTS languages (
	lang_id CHAR(2) PRIMARY KEY,
	name VARCHAR(255)
);


-- Categories
CREATE TABLE IF NOT EXISTS categories (
	category_id INT(10) PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(255) NOT NULL UNIQUE,
	description VARCHAR(1024)
);


-- Content type - text, html, video, audio, etc
CREATE TABLE IF NOT EXISTS content_types (
	content_type_id INT(10) PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(255) NOT NULL UNIQUE
);

-- Contents
CREATE TABLE IF NOT EXISTS contents (
	content_id INT(10) PRIMARY KEY AUTO_INCREMENT,
	file_name VARCHAR(255) NOT NULL,
	file_path VARCHAR(1024) NOT NULL,
	time_added TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	time_expiry TIMESTAMP,
	lang_id CHAR(2),
	category_id INT(10),
	content_type_id INT(10),
	FOREIGN KEY (lang_id) REFERENCES languages(lang_id) ON UPDATE CASCADE ON DELETE RESTRICT,
	FOREIGN KEY (category_id) REFERENCES categories(category_id) ON UPDATE CASCADE ON DELETE RESTRICT,
	FOREIGN KEY (content_type_id) REFERENCES content_types(content_type_id) ON UPDATE CASCADE ON DELETE RESTRICT
);


-- Users
CREATE TABLE IF NOT EXISTS users (
	user_id INT(10) PRIMARY KEY AUTO_INCREMENT,
	username VARCHAR(255) NOT NULL UNIQUE,
	phone CHAR(10) NOT NULL UNIQUE,
	password CHAR(64) NOT NULL	-- 64 bytes assuming sha256 hash
);

-- Peers
CREATE TABLE IF NOT EXISTS peers (
	peer1 INT(10) NOT NULL,
	peer2 INT(10) NOT NULL,
	approval BIT,
	FOREIGN KEY (peer1) REFERENCES users(user_id) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (peer2) REFERENCES users(user_id) ON UPDATE CASCADE ON DELETE CASCADE
);

-- Downloads
CREATE TABLE IF NOT EXISTS downloads (
	user_id INT(10) NOT NULL,
	content_id INT(10) NOT NULL,
	downloaded TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	deleted BIT DEFAULT 0,
	FOREIGN KEY (user_id) REFERENCES users(user_id) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (content_id) REFERENCES contents(content_id) ON UPDATE CASCADE ON DELETE RESTRICT
);
