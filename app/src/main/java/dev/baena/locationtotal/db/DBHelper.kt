package dev.baena.locationtotal.db

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import dev.baena.locationtotal.models.Note

const val DATABASE_NAME = "locationtotal"
const val DATABASE_VERSION = 1

class DBHelper(ctx: Context): SQLiteOpenHelper(ctx, DATABASE_NAME, null, DATABASE_VERSION) {

    val TABLE_NAME = "Notes"
    val ID_COLUMN = "id"
    val TEXT_COLUMN = "text"
    val LAT_COLUMN = "lat"
    val LNG_COLUMN = "lng"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_NAME ($ID_COLUMN INTEGER PRIMARY KEY, $TEXT_COLUMN VARCHAR(255), $LAT_COLUMN REAL, $LNG_COLUMN REAL)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    private fun castNote(cursor: Cursor): Note {
        return  Note(
            cursor.getInt(cursor.getColumnIndex(ID_COLUMN)),
            cursor.getString(cursor.getColumnIndex(TEXT_COLUMN)),
            cursor.getDouble(cursor.getColumnIndex(LAT_COLUMN)),
            cursor.getDouble(cursor.getColumnIndex(LNG_COLUMN))
        )
    }

    fun addNote(note: Note): Long {
        val db = writableDatabase
        return db.compileStatement(
            "INSERT INTO $TABLE_NAME($TEXT_COLUMN, $LAT_COLUMN, $LNG_COLUMN) VALUES (?, ?, ?)")
            .apply {
                bindString(1, note.text)
                bindDouble(2, note.lat)
                bindDouble(3, note.lng)
            }.executeInsert()
    }

    fun getNote(id: Int): Note? {
        var note: Note? = null
        val db = writableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE $ID_COLUMN=?",
            arrayOf<String>(id.toString())
        )
        if (cursor.moveToFirst()) note = castNote(cursor)
        return note
    }

    fun getNotes(): List<Note> {
        val notes = mutableListOf<Note>()
        val db = writableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                notes.add(castNote(cursor))
                cursor.moveToNext()
            }
        }
        cursor.close()
        return notes
    }

    fun deleteNote(id: Int?): Int {
        val db = writableDatabase
        return db.compileStatement("DELETE FROM $TABLE_NAME WHERE $ID_COLUMN=?").apply{
            bindString (1, id?.toString())
        }.executeUpdateDelete()
    }

    fun deleteNote(note: Note?): Int {
        return deleteNote(note?.id)
    }
}