package com.aa183.ariskawijaya;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DatabaseHandler extends SQLiteOpenHelper {

    private final static int DATABASE_VERSION = 2;
    private final static String DATABASE_NAME = "db_buku";
    private final static String TABLE_BUKU = "t_buku";
    private final static String KEY_ID_BUKU = "ID_Buku";
    private final static String KEY_JUDUL = "Judul";
    private final static String KEY_TGL = "Tanggal";
    private final static String KEY_GAMBAR = "Gambar";
    private final static String KEY_PENULIS = "Penulis";
    private final static String KEY_ULASAN_BUKU = "Ulasan_Buku";
    private SimpleDateFormat sdFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault());
    private Context context;

    public DatabaseHandler(Context ctx){
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = ctx;
    }

    @Override
    //MEMBANGUN DATABASE
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_BUKU = "CREATE TABLE " + TABLE_BUKU
                + "(" + KEY_ID_BUKU + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_JUDUL + " TEXT, " + KEY_TGL + " DATE, "
                + KEY_GAMBAR + " TEXT, " + KEY_PENULIS + " TEXT, "
                + KEY_ULASAN_BUKU + " TEXT);";

        db.execSQL(CREATE_TABLE_BUKU);
        inisialisasiBukuAwal(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_BUKU;
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public void tambahBuku(Buku dataBuku) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(KEY_JUDUL, dataBuku.getJudul());
        cv.put(KEY_TGL, sdFormat.format(dataBuku.getTanggal()));
        cv.put(KEY_PENULIS, dataBuku.getPenulis());
        cv.put(KEY_ULASAN_BUKU, dataBuku.getUlasanBuku());
        db.insert(TABLE_BUKU, null, cv);
        db.close();
    }

    public void tambahBuku(Buku dataBuku, SQLiteDatabase db) {
        ContentValues cv = new ContentValues();

        cv.put(KEY_JUDUL, dataBuku.getJudul());
        cv.put(KEY_TGL, sdFormat.format(dataBuku.getTanggal()));
        cv.put(KEY_PENULIS, dataBuku.getPenulis());
        cv.put(KEY_ULASAN_BUKU, dataBuku.getUlasanBuku());
        db.insert(TABLE_BUKU, null, cv);
    }


    public void editBuku(Buku dataBuku) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(KEY_JUDUL, dataBuku.getJudul());
        cv.put(KEY_TGL, sdFormat.format(dataBuku.getTanggal()));
        cv.put(KEY_PENULIS, dataBuku.getPenulis());
        cv.put(KEY_ULASAN_BUKU, dataBuku.getUlasanBuku());
        db.update(TABLE_BUKU, cv, KEY_ID_BUKU + "=?", new String[]{String.valueOf(dataBuku.getIdBuku())});
        db.close();
    }

    public void hapusBuku(int idBuku) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_BUKU, KEY_ID_BUKU + "=?", new String[]{String.valueOf(idBuku)});
        db.close();
    }

    public ArrayList<Buku>getAllBuku(){
        ArrayList<Buku> dataBuku = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_BUKU;
        SQLiteDatabase db = getReadableDatabase();
        Cursor csr = db.rawQuery(query, null);
        if (csr.moveToFirst()){
            do {
                Date tempDate = new Date();
                try {
                    tempDate = sdFormat.parse(csr.getString(2));
                } catch (ParseException er){
                    er.printStackTrace();
                }

                Buku tempBuku = new Buku(
                        csr.getInt(0),
                        csr.getString(1),
                        tempDate,
                        csr.getString(3),
                        csr.getString(4),
                        csr.getString(5)
                );

                dataBuku.add(tempBuku);
            }
            while (csr.moveToNext());
        }
        return dataBuku;
    }

    private String storeImageFile(int id){
        String location;
        Bitmap image = BitmapFactory.decodeResource(context.getResources(), id);
        location = InputActivity.saveImageToInternalStorage(image, context);
        return location;
    }

    private void inisialisasiBukuAwal(SQLiteDatabase db){
        int idBuku = 0;
        Date tempDate = new Date();

        //Menambah data buku ke - 1
        try {
            tempDate = sdFormat.parse("28/05/2020 06:00");
        } catch (ParseException er){
            er.printStackTrace();
        }

        Buku buku1 = new Buku(
                idBuku,
                "SENI membuat HIDUP jadi lebih RINGAN",
                tempDate,
                storeImageFile(R.drawable.gambar1),
                "Francine Jay",
                "Buku yang akan mengajarkan kita untuk melepaskan beban satu per satu."
        );
        tambahBuku(buku1, db);
        idBuku++;

        //Menambah data buku ke - 2
        try {
            tempDate = sdFormat.parse("28/05/2020 07:00");
        } catch (ParseException er){
            er.printStackTrace();
        }

        Buku buku2 = new Buku(
                idBuku,
                "Y - Menggapai Arti Kesuksesan Dan Kebahagiaan Yang Sesungguhnya",
                tempDate,
                storeImageFile(R.drawable.gambar2),
                "Billy Boen",
                "Buku yang akan mengajak kita untuk mengetahui purpose dalam diri sesungguhnya. Sehingga membuat hidup jadi lebih berarti. "
        );
        tambahBuku(buku2, db);
    }
}
