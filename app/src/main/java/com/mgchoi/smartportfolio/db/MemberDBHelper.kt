package com.mgchoi.smartportfolio.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.mgchoi.smartportfolio.ViewStyle
import com.mgchoi.smartportfolio.model.Member
import com.mgchoi.smartportfolio.value.DBName

class MemberDBHelper(private val context: Context) : SQLiteOpenHelper(context, DBName.name, null, 1) {

    companion object {
        const val TABLE_NAME = "Member"
        const val COL_NAME = "name"
        const val COL_IMAGE = "image"
        const val COL_URL = "url"
        const val COL_VIEW_STYLE = "viewStyle"
        const val COL_DESTROYABLE = "destroyable"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val sql = "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "image INTEGER," +
                "url TEXT," +
                "viewStyle INTEGER," +
                "destroyable INTEGER DEFAULT 1" + // (1: true, 0: false)
                ");"
        db?.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val sql = "DROP TABLE IF EXISTS $TABLE_NAME;"
        db?.execSQL(sql)
        onCreate(db)
    }

}