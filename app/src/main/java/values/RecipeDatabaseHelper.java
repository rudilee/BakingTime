package values;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by rudilee on 9/20/17.
 */

public class RecipeDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "recipe_database.db";
    private static final int DATABASE_VERSION = 1;

    public static final String RECIPE_ID = "recipe_id";
    public static final String QUANTITY = "quantity";
    public static final String MEASURE = "measure";
    public static final String INGREDIENT = "ingredient";

    public RecipeDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        final String CREATE_INGREDIENT_TABLE =
                "CREATE TABLE " + INGREDIENT + " (" +
                    RECIPE_ID + " INTEGER NOT NULL," +
                    QUANTITY + " REAL," +
                    MEASURE + " TEXT," +
                    INGREDIENT + " TEXT" +
                ");"
        ;

        database.execSQL(CREATE_INGREDIENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        final String DROP_INGREDIENT_TABLE = "DROP TABLE IF EXISTS " + INGREDIENT;

        database.execSQL(DROP_INGREDIENT_TABLE);

        onCreate(database);
    }
}
