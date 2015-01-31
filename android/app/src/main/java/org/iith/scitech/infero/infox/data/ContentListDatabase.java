package org.iith.scitech.infero.infox.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ContentListDatabase extends SQLiteOpenHelper {
    private static final String DEBUG_TAG = "ContentListDatabase";
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "infoX";

    public static final String TABLE_LANGUAGES = "languages";
    private static final String TABLE_CATEGORIES = "categories";
    private static final String TABLE_CONTENT_TYPES = "content_types";
    public static final String TABLE_CONTENTS = "contents";
    public static final String TABLE_DOWNLOADS = "downloads";

    public static final String ID = "_id";

    private static final String CREATE_TABLE_LANGUAGES = "CREATE TABLE IF NOT EXISTS languages (" +
            "lang_id TEXT PRIMARY KEY," +
            "name TEXT" +
            ");";

    private static final String CREATE_TABLE_CATEGORIES = "CREATE TABLE IF NOT EXISTS categories (" +
            "category_id TEXT PRIMARY KEY," +
            "name TEXT NOT NULL UNIQUE," +
            "description TEXT" +
            ");";

    private static final String CREATE_TABLE_CONTENT_TYPES = "CREATE TABLE IF NOT EXISTS content_types (" +
            "content_type_id TEXT PRIMARY KEY," +
            "name TEXT NOT NULL UNIQUE" +
            ");";

    private static final String CREATE_TABLE_CONTENTS = "CREATE TABLE IF NOT EXISTS contents ("
            + "content_id INTEGER PRIMARY KEY,"
            + "file_name TEXT NOT NULL,"
            + "file_path TEXT NOT NULL,"
            + "time_added TEXT NOT NULL,"
            + "time_expiry TEXT,"
            + "lang_id TEXT,"
            + "category_id TEXT,"
            + "content_type_id TEXT"+
            ");";

    private static final String CREATE_TABLE_DOWNLOADS = "CREATE TABLE IF NOT EXISTS downloads (" +
            "content_id INTEGER NOT NULL," +
            "downloaded TEXT NOT NULL," +
            "deleted INTEGER DEFAULT 0"+
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
        db.execSQL(CREATE_TABLE_DOWNLOADS);
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOWNLOADS);
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

        db.execSQL("insert into content_types (content_type_id, name) values ('tile_education', 'Text Content')");
        db.execSQL("insert into content_types (content_type_id, name) values ('tile_weather', 'Weather Content')");
        db.execSQL("insert into content_types (content_type_id, name) values ('tile_music', 'Audio Content')");
        db.execSQL("insert into content_types (content_type_id, name) values ('tile_video', 'Video Content')");

        db.execSQL("insert into contents (content_id, file_name, file_path, time_added, time_expiry, lang_id, category_id, content_type_id) values (1,'ABC', 'In 1879, Maxwell published a paper on the viscous stresses arising in rarefied gases. In an appendix to the paper, Maxwell proposed his now-famous velocity slip boundary condition.', '20140128224143', '20160128224143', 'EN', 'EDU', 'tile_education')");
        db.execSQL("insert into contents (content_id, file_name, file_path, time_added, time_expiry, lang_id, category_id, content_type_id) values (2,'ABC', 'In 1879, Maxwell published a paper on the viscous stresses arising in rarefied gases titled : On Stresses in Rarified Gases Arising from Inequalities of Temperature. In an appendix to the paper, Maxwell proposed his now-famous velocity slip boundary condition.\n\t-Phil. Trans. R. Soc. Lond. 1879 170 , 231-256, published 1 January 1879\nhttp://rstl.royalsocietypublishing.org/', '20140128224143', '20160128224143', 'EN', 'EDU_HTML', 'tile_education')");
        db.execSQL("insert into contents (content_id, file_name, file_path, time_added, time_expiry, lang_id, category_id, content_type_id) values (3,'ABC', '24;05:00 PM;23rd Jan', '20140128224143', '20140129224143', 'EN', 'PS', 'tile_weather')");
        db.execSQL("insert into contents (content_id, file_name, file_path, time_added, time_expiry, lang_id, category_id, content_type_id) values (4,'tereLiye.mp3', 'tereLiye.mp3', '20140128224143', '20160128224143', 'EN', 'PS', 'tile_music')");
        db.execSQL("insert into contents (content_id, file_name, file_path, time_added, time_expiry, lang_id, category_id, content_type_id) values (5,'movie.mp4', 'movie.mp4', '20140128224143', '20160128224143', 'EN', 'PS', 'tile_video')");

    }
}
