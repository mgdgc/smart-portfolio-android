package com.mgchoi.smartportfolio.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.mgchoi.smartportfolio.model.Portfolio
import com.mgchoi.smartportfolio.value.DBName

class PortfolioDBHelper(private val context: Context) : SQLiteOpenHelper(context, DBName.name, null, 1) {

    companion object {
        const val TABLE_NAME = "Portfolio"
        const val COL_ID = "id"
        const val COL_MEMBER_ID = "memberId"
        const val COL_TITLE = "title"
        const val COL_CONTENT = "content"
        const val COL_URL = "url"
    }

    init {
        onCreate(writableDatabase)
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val sql = "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_MEMBER_ID INTEGER," +
                "$COL_TITLE TEXT," +
                "$COL_CONTENT TEXT," +
                "$COL_URL TEXT" +
                ");"
        db?.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val sql = "DROP TABLE IF EXISTS $TABLE_NAME;"
        db?.execSQL(sql)
        onCreate(db)
    }

}