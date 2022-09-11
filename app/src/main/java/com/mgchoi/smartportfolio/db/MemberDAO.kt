package com.mgchoi.smartportfolio.db

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.core.database.getStringOrNull
import com.mgchoi.smartportfolio.model.ViewStyle
import com.mgchoi.smartportfolio.model.Member

class MemberDAO(private val context: Context) {

    fun dropTable() {
        try {
            val db = MemberDBHelper(context).writableDatabase
            val sql = "DROP TABLE IF EXISTS ${MemberDBHelper.TABLE_NAME};"
            db?.execSQL(sql)
            db.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun insert(member: Member): Boolean {

        Log.e(member.viewStyle.toString(), "It's me")
        return insert(member.name, member.image, member.url, member.viewStyle, member.destroyable)
    }

    fun insert(
        name: String,
        image: String?,
        url: String?,
        viewStyle: ViewStyle,
        destroyable: Boolean = true
    ): Boolean {

        Log.e("여기다 이 등신아", "It's me")
        val db = MemberDBHelper(context).writableDatabase

        val contentValues = ContentValues()
        contentValues.put(MemberDBHelper.COL_NAME, name)
        contentValues.put(MemberDBHelper.COL_IMAGE, image)
        contentValues.put(MemberDBHelper.COL_URL, url)
        contentValues.put(MemberDBHelper.COL_VIEW_STYLE, viewStyle.rawValue)
        contentValues.put(MemberDBHelper.COL_DESTROYABLE, if (destroyable) 1 else 0)

        try {
            val result = db.insert(MemberDBHelper.TABLE_NAME, null, contentValues) != -1L
            db.close()
            return result
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    fun selectAll(): ArrayList<Member> {
        try {
            val db = MemberDBHelper(context).writableDatabase

            val sql = "SELECT * FROM ${MemberDBHelper.TABLE_NAME};"
            val cursor = db.rawQuery(sql, null)

            val data: ArrayList<Member> = arrayListOf()
            while (cursor.moveToNext()) {
                val id = cursor.getInt(0)
                val name = cursor.getString(1)
                val image = cursor.getString(2)
                val url = cursor.getString(3)
                val viewStyle = cursor.getInt(4)
                val destroyable = cursor.getInt(5) == 1

                data.add(
                    Member(id, name, image, url, ViewStyle.of(viewStyle), destroyable)
                )
            }

            db.close()

            return data
        } catch (e: Exception) {
            e.printStackTrace()
            return arrayListOf()
        }
    }

    fun select(id: Int): Member? {
        try {

            val db = MemberDBHelper(context).writableDatabase

            val sql = "SELECT * FROM ${MemberDBHelper.TABLE_NAME} WHERE id = $id;"
            val cursor = db.rawQuery(sql, null)

            while (cursor.moveToNext()) {
                val _id = cursor.getInt(0)
                val name = cursor.getString(1)
                val image = cursor.getStringOrNull(2)
                val url = cursor.getString(3)
                val viewStyle = cursor.getInt(4)
                val destroyable = cursor.getInt(5) == 1

                db.close()

                return Member(_id, name, image, url, ViewStyle.of(viewStyle), destroyable)
            }

            db.close()

            return null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun isEmpty(): Boolean {
        try {
            val db = MemberDBHelper(context).writableDatabase

            val sql = "SELECT COUNT(*) FROM ${MemberDBHelper.TABLE_NAME};"
            val cursor = db.rawQuery(sql, null)

            while (cursor.moveToNext()) {
                val isEmpty = cursor.getInt(0)

                db.close()

                return isEmpty <= 0
            }
            db.close()

            return false
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun update(member: Member): Int {
        try {

            val db = MemberDBHelper(context).writableDatabase

            val contentValues = ContentValues()
            contentValues.put(MemberDBHelper.COL_NAME, member.name)
            contentValues.put(MemberDBHelper.COL_IMAGE, member.image)
            contentValues.put(MemberDBHelper.COL_URL, member.url)
            contentValues.put(MemberDBHelper.COL_VIEW_STYLE, member.viewStyle.rawValue)
            contentValues.put(MemberDBHelper.COL_DESTROYABLE, if (member.destroyable) 1 else 0)

            val result = db.update(
                MemberDBHelper.TABLE_NAME,
                contentValues,
                "id = ?",
                arrayOf(member.id.toString())
            )
            db.close()
            return result

        } catch (e: Exception) {
            e.printStackTrace()
            return -1
        }
    }

    fun delete(id: Int): Int {
        return try {
            val db = MemberDBHelper(context).writableDatabase

            val result = db.delete(MemberDBHelper.TABLE_NAME, "id = ?", arrayOf(id.toString()))

            db.close()

            result

        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }
}