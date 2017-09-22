package values;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;

public class RecipeContentProvider extends ContentProvider {
    private static final String PATH = "/recipe";
    private static final int MATCH_DIRECTORY = 1;
    private static final int MATCH_ITEM = 2;

    public static final String PROVIDER_NAME = "recipe_content_provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + PATH);

    private SQLiteDatabase database;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(PROVIDER_NAME, PATH, MATCH_DIRECTORY);
        uriMatcher.addURI(PROVIDER_NAME, PATH + "/#", MATCH_ITEM);
    }

    public static Uri contentUriWithId(int id) {
        return Uri.parse("content://" + PROVIDER_NAME + PATH + "/" + id);
    }

    private String[] prependWithId(String[] arguments, String id) {
        ArrayDeque<String> whereArgs = arguments != null ?
                new ArrayDeque<>(Arrays.asList(arguments)) :
                new ArrayDeque<String>();

        whereArgs.addFirst(id);

        return whereArgs.toArray(new String[0]);
    }

    @Override
    public boolean onCreate() {
        RecipeDatabaseHelper databaseHelper = new RecipeDatabaseHelper(getContext());
        database = databaseHelper.getWritableDatabase();

        return database != null;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case MATCH_DIRECTORY:
                return "vnd.android.cursor.dir/vnd.bakingtime.recipe";
            case MATCH_ITEM:
                return "vnd.android.cursor.item/vnd.bakingtime.recipe";
            default:
                return null;
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(RecipeDatabaseHelper.RECIPE);

        switch (uriMatcher.match(uri)) {
            case MATCH_DIRECTORY:
                queryBuilder.setProjectionMap(new HashMap<String, String>());
                break;
            case MATCH_ITEM:
                queryBuilder.appendWhere(RecipeDatabaseHelper.ID + " = ?");

                selectionArgs = prependWithId(selectionArgs, uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Content URI do not match!");
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
            throw new NullPointerException("Recipe fields can not be empty");
        }

        long result = database.insert(RecipeDatabaseHelper.RECIPE, null, values);
        if (result == -1) {
            throw new SQLException("Failed to add new Ingredient");
        }

        return ContentUris.withAppendedId(CONTENT_URI,
                values.getAsInteger(RecipeDatabaseHelper.ID));
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int affectedCount;

        switch (uriMatcher.match(uri)) {
            case MATCH_DIRECTORY:
                affectedCount = database.delete(RecipeDatabaseHelper.RECIPE, selection,
                        selectionArgs);
                break;
            case MATCH_ITEM:
                String whereClause = RecipeDatabaseHelper.ID + " = ?" +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");

                affectedCount = database.delete(
                        RecipeDatabaseHelper.RECIPE,
                        whereClause,
                        prependWithId(selectionArgs, uri.getPathSegments().get(1))
                );
                break;
            default:
                throw new IllegalArgumentException("Content URI do not match!");
        }

        return affectedCount;
    }
}
