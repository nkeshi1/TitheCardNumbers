package com.example.tithecardnumbers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DatabaseManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TithingCards.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "tithe_cards_info";
    public static final String CARD_NO = "_id";
    public static final String CARD_HOLDER = "card_holder";
    public static final String CARD_YEAR = "card_year";
    private SQLiteDatabase database;
    private int cardNoPos;
    private int holderNamePos;
    private int cardYearPos;

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String tableCreation = "CREATE TABLE "+ TABLE_NAME + " ( " +
                CARD_NO + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                CARD_HOLDER+" TEXT NOT NULL, " +
                CARD_YEAR+" TEXT NOT NULL);";
        db.execSQL(tableCreation);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }

    //Methods associated with the table...
    public void addCardNumber(TitheCardRegister titheCard){
        ContentValues values = new ContentValues();
        values.put(CARD_HOLDER, titheCard.getHolderName());
        values.put(CARD_YEAR, titheCard.getCardYear());
        database = getWritableDatabase();
        database.insert(TABLE_NAME, null, values);
    }

    public ArrayList<Map<String, String>> getCardInfoById(String cardid){
        database = getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, new String[]{CARD_NO, CARD_HOLDER, CARD_YEAR},
                CARD_NO +" = ?", new String[]{cardid}, null, null, null);

        Map<String, String> cardRecord = new HashMap<>();
        ArrayList<Map<String, String>> cardDetail = new ArrayList<>();
        cardNoPos = cursor.getColumnIndex(CARD_NO);
        holderNamePos = cursor.getColumnIndex(CARD_HOLDER);
        cardYearPos = cursor.getColumnIndex(CARD_YEAR);

        if(cursor.moveToFirst()){
            cardRecord.put(CARD_NO, cursor.getString(cardNoPos));
            cardRecord.put(CARD_HOLDER, cursor.getString(holderNamePos));
            cardRecord.put(CARD_YEAR, cursor.getString(cardYearPos));

            cardDetail.add(cardRecord);
        }
        cursor.close();
        return cardDetail;
    }

    public void updateCardNumber(TitheCardRegister titheCardRegister){
        database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CARD_NO, titheCardRegister.getCardNo());
        values.put(CARD_HOLDER, titheCardRegister.getHolderName());
        values.put(CARD_YEAR, titheCardRegister.getCardYear());
        database.update(TABLE_NAME, values, CARD_NO +" = ? ", new String[]{String.valueOf(titheCardRegister.getCardNo())});
    }

    public ArrayList<TitheCardRegister> getAllCardInfo(){
        database = getReadableDatabase();
        String[] column = {CARD_NO, CARD_HOLDER, CARD_YEAR};
        Cursor cursor = database.query(true, TABLE_NAME, column, null, null, null, null, null, null);

        cardNoPos = cursor.getColumnIndex(CARD_NO);
        holderNamePos = cursor.getColumnIndex(CARD_HOLDER);
        cardYearPos = cursor.getColumnIndex(CARD_YEAR);

        ArrayList<TitheCardRegister> titheCardDetails = new ArrayList<>();
        while(cursor.moveToNext()){
            TitheCardRegister titheCardRegister = new TitheCardRegister();
            titheCardRegister.setCardNo(cursor.getInt(cardNoPos));
            titheCardRegister.setHolderName(cursor.getString(holderNamePos));
            titheCardRegister.setCardYear(cursor.getInt(cardYearPos));

            titheCardDetails.add(titheCardRegister);
        }
        cursor.close();
        return titheCardDetails;
    }
}
