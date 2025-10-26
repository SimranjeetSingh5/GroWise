package com.simranjeet.growise.presentation.ui.helpers

import android.util.Log
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.simranjeet.growise.data.database.AppDatabase

fun RoomDatabase.Builder<AppDatabase>.addDebugger(): RoomDatabase.Builder<AppDatabase> {
    return this.addCallback(object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Log.d("AppDatabase", "Database CREATED from scratch")
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            Log.d("AppDatabase", "Database OPENED (existing)")

            // Check table contents on open
            val userCursor = db.query("SELECT COUNT(*) FROM user")
            userCursor.moveToFirst()
            val userCount = userCursor.getInt(0)
            userCursor.close()

            val txnCursor = db.query("SELECT COUNT(*) FROM transactions")
            txnCursor.moveToFirst()
            val txnCount = txnCursor.getInt(0)
            txnCursor.close()

            Log.d("AppDatabase", "User count: $userCount, Transaction count: $txnCount")
        }
    })
}