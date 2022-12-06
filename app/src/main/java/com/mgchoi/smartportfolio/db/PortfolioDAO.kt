package com.mgchoi.smartportfolio.db

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import com.mgchoi.smartportfolio.model.Portfolio
import com.mgchoi.smartportfolio.value.IntentFilterActions

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

        // Portfolio가 추가된 것을 broadcast
        context.sendBroadcast(Intent(IntentFilterActions.ACTION_PORTFOLIO_ADDED))

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

    fun selectLast(memberId: Int): Portfolio? {
        val portfolios = selectAll(memberId)
        return if (portfolios.size <= 0) null else portfolios[0]
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

            return isEmpty <= 0
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

        val result = db.update(
            PortfolioDBHelper.TABLE_NAME,
            contentValues,
            "id = ?",
            arrayOf(portfolio.id.toString())
        )

        db.close()

        // Portfolio가 수정된 것을 broadcast
        context.sendBroadcast(Intent(IntentFilterActions.ACTION_PORTFOLIO_UPDATE))

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

        // Portfolio가 제거된 것을 broadcast
        val intent = Intent(IntentFilterActions.ACTION_PORTFOLIO_REMOVED).apply {
            putExtra(PortfolioDBHelper.COL_ID, id)
        }
        context.sendBroadcast(intent)

        return result
    }

    fun deleteAll() {
        val db = PortfolioDBHelper(context).writableDatabase

        // 데이터 모두 삭제
        db.delete(PortfolioDBHelper.TABLE_NAME, null, null)

        // AUTOINCREMENT 초기화
        db.execSQL("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name = '${PortfolioDBHelper.TABLE_NAME}'; ")

        db.close()

        // Portfolio가 제거된 것을 broadcast
        val intent = Intent(IntentFilterActions.ACTION_PORTFOLIO_REMOVED)
        context.sendBroadcast(intent)
    }
}