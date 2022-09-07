package com.mgchoi.smartportfolio.db

import android.content.ContentValues
import android.content.Context
import com.mgchoi.smartportfolio.model.Portfolio
import java.io.Closeable

class PortfolioDAO(private val context: Context) {

    fun dropTable() {
        val db = PortfolioDBHelper(context).writableDatabase
        val sql = "DROP TABLE IF EXISTS ${PortfolioDBHelper.TABLE_NAME};"
        db?.execSQL(sql)
        db?.close()
    }

    fun insert(portfolio: Portfolio): Boolean {
        val db = PortfolioDBHelper(context).writableDatabase

        val contentValues = ContentValues()
        contentValues.put(PortfolioDBHelper.COL_MEMBER_ID, portfolio.memberId)
        contentValues.put(PortfolioDBHelper.COL_TITLE, portfolio.title)
        contentValues.put(PortfolioDBHelper.COL_CONTENT, portfolio.content)
        contentValues.put(PortfolioDBHelper.COL_URL, portfolio.url)

        val result = db.insert(PortfolioDBHelper.TABLE_NAME, null, contentValues) != -1L
        db.close()
        return result
    }

    fun selectAll(memberId: Int): ArrayList<Portfolio> {
        val db = PortfolioDBHelper(context).writableDatabase

        val sql = "SELECT * FROM ${PortfolioDBHelper.TABLE_NAME} WHERE memberId = $memberId;"
        val cursor = db.rawQuery(sql, null)

        val data: ArrayList<Portfolio> = arrayListOf()

        while (cursor.count > 0 && cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val mId = cursor.getInt(1)
            val title = cursor.getString(2)
            val content = cursor.getString(3)
            val url = cursor.getString(4)

            data.add(
                Portfolio(id, mId, title, content, url)
            )
        }

        db.close()

        return data
    }

    fun select(id: Int): Portfolio? {
        val db = PortfolioDBHelper(context).writableDatabase

        val sql = "SELECT * FROM ${PortfolioDBHelper.TABLE_NAME} WHERE id = $id;"
        val cursor = db.rawQuery(sql, null)

        while (cursor.moveToNext()) {
            val _id = cursor.getInt(0)
            val mId = cursor.getInt(1)
            val title = cursor.getString(2)
            val content = cursor.getString(3)
            val url = cursor.getString(4)

            db.close()

            return Portfolio(_id, mId, title, content, url)
        }

        db.close()

        return null
    }

    fun isEmpty(): Boolean {
        val db = PortfolioDBHelper(context).writableDatabase

        val sql = "SELECT COUNT(*) FROM ${PortfolioDBHelper.TABLE_NAME};"
        val cursor = db.rawQuery(sql, null)

        while (cursor.moveToNext()) {
            val isEmpty = cursor.getInt(0)

            db.close()

            return isEmpty > 0
        }

        db.close()

        return false
    }

    fun update(portfolio: Portfolio): Int {
        val db = PortfolioDBHelper(context).writableDatabase

        val contentValues = ContentValues()
        contentValues.put(PortfolioDBHelper.COL_MEMBER_ID, portfolio.memberId)
        contentValues.put(PortfolioDBHelper.COL_TITLE, portfolio.title)
        contentValues.put(PortfolioDBHelper.COL_CONTENT, portfolio.content)
        contentValues.put(PortfolioDBHelper.COL_URL, portfolio.url)

        val result =  db.update(
            PortfolioDBHelper.TABLE_NAME,
            contentValues,
            "id = ?",
            arrayOf(portfolio.id.toString())
        )

        db.close()

        return result
    }

    fun delete(id: Int): Int {
        val db = PortfolioDBHelper(context).writableDatabase

        val result = db.delete(
            PortfolioDBHelper.TABLE_NAME,
            "id = ?",
            arrayOf(id.toString())
        )

        db.close()

        return result
    }
}