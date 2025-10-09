//package com.example.newsroom.data.local.sqlite
//
//import android.content.Context
//import android.database.sqlite.SQLiteDatabase
//import android.database.sqlite.SQLiteOpenHelper
//
///*
//
//Later, when you view an article or fetch a batch,
//you can insert rows into articles_fts to demonstrate FTS search.
//This keeps Room for bookmarks and raw SQLite for search — both skills.
//
//*/
//class HistoryDbHelper(context: Context) :
//    SQLiteOpenHelper(context, "history.db", null, 1) {
//
//    override fun onCreate(db: SQLiteDatabase) {
//        db.execSQL("""
//            CREATE TABLE IF NOT EXISTS searches (
//                _id INTEGER PRIMARY KEY AUTOINCREMENT,
//             /*  query TEXT NOT NULL,  */
//                `query` TEXT NOT NULL,
//                ts INTEGER NOT NULL
//            );
//        """.trimIndent())
//
//
//        db.execSQL("""
//        /*  CREATE VIRTUAL TABLE IF NOT EXISTS articles_fts USING fts5(  */
//            CREATE VIRTUAL TABLE IF NOT EXISTS articles_fts USING fts4(
//             /* title, content, url UNINDEXED, tokenize = 'unicode61'  */
//                title, content, url
//            )
//            /* tokenize='unicode61' --->    NOT WORKING */
//            ;
//        """.trimIndent())
//    }
//
//    override fun onUpgrade(db: SQLiteDatabase, old: Int, newV: Int) {
//        // simple demo
//        db.execSQL("DROP TABLE IF EXISTS searches")
//        db.execSQL("DROP TABLE IF EXISTS articles_fts")
//        onCreate(db)
//    }
//}
