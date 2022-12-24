package com.mgchoi.smartportfolio.tool

import android.content.Context
import com.google.gson.Gson
import com.mgchoi.smartportfolio.db.MemberDAO
import com.mgchoi.smartportfolio.db.PortfolioDAO
import com.mgchoi.smartportfolio.model.Member
import com.mgchoi.smartportfolio.model.Portfolio
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader

class DataImportManager(private val context: Context) {

    fun importPortfolios(filePath: String) {
        // file path 에서 파일 읽어오기
        val json = readFile(filePath) ?: throw FileNotFoundException()

        // Array<String> 으로 변환
        val strArray = Gson().fromJson(json, Array<String>::class.java)

        val members: Array<Member>
        val portfolios: Array<Portfolio>
        try {
            // array[0]: Member, array[1]: Portfolio
            val memberJson = strArray[0]
            val portfolioJson = strArray[1]

            // JSON을 Array로 변환
            members = Gson().fromJson(memberJson, Array<Member>::class.java)
            portfolios = Gson().fromJson(portfolioJson, Array<Portfolio>::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }

        // Member DB에 import
        val memberDao = MemberDAO(context)
        members.forEach {
            memberDao.insert(it)
        }

        // Portfolio DB에 import
        val portfolioDao = PortfolioDAO(context)
        portfolios.forEach {
            portfolioDao.insert(it)
        }

    }

    private fun readFile(filePath: String): String? {
        val file = File(filePath)
        // 파일이 없으면 오류
        if (!file.exists()) {
            throw FileNotFoundException()
        }

        try {
            val fileReader = FileReader(file)
            val bufferReader = BufferedReader(fileReader)

            // 파일 읽기
            val stringBuffer = StringBuffer()
            var line: String?
            do {
                line = bufferReader.readLine()
                if (line == null) break
                stringBuffer.append(line)
            } while (true)

            bufferReader.close()

            return stringBuffer.toString()

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

}