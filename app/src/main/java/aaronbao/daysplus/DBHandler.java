package aaronbao.daysplus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Aaron on 09/04/2016.
 */
public class DBHandler extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Daysplus3.db";
    public static final String TABLE_USER = "user";
    public static final String USER_ID = "id";
    public static final String USER_NAME = "user_name";

    public static final String TABLE_DAY = "days";
    public static final String DAY_ID = "id";
    public static final String DAY_USER_ID = "user_id";
    public static final String DAY_STORY = "story";
    public static final String DAY_TITLE = "title";
    public static final String DAY_DATE = "created_at";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "(" + USER_ID + " INTEGER PRIMARY KEY,"
                + USER_NAME + " TEXT)";
        String CREATE_DAY_TABLE = "CREATE TABLE " + TABLE_DAY + "(" + DAY_ID + " INTEGER PRIMARY KEY,"
                + DAY_USER_ID + " INTEGER, " + DAY_TITLE + " TEXT, " + DAY_STORY + " TEXT, " + DAY_DATE
                + "timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY(" + DAY_USER_ID +") REFERENCES " + TABLE_USER + "(" + USER_ID + "))";
        String INSERT_USER = "INSERT INTO user (id, user_name) VALUES (1, 'admin')";
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_DAY_TABLE);
        db.execSQL(INSERT_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DAY);
        onCreate(db);
    }

    public boolean addDay(Integer user_id, String title, String story){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", user_id);
        values.put("title", title);
        values.put("story", story);
        db.insert("days", null, values);
        return true;
    }

    public Cursor getDays(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from days where id = " + id + "", null);
        return res;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_DAY);
        return numRows;
    }

    public boolean updateDay(Integer id, String title, String story) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("story", story);
        db.update("days", values, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public Integer deleteDay (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("days", "id = ?", new String[] {Integer.toString(id)} );
    }

    public ArrayList<String> getAllDays() {
        ArrayList<String> all_days = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from days", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            all_days.add(res.getString(res.getColumnIndex(DAY_TITLE)));
            res.moveToNext();
        }
        return all_days;
    }
}
