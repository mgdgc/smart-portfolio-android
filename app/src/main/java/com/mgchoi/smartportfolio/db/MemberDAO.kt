package com.mgchoi.smartportfolio.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.mgchoi.smartportfolio.ViewStyle
import com.mgchoi.smartportfolio.model.Member
import com.mgchoi.smartportfolio.value.DBName

class MemberDAO(private val context: Context) : SQLiteOpenHelper(context, DBName.name, null, 1) {

    companion object {
        private const val TABLE_NAME = "Member"
        private const val COL_NAME = "name"
        private const val COL_IMAGE = "image"
        private const val COL_URL = "url"
        private const val COL_VIEW_STYLE = "viewStyle"
        private const val COL_DESTROYABLE = "destroyable"
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

    fun insert(member: Member): Boolean {
        return insert(member.name, member.image, member.url, member.viewStyle, member.destroyable)
    }

    fun insert(
        name: String,
        image: Int,
        url: String,
        viewStyle: ViewStyle,
        destroyable: Boolean = true
    ): Boolean {
        val contentValues = ContentValues()
        contentValues.put(COL_NAME, name)
        contentValues.put(COL_IMAGE, image)
        contentValues.put(COL_URL, url)
        contentValues.put(COL_VIEW_STYLE, viewStyle.rawValue)
        contentValues.put(COL_DESTROYABLE, if (destroyable) 1 else 0)

        return writableDatabase.insert(TABLE_NAME, null, contentValues) != -1L
    }

    fun selectAll(): ArrayList<Member> {
        val sql = "SELECT * FROM $TABLE_NAME;"
        val cursor = writableDatabase.rawQuery(sql, null)

        val data: ArrayList<Member> = arrayListOf()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val name = cursor.getString(1)
            val image = cursor.getInt(2)
            val url = cursor.getString(3)
            val viewStyle = cursor.getInt(4)
            val destroyable = cursor.getInt(5) == 1

            data.add(
                Member(id, name, image, url, ViewStyle.of(viewStyle), destroyable)
            )
        }

        return data
    }

    fun select(id: Int): Member? {
        val sql = "SELECT * FROM $TABLE_NAME WHERE id = $id;"
        val cursor = writableDatabase.rawQuery(sql, null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val name = cursor.getString(1)
            val image = cursor.getInt(2)
            val url = cursor.getString(3)
            val viewStyle = cursor.getInt(4)
            val destroyable = cursor.getInt(5) == 1

            return Member(id, name, image, url, ViewStyle.of(viewStyle), destroyable)
        }

        return null
    }

    fun isDestroyable(id: Int): Boolean {
        val sql = "SELECT $COL_DESTROYABLE FROM $TABLE_NAME WHERE id = $id;"
        val cursor = writableDatabase.rawQuery(sql, null)

        while (cursor.moveToNext()) {
            return cursor.getInt(5) == 1
        }

        return false
    }

    fun update(member: Member): Int {
        val contentValues = ContentValues()
        contentValues.put(COL_NAME, member.name)
        contentValues.put(COL_IMAGE, member.image)
        contentValues.put(COL_URL, member.url)
        contentValues.put(COL_VIEW_STYLE, member.viewStyle.rawValue)
        contentValues.put(COL_DESTROYABLE, if (member.destroyable) 1 else 0)
        return writableDatabase.update(TABLE_NAME, contentValues, "id = ?", arrayOf(member.id.toString()))
    }

    fun delete(id: Int): Int {
        return if (isDestroyable(id)) {
            writableDatabase.delete(TABLE_NAME, "id = ?", arrayOf(id.toString()))
        } else -1
    }

}