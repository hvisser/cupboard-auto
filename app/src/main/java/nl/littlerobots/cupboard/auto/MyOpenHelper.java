package nl.littlerobots.cupboard.auto;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import nl.littlerobots.cupboard.auto.model.Cheese;
import nl.qbusict.cupboard.CupboardBuilder;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;
import static nl.qbusict.cupboard.CupboardFactory.setCupboard;

public class MyOpenHelper extends SQLiteOpenHelper {

    static {
        setCupboard(new CupboardBuilder().registerEntityConverterFactory(new AutoEntityConverterFactory()).build());
        cupboard().register(Cheese.class);
    }

    public MyOpenHelper(Context context, String name) {
        super(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        cupboard().withDatabase(db).createTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        cupboard().withDatabase(db).upgradeTables();
    }
}
