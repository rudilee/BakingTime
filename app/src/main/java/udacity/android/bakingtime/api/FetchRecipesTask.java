package udacity.android.bakingtime.api;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.text.Html;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import udacity.android.bakingtime.R;
import values.RecipeContentProvider;
import values.RecipeDatabaseHelper;

/**
 * Created by rudilee on 8/30/17.
 */

public class FetchRecipesTask extends AsyncTask<String, Integer, List<Recipe>> {
    private Context context;
    private AsyncTaskCompleteListener<List<Recipe>> listener;

    public FetchRecipesTask(Context context, AsyncTaskCompleteListener<List<Recipe>> listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected List<Recipe> doInBackground(String... strings) {
        List<Recipe> recipes = null;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.json_base_url))
                .addConverterFactory(MoshiConverterFactory.create())
                .build();

        BakingService service = retrofit.create(BakingService.class);

        Call<List<Recipe>> caller = service.listRecipes(context.getString(R.string.json_path));
        try {
            recipes = caller.execute().body();

            if (recipes != null && !recipes.isEmpty()) {
                ContentResolver contentResolver = context.getContentResolver();
                contentResolver.delete(RecipeContentProvider.CONTENT_URI, null, null);

                for (Recipe recipe : recipes) {
                    ContentValues values = new ContentValues();
                    values.put(RecipeDatabaseHelper.ID, recipe.id);
                    values.put(RecipeDatabaseHelper.RECIPE, recipe.name);

                    String ingredients = "";

                    for (Ingredient i : recipe.ingredients) {
                        String quantity = new DecimalFormat("#.#").format(i.quantity);

                        ingredients += context.getString(
                                R.string.ingredient,
                                quantity,
                                i.measure,
                                i.ingredient
                        );
                    }

                    values.put(RecipeDatabaseHelper.INGREDIENTS, ingredients);

                    if (values.size() > 0) {
                        contentResolver.insert(RecipeContentProvider.CONTENT_URI, values);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return recipes;
    }

    @Override
    protected void onPostExecute(List<Recipe> recipes) {
        super.onPostExecute(recipes);

        listener.onTaskComplete(recipes);
    }
}
