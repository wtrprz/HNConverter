import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "CurrencyDB", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        //Creamos las tablas
        db.execSQL("CREATE TABLE rates (id INTEGER PRIMARY KEY AUTOINCREMENT, from_code TEXT, to_code TEXT, rate REAL)")
        db.execSQL("CREATE TABLE conversions (id INTEGER PRIMARY KEY AUTOINCREMENT, from_code TEXT, to_code TEXT, amount REAL, result REAL, date TEXT, is_favorite INTEGER DEFAULT 0)")

        //Insertamos los primeros datos
        db.execSQL("INSERT INTO rates (from_code, to_code, rate) VALUES ('HNL', 'USD', 0.040)")
        db.execSQL("INSERT INTO rates (from_code, to_code, rate) VALUES ('GTQ', 'USD', 0.13)")
        db.execSQL("INSERT INTO rates (from_code, to_code, rate) VALUES ('CRC', 'USD', 0.0019)")
        db.execSQL("INSERT INTO rates (from_code, to_code, rate) VALUES ('NIO', 'USD', 0.027)")
        db.execSQL("INSERT INTO rates (from_code, to_code, rate) VALUES ('SVC', 'USD', 0.11)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS rates")
        db.execSQL("DROP TABLE IF EXISTS conversions")
        onCreate(db)
    }
}