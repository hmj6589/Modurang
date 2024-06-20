package com.example.projectapplication

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "testdb", null, 1){
    override fun onCreate(db: SQLiteDatabase?) {
        //TODO("Not yet implemented")
        // SQLite -> 무조건 이거 포함시켜야 함

        // SQLiteDatabase? -> null 일 수도 있으니까 db?.execSQL 이렇게 해줘야해

        db?.execSQL("create table todo_tb (_id integer primary key autoincrement, todo not null)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //TODO("Not yet implemented")
        // SQLite -> 무조건 이거 포함시켜야 함
    }
}