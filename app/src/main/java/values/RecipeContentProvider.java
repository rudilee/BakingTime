package values;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;

public class RecipeContentProvider extends ContentProvider {
    private static final String PATH = "/ingredient";
    private static final int INGREDIENT_ID = 1;

    public static final String PROVIDER_NAME = "recipe_content_provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + PATH);

    private SQLiteDatabase database;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(PROVIDER_NAME, PATH, INGREDIENT_ID);
    }

    public RecipeContentProvider() {
    }

    @Override
    public boolean onCreate() {
        RecipeDatabaseHelper databaseHelper = new RecipeDatabaseHelper(getContext());
        database = databaseHelper.getWritableDatabase();

        return database != null;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        if (uriMatcher.match(uri) == INGREDIENT_ID) {
            return "vnd.android.cursor.dir/vnd.bakingtime.ingredient";
        }

        return null;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(RecipeDatabaseHelper.INGREDIENT);

        if (uriMatcher.match(uri) == INGREDIENT_ID) {
            queryBuilder.setProjectionMap(new HashMap<String, String>());
        }

        return queryBuilder.query(
                database,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        if (values == null) {
            throw new NullPointerException("Ingredient can not be empty");
        }

        long result = database.insert(RecipeDatabaseHelper.INGREDIENT, null, values);
        if (result == -1) {
            throw new SQLException("Failed to add new Ingredient");
        }

        return null;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int affectedCount = 0;

        if (uriMatcher.match(uri) == INGREDIENT_ID) {
            affectedCount = database.delete(RecipeDatabaseHelper.INGREDIENT, selection,
                    selectionArgs);
        }

        return affectedCount;
    }
}
