package com.mgchoi.smartportfolio.db

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.database.getStringOrNull
import com.mgchoi.smartportfolio.model.ViewStyle
import com.mgchoi.smartportfolio.model.Member
import com.mgchoi.smartportfolio.value.IntentFilterActions

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
        return insert(member.name, member.image, member.url, member.viewStyle, member.destroyable)
    }

    fun insert(
        name: String,
        image: String?,
        url: String?,
        viewStyle: ViewStyle,
        destroyable: Boolean = true
    ): Boolean {

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

            // Member가 추가된 것을 broadcast
            context.sendBroadcast(Intent(IntentFilterActions.ACTION_MEMBER_ADDED))

            return result
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    fun selectAll(includesInitialDB: Boolean = true): ArrayList<Member> {
        try {
            val db = MemberDBHelper(context).writableDatabase

            var sql = "SELECT * FROM ${MemberDBHelper.TABLE_NAME}"
            val cursor = if (includesInitialDB) {
                db.rawQuery(sql, null)
            } else {
                sql += " WHERE destroyable = ?;"
                db.rawQuery(sql, arrayOf(false.toString()))
            }

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

            // Portfolio가 수정된 것을 broadcast
            context.sendBroadcast(Intent(IntentFilterActions.ACTION_MEMBER_UPDATE))

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

            // Member가 제거된 것을 broadcast
            val intent = Intent(IntentFilterActions.ACTION_MEMBER_REMOVED).apply {
                putExtra(MemberDBHelper.COL_ID, id)
            }
            context.sendBroadcast(intent)

            result

        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    fun deleteAll() {
        val db = MemberDBHelper(context).writableDatabase

        // 데이터 모두 삭제
        db.delete(MemberDBHelper.TABLE_NAME, null, null)

        // AUTOINCREMENT 초기화
        db.execSQL("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name = '${MemberDBHelper.TABLE_NAME}'; ")

        db.close()

        // Member가 제거된 것을 broadcast
        val intent = Intent(IntentFilterActions.ACTION_MEMBER_REMOVED)
        context.sendBroadcast(intent)
    }
}