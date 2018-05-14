package com.home.nattapop.todo.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.home.nattapop.todo.dao.model.Task;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    /***********************************
     * Variables
     ***********************************/
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "tasks_db";

    /***********************************
     * DBHelper Functions
     ***********************************/
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Task.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Task.TABLE_NAME);
        onCreate(db);
    }

    /***********************************
     * App CRUD Functions
     ***********************************/
    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + Task.TABLE_NAME + " ORDER BY " +
                Task.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Task task = new Task();
                task.setId(cursor.getInt(cursor.getColumnIndex(Task.COLUMN_ID)));
                task.setTask(cursor.getString(cursor.getColumnIndex(Task.COLUMN_TASK)));
                task.setTimestamp(cursor.getString(cursor.getColumnIndex(Task.COLUMN_TIMESTAMP)));
                tasks.add(task);
            } while (cursor.moveToNext());
        }
        db.close();
        return tasks;
    }

    public int getTasksCount() {
        String countQuery = "SELECT  * FROM " + Task.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public Task getTask(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Task.TABLE_NAME,
                new String[]{Task.COLUMN_ID, Task.COLUMN_TASK, Task.COLUMN_TIMESTAMP},
                Task.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Task task = new Task(
                cursor.getInt(cursor.getColumnIndex(Task.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Task.COLUMN_TASK)),
                cursor.getString(cursor.getColumnIndex(Task.COLUMN_TIMESTAMP)));
        cursor.close();

        return task;
    }

    public long insertTask(String task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Task.COLUMN_TASK, task);
        long id = db.insert(Task.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public int updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Task.COLUMN_TASK, task.getTask());
        return db.update(Task.TABLE_NAME, values, Task.COLUMN_ID + " = ?",
                new String[]{String.valueOf(task.getId())});
    }

    public void deleteTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Task.TABLE_NAME, Task.COLUMN_ID + " = ?",
                new String[]{String.valueOf(task.getId())});
        db.close();
    }

}
