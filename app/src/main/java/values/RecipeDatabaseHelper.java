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

    public static final String RECIPE = "recipe";
    public static final String ID = "id";
    public static final String INGREDIENTS = "ingredients";

    public RecipeDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        final String CREATE_RECIPE_TABLE =
                "CREATE TABLE " + RECIPE + " (" +
                    ID + " INTEGER PRIMARY KEY NOT NULL," +
                    RECIPE + " TEXT," +
                    INGREDIENTS + " TEXT" +
                ");"
        ;

        database.execSQL(CREATE_RECIPE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        final String DROP_RECIPE_TABLE = "DROP TABLE IF EXISTS " + RECIPE;

        database.execSQL(DROP_RECIPE_TABLE);

        onCreate(database);
    }
}
