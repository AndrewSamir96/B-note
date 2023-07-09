package com.example.bnote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class NoteDB {

    public static final String KEY_ROWID = "_id";
    public static final String KEY_TITLE = "note_title";
    public static final String KEY_CONTENT = "note_content";
    public static final String KEY_COLOR = "note_color";
    public static final String KEY_DATE = "note_date";
    public static final String KEY_IS_PINNED = "is_pinned";

    //DATABASE VARIABLES
    public static final String DATABASE_NAME = "NotesDB";
    public static final String DATABASE_TABLE = "AllNotesTable";
    public static final int DATABASE_VERSION = 1;
    private DBHelper ourHelper;
    private final Context ourContext;
    private SQLiteDatabase ourDatabase;

    public NoteDB(Context context){
        ourContext = context;
    }
    private class DBHelper extends SQLiteOpenHelper{

        //this constructor will create the new Database
        public DBHelper(Context context){
            super(context,DATABASE_NAME,null,DATABASE_VERSION);
        }

        //will only run on first time or database not found
        @Override
        public void onCreate(SQLiteDatabase db) {

            String sqlQuery = "CREATE TABLE " + DATABASE_TABLE + " (" +
                    KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_TITLE + " TEXT NOT NULL, "+
                    KEY_CONTENT + " TEXT NOT NULL, "+
                    KEY_COLOR + " TEXT DEFAULT \"#333333\", "+
                    KEY_DATE + " TEXT NOT NULL, "+
                    KEY_IS_PINNED + " INTEGER DEFAULT 0);";
            db.execSQL(sqlQuery);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    public NoteDB open() throws SQLException{
        ourHelper = new DBHelper(ourContext);
        ourDatabase = ourHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        ourHelper.close();
    }

    public long createNote(String title, String content,String color, String date, int pinned){
        ContentValues cv = new ContentValues();
        cv.put(KEY_TITLE, title);
        cv.put(KEY_CONTENT, content);
        cv.put(KEY_COLOR, color);
        cv.put(KEY_DATE, date);
        cv.put(KEY_IS_PINNED, pinned);
        return ourDatabase.insert(DATABASE_TABLE,null,cv);
    }

    public ArrayList<Note> getAllNotes(){
        String [] columns = new String[] {KEY_ROWID, KEY_TITLE, KEY_CONTENT, KEY_COLOR, KEY_DATE, KEY_IS_PINNED};
        //TODO: Order By isPinned and then by descending order of id
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, KEY_IS_PINNED + " DESC, " + KEY_ROWID + " DESC");

        ArrayList<Note> allNotes = new ArrayList<>();
        int columnID = c.getColumnIndex(KEY_ROWID);
        int columnTitle = c.getColumnIndex(KEY_TITLE);
        int columnContent = c.getColumnIndex(KEY_CONTENT);
        int columnColor = c.getColumnIndex(KEY_COLOR);
        int columnDate = c.getColumnIndex(KEY_DATE);
        int columnPinned = c.getColumnIndex(KEY_IS_PINNED);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
            allNotes.add(new Note(Integer.parseInt(c.getString(columnID)), c.getString(columnTitle),
                    c.getString(columnContent),c.getString(columnColor), c.getString(columnDate), Integer.parseInt(c.getString(columnPinned))));
        }
        c.close();
        if(allNotes == null){
            return null;
        }
        else {
            return allNotes;
        }
    }

    public Note getNote(int rowId){
        if (rowId == -1){
            return null;
        }
        String [] columns = new String[] {KEY_ROWID, KEY_TITLE, KEY_CONTENT, KEY_COLOR, KEY_DATE, KEY_IS_PINNED};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns,KEY_ROWID + " = " + rowId, null, null, null, null);

        int columnID = c.getColumnIndex(KEY_ROWID);
        int columnTitle = c.getColumnIndex(KEY_TITLE);
        int columnContent = c.getColumnIndex(KEY_CONTENT);
        int columnColor = c.getColumnIndex(KEY_COLOR);
        int columnDate = c.getColumnIndex(KEY_DATE);
        int columnPinned = c.getColumnIndex(KEY_IS_PINNED);
        c.moveToFirst();
        return new Note(Integer.parseInt(c.getString(columnID)), c.getString(columnTitle),
                c.getString(columnContent),c.getString(columnColor), c.getString(columnDate), Integer.parseInt(c.getString(columnPinned)));
    }

    public long deleteNote(int rowId){
        String row = rowId+"";
        return ourDatabase.delete(DATABASE_TABLE, KEY_ROWID + "=?",new String[]{row});
    }
    public long updateNote(int rowId, String title, String content, String color, String date, int isPinned){
        String row = rowId + "";
        ContentValues cv = new ContentValues();
        cv.put(KEY_TITLE, title);
        cv.put(KEY_CONTENT, content);
        cv.put(KEY_COLOR, color);
        cv.put(KEY_IS_PINNED, isPinned);
        cv.put(KEY_DATE, date);

        return ourDatabase.update(DATABASE_TABLE, cv, KEY_ROWID + "=?", new String[]{row});
    }
}
