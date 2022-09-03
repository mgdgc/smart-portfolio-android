package com.mgchoi.smartportfolio.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.mgchoi.smartportfolio.model.Portfolio
import com.mgchoi.smartportfolio.value.DBName

class PortfolioDAO(private val context: Context) : SQLiteOpenHelper(context, DBName.name, null, 1) {

    companion object {
        private const val TABLE_NAME = "Portfolio"
        private const val COL_ID = "id"
        private const val COL_MEMBER_ID = "memberId"
        private const val COL_TITLE = "title"
        private const val COL_CONTENT = "content"
        private const val COL_URL = "url"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val sql = "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "memberId INTEGER," +
                "title TEXT," +
                "content TEXT," +
                "url TEXT" +
                ");"
        db?.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val sql = "DROP TABLE IF EXISTS $TABLE_NAME;"
        db?.execSQL(sql)
        onCreate(db)
    }

    fun insert(portfolio: Portfolio): Boolean {
        val contentValues = ContentValues()
        contentValues.put(COL_MEMBER_ID, portfolio.memberId)
        contentValues.put(COL_TITLE, portfolio.title)
        contentValues.put(COL_CONTENT, portfolio.content)
        contentValues.put(COL_URL, portfolio.url)

        return writableDatabase.insert(TABLE_NAME, null, contentValues) != -1L
    }

    fun selectAll(memberId: Int): ArrayList<Portfolio> {
        val sql = "SELECT * FROM ${TABLE_NAME} WHERE memberId = $memberId;"
        val cursor = writableDatabase.rawQuery(sql, null)

        val data: ArrayList<Portfolio> = arrayListOf()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val mId = cursor.getInt(1)
            val title = cursor.getString(2)
            val content = cursor.getString(3)
            val url = cursor.getString(4)

            data.add(
                Portfolio(id, mId, title, content, url)
            )
        }

        return data
    }

    fun select(id: Int): Portfolio? {
        val sql = "SELECT * FROM ${TABLE_NAME} WHERE id = $id;"
        val cursor = writableDatabase.rawQuery(sql, null)

        while (cursor.moveToNext()) {
            val _id = cursor.getInt(0)
            val mId = cursor.getInt(1)
            val title = cursor.getString(2)
            val content = cursor.getString(3)
            val url = cursor.getString(4)

            return Portfolio(_id, mId, title, content, url)
        }

        return null
    }

    fun update(portfolio: Portfolio): Int {
        val contentValues = ContentValues()
        contentValues.put(COL_MEMBER_ID, portfolio.memberId)
        contentValues.put(COL_TITLE, portfolio.title)
        contentValues.put(COL_CONTENT, portfolio.content)
        contentValues.put(COL_URL, portfolio.url)

        return writableDatabase.update(
            TABLE_NAME,
            contentValues,
            "id = ?",
            arrayOf(portfolio.id.toString())
        )
    }

    fun delete(id: Int): Int {
        return writableDatabase.delete(TABLE_NAME, "id = ?", arrayOf(id.toString()))
    }

}