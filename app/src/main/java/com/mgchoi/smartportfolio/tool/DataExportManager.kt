package com.mgchoi.smartportfolio.tool

import android.content.Context
import android.os.Environment
import com.google.gson.Gson
import com.mgchoi.smartportfolio.db.MemberDAO
import com.mgchoi.smartportfolio.db.PortfolioDAO
import com.mgchoi.smartportfolio.model.Portfolio
import java.io.File
import java.io.FileWriter

class DataExportManager(private val context: Context) {

    companion object {
        const val DIR_EXPORTS = "exports"
    }

    fun exportPortfolios(): String {
        // 데이터를 가져올 DAO
        val memberDao = MemberDAO(context)
        val portfolioDao = PortfolioDAO(context)
        // Export 할 Member들 가져오기
        val members = memberDao.selectAll(false)
        // Member의 Portfolio 가져오기
        val portfolios = ArrayList<Portfolio>()
        for (member in members) {
            val portfolioArr = portfolioDao.selectAll(member.id)
            for (p in portfolioArr) {
                p.image = null
                portfolios.add(p)
            }
        }

        // Export할 수 있도록 JSON으로 변환
        val memberJson = Gson().toJson(members)
        val portfolioJson = Gson().toJson(portfolios)
        // 하나의 JSON으로 묶기
        val grouped = arrayOf(memberJson, portfolioJson)
        val groupedJson = Gson().toJson(grouped)

        // File에 쓰기
        val dir = getDirectory()
        val filename = generateFilename()
        export(dir, filename, groupedJson)

        // 파일 경로 및 이름 리턴
        return "$dir/$filename"
    }

    private fun getDirectory(): String {
        val externalDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val absolutePath = externalDir?.absolutePath
        return "$absolutePath/$DIR_EXPORTS"
    }

    private fun generateFilename(): String {
        return "${System.currentTimeMillis()}.spf"
    }

    private fun export(dirPath: String, filename: String, data: String) {
        val dir = File(dirPath)
        // 폴더가 없으면 생성
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val file = File("$dirPath/$filename")
        // 파일이 없으면 생성
        if (!file.exists()) {
            file.createNewFile()
        }

        try {
            val writer = FileWriter(file, true)
            writer.write(data)
            writer.flush()
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}