INSERT INTO languages
	(lang_id, name)
VALUES
	("EN", "English")
	,("HI", "Hindi")
	;

INSERT INTO categories
	(category_id, name)
VALUES
	("EDU", "Education")
	,("PS", "Partly Sunny")
	,("ENT", "Entertainment")
	;

INSERT INTO content_types
	(content_type_id, name)
VALUES
	("TXT", "Text")
	,("HTM", "HTML")
	,("VID", "Video")
	,("AUD", "Audio")
	;

INSERT INTO contents
	(content, file_name, file_path, time_expiry, lang_id, category_id, content_type_id)
VALUES
	("Baby doll mai sone di", NULL, NULL, NULL, "EN", "EDU", "TXT")
	,("24;7:00 AM;26-01-2015", NULL, NULL, NULL, "EN", "PS", "TXT")
	,(NULL, "Airtel - Har Ek Friend Zaroori Hota Hai.mp4", "content/hefzhh.mp4", NULL, "HI", "ENT", "VID")
	;

INSERT INTO users
	(name, phone, password)
VALUES
	("Adarsh", "8790663987", "123"),
	("Agma", "8790653987", "123");

INSERT INTO preferences
	(user_id, category_id)
VALUES
	(1, "EDU"),
	(1, "PS");

INSERT INTO downloads
	(user_id, content_id)
VALUES
	(1, 1);