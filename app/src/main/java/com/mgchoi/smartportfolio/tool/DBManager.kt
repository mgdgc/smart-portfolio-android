package com.mgchoi.smartportfolio.tool

import android.content.Context
import com.google.gson.Gson
import com.mgchoi.smartportfolio.db.MemberDAO
import com.mgchoi.smartportfolio.db.PortfolioDAO
import com.mgchoi.smartportfolio.model.Member
import com.mgchoi.smartportfolio.model.Portfolio

class DBManager(private val context: Context) {

    companion object {
        const val FILE_MEMBER = "member.json"
        const val FILE_PORTFOLIO = "portfolio.json"
    }

    fun initDB() {
        initMemberDB()
        initPortfolioDB()
    }

    fun initMemberDB() {
        val dao = MemberDAO(context)
        if (dao.isEmpty()) {
            insertMemberDB()
        }
    }

    fun initPortfolioDB() {
        val dao = PortfolioDAO(context)
        if (dao.isEmpty()) {
            insertPortfolioDB()
        }
    }

    fun insertMemberDB() {
        val json = JsonAssetReader.readStringFromJson(context.assets, FILE_MEMBER)
        val members = Gson().fromJson(json, Array<Member>::class.java)
        val dao = MemberDAO(context)
        members.forEach {
            dao.insert(it)
        }
    }

    fun insertPortfolioDB() {
        val json = JsonAssetReader.readStringFromJson(context.assets, FILE_PORTFOLIO)
        val portfolios = Gson().fromJson(json, Array<Portfolio>::class.java)
        val dao = PortfolioDAO(context)
        portfolios.forEach {
            dao.insert(it)
        }
    }

    fun resetDB() {
        resetMemberDB()
        resetPortfolioDB()
    }

    fun resetMemberDB() {
        val dao = MemberDAO(context)
        dao.deleteAll()
    }

    fun resetPortfolioDB() {
        val dao = PortfolioDAO(context)
        dao.deleteAll()
    }

}