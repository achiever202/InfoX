package org.iith.scitech.infero.infox.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ContentListDatabase extends SQLiteOpenHelper {
    private static final String DEBUG_TAG = "ContentListDatabase";
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "infoX";

    public static final String TABLE_CONTENTS = "contents";
    public static final String TABLE_LANGUAGES = "languages";
    private static final String TABLE_CATEGORIES = "categories";
    private static final String TABLE_CONTENT_TYPES = "content_types";

    public static final String ID = "_id";

    private static final String CREATE_TABLE_LANGUAGES = "CREATE TABLE IF NOT EXISTS languages (" +
            "lang_id CHAR(2) PRIMARY KEY," +
            "name VARCHAR(255)" +
            ");";

    private static final String CREATE_TABLE_CATEGORIES = "CREATE TABLE IF NOT EXISTS categories (" +
            "category_id CHAR(3) PRIMARY KEY," +
            "name VARCHAR(255) NOT NULL UNIQUE," +
            "description VARCHAR(1024)" +
            ");";

    private static final String CREATE_TABLE_CONTENT_TYPES = "CREATE TABLE IF NOT EXISTS content_types (" +
            "content_type_id CHAR(3) PRIMARY KEY," +
            "name VARCHAR(255) NOT NULL UNIQUE" +
            ");";

    private static final String CREATE_TABLE_CONTENTS = "CREATE TABLE IF NOT EXISTS contents ("
            + "content_id INT(10) PRIMARY KEY AUTO_INCREMENT,"
            + "file_name VARCHAR(255) NOT NULL,"
            + "file_path VARCHAR(1024) NOT NULL,"
            + "time_added TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "time_expiry TIMESTAMP,"
            + "lang_id CHAR(2),"
            + "category_id CHAR(3),"
            + "content_type_id CHAR(3),"
            + "FOREIGN KEY (lang_id) REFERENCES languages(lang_id) ON UPDATE CASCADE ON DELETE RESTRICT,"
            + "FOREIGN KEY (category_id) REFERENCES categories(category_id) ON UPDATE CASCADE ON DELETE RESTRICT,"
            + "FOREIGN KEY (content_type_id) REFERENCES content_types(content_type_id) ON UPDATE CASCADE ON DELETE RESTRICT" +
            ");";


    public ContentListDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_LANGUAGES);
        db.execSQL(CREATE_TABLE_CATEGORIES);
        db.execSQL(CREATE_TABLE_CONTENT_TYPES);
        db.execSQL(CREATE_TABLE_CONTENTS);
        seedData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DEBUG_TAG, "Upgrading database. Existing contents will be lost. ["
                + oldVersion + "]->[" + newVersion + "]");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LANGUAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTENT_TYPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTENTS);
        onCreate(db);
    }

    /**
     * Create sample data to use
     * 
     * @param db
     *            The open database
     */
    private void seedData(SQLiteDatabase db) {
        db.execSQL("insert into languages (lang_id, name) values ('EN', 'English')");
        db.execSQL("insert into languages (lang_id, name) values ('HI', 'Hindi')");

        db.execSQL("insert into categories (category_id, name, description) values ('EDU', 'Education', 'Educational Content')");

        db.execSQL("insert into content_types (content_type_id, name) values ('TXT', 'Text Content')");
        db.execSQL("insert into content_types (content_type_id, name) values ('HTM', 'HTML Content')");
        db.execSQL("insert into content_types (content_type_id, name) values ('AUD', 'Audio Content')");
        db.execSQL("insert into content_types (content_type_id, name) values ('VID', 'Video Content')");

        db.execSQL("insert into contents (file_name, file_path, time_expiry, lang_id, category_id, content_type_id) values ('ABC', 'Text Content', '2010-04-28 22:41:43', 'EN', 'EDU', 'TXT')");

    }
}
