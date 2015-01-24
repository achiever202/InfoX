package org.iith.scitech.infero.infox.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class ContentListProvider extends ContentProvider {

    private ContentListDatabase mDB;

    private static final String AUTHORITY = "org.iith.scitech.infero.infox.data.ContentListProvider";
    public static final int CONTENTS = 100;
    public static final int CONTENT_ID = 110;

    private static final String CONTENTS_BASE_PATH = "contents";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + CONTENTS_BASE_PATH);

    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/mt-CONTENT";
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/mt-CONTENT";

    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, CONTENTS_BASE_PATH, CONTENTS);
        sURIMatcher.addURI(AUTHORITY, CONTENTS_BASE_PATH + "/#", CONTENT_ID);
    }

    @Override
    public boolean onCreate() {
        mDB = new ContentListDatabase(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(ContentListDatabase.TABLE_CONTENTS);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
        case CONTENT_ID:
            queryBuilder.appendWhere(ContentListDatabase.ID + "="
                    + uri.getLastPathSegment());
            break;
        case CONTENTS:
            // no filter
            break;
        default:
            throw new IllegalArgumentException("Unknown URI");
        }

        Cursor cursor = queryBuilder.query(mDB.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDB.getWritableDatabase();
        int rowsAffected = 0;
        switch (uriType) {
        case CONTENTS:
            rowsAffected = sqlDB.delete(ContentListDatabase.TABLE_CONTENTS,
                    selection, selectionArgs);
            break;
        case CONTENT_ID:
            String id = uri.getLastPathSegment();
            if (TextUtils.isEmpty(selection)) {
                rowsAffected = sqlDB.delete(ContentListDatabase.TABLE_CONTENTS,
                        ContentListDatabase.ID + "=" + id, null);
            } else {
                rowsAffected = sqlDB.delete(ContentListDatabase.TABLE_CONTENTS,
                        selection + " and " + ContentListDatabase.ID + "=" + id,
                        selectionArgs);
            }
            break;
        default:
            throw new IllegalArgumentException("Unknown or Invalid URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }

    @Override
    public String getType(Uri uri) {
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
        case CONTENTS:
            return CONTENT_TYPE;
        case CONTENT_ID:
            return CONTENT_ITEM_TYPE;
        default:
            return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        if (uriType != CONTENTS) {
            throw new IllegalArgumentException("Invalid URI for insert");
        }
        SQLiteDatabase sqlDB = mDB.getWritableDatabase();
        long newID = sqlDB
                .insert(ContentListDatabase.TABLE_CONTENTS, null, values);
        if (newID > 0) {
            Uri newUri = ContentUris.withAppendedId(uri, newID);
            getContext().getContentResolver().notifyChange(uri, null);
            return newUri;
        } else {
            throw new SQLException("Failed to insert row into " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDB.getWritableDatabase();

        int rowsAffected;

        switch (uriType) {
        case CONTENT_ID:
            String id = uri.getLastPathSegment();
            StringBuilder modSelection = new StringBuilder(ContentListDatabase.ID
                    + "=" + id);

            if (!TextUtils.isEmpty(selection)) {
                modSelection.append(" AND " + selection);
            }

            rowsAffected = sqlDB.update(ContentListDatabase.TABLE_CONTENTS,
                    values, modSelection.toString(), null);
            break;
        case CONTENTS:
            rowsAffected = sqlDB.update(ContentListDatabase.TABLE_CONTENTS,
                    values, selection, selectionArgs);
            break;
        default:
            throw new IllegalArgumentException("Unknown URI");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }
}
