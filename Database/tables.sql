
-- Languages
CREATE TABLE IF NOT EXISTS languages (
	lang_id CHAR(2) PRIMARY KEY,
	name VARCHAR(255)
);


-- Categories
CREATE TABLE IF NOT EXISTS categories (
	category_id CHAR(3) PRIMARY KEY,
	name VARCHAR(255) NOT NULL UNIQUE,
	description VARCHAR(1024)
);


-- Content type - text, html, video, audio, etc
CREATE TABLE IF NOT EXISTS content_types (
	content_type_id CHAR(3) PRIMARY KEY,
	name VARCHAR(255) NOT NULL UNIQUE
);

-- Contents
CREATE TABLE IF NOT EXISTS contents (
	content_id INT(10) PRIMARY KEY AUTO_INCREMENT,
	content TEXT,
	file_name VARCHAR(255),
	file_path VARCHAR(1024),
	time_added TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	time_expiry TIMESTAMP NULL,
	lang_id CHAR(2),
	category_id CHAR(3),
	content_type_id CHAR(3),
	FOREIGN KEY (lang_id) REFERENCES languages(lang_id) ON UPDATE CASCADE ON DELETE RESTRICT,
	FOREIGN KEY (category_id) REFERENCES categories(category_id) ON UPDATE CASCADE ON DELETE RESTRICT,
	FOREIGN KEY (content_type_id) REFERENCES content_types(content_type_id) ON UPDATE CASCADE ON DELETE RESTRICT
);


-- Users
CREATE TABLE IF NOT EXISTS users (
	user_id INT(10) PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(255) NOT NULL,
	phone CHAR(10) NOT NULL UNIQUE,
	password CHAR(64) NOT NULL	-- 64 bytes assuming sha256 hash
);


-- User - category preferences
CREATE TABLE IF NOT EXISTS preferences (
	user_id INT(10) NOT NULL,
	category_id CHAR(3) NOT NULL,
	FOREIGN KEY (user_id) REFERENCES users(user_id) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (category_id) REFERENCES categories(category_id) ON UPDATE CASCADE ON DELETE RESTRICT
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
	FOREIGN KEY (user_id) REFERENCES users(user_id) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (content_id) REFERENCES contents(content_id) ON UPDATE CASCADE ON DELETE CASCADE,
	UNIQUE KEY(user_id, content_id)
);
