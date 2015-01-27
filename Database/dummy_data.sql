INSERT INTO languages
	(lang_id, name)
VALUES
	("EN", "English");

INSERT INTO categories
	(category_id, name)
VALUES
	("EDU", "Education"),
	("PS", "Partly Sunny");

INSERT INTO content_types
	(content_type_id, name)
VALUES
	("TXT", "Text"),
	("HTM", "HTML");

INSERT INTO contents
	(content, file_name, file_path, time_expiry, lang_id, category_id, content_type_id)
VALUES
	("Baby doll mai sone di", NULL, NULL, NULL, "EN", "EDU", "TXT"),
	("24;7:00 AM;26-01-2015", NULL, NULL, NULL, "EN", "PS", "TXT");

INSERT INTO users
	(user_id, name, phone, password)
VALUES
	("1", "Adarsh", "8790663987", "123"),
	("2", "Agma", "8790653987", "123");