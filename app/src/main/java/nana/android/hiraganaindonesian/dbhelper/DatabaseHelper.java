package nana.android.hiraganaindonesian.dbhelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import nana.android.hiraganaindonesian.entity.Huruf;
import nana.android.hiraganaindonesian.entity.Kamus;

public class DatabaseHelper extends SQLiteOpenHelper {
    private final static String TAG = "DatabaseHelper";
    private final Context myContext;
    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;
    private String pathToSaveDBFile;

    public DatabaseHelper(Context context, String filePath) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
        pathToSaveDBFile = new StringBuffer(filePath).append("/").append(DATABASE_NAME).toString();
    }

    public void prepareDatabase() throws IOException {
        boolean dbExist = checkDataBase();
        if (dbExist) {
            Log.d(TAG, "Database exists.");
            int currentDBVersion = getVersionId();
            if (DATABASE_VERSION > currentDBVersion) {
                Log.d(TAG, "Database version is higher than old.");
                deleteDb();
                try {
                    copyDataBase();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        } else {
            try {
                copyDataBase();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private boolean checkDataBase() {
        boolean checkDB = false;
        try {
            File file = new File(pathToSaveDBFile);
            checkDB = file.exists();
        } catch (SQLiteException e) {
            Log.d(TAG, e.getMessage());
        }
        return checkDB;
    }

    private void copyDataBase() throws IOException {
        OutputStream os = new FileOutputStream(pathToSaveDBFile);
        InputStream is = myContext.getAssets().open("sqlite/" + DATABASE_NAME);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }
        is.close();
        os.flush();
        os.close();
    }

    public void deleteDb() {
        File file = new File(pathToSaveDBFile);
        if (file.exists()) {
            file.delete();
            Log.d(TAG, "Database deleted.");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            try {
                copyDataBase();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    public List<Huruf> getHuruf() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT id, huruf, value FROM Huruf";
        Cursor cursor = db.rawQuery(query, null);
        List<Huruf> list = new ArrayList<Huruf>();
        while (cursor.moveToNext()) {
            Huruf huruf = new Huruf();
            //Log.d("ID Table",String.valueOf(cursor.getInt(0)));
            huruf.setId(cursor.getInt(0));
            huruf.setHuruf(cursor.getString(1));
            huruf.setValue(cursor.getDouble(2));

            list.add(huruf);
        }
        db.close();
        return list;
    }


    public List<Kamus> getKata() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT * FROM Kamus";
        Cursor cursor = db.rawQuery(query, null);
        List<Kamus> list = new ArrayList<Kamus>();
        while (cursor.moveToNext()) {
            Kamus kata = new Kamus();
            //Log.d("ID Table",String.valueOf(cursor.getInt(0)));
            kata.setId(cursor.getInt(0));
            kata.setJepang(cursor.getString(1));
            kata.setIndonesia(cursor.getString(2));

            list.add(kata);
        }
        db.close();
        return list;
    }


    private int getVersionId() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT version_id FROM dbVersion";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        int v = cursor.getInt(0);
        db.close();
        return v;
    }
}