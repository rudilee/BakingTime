package udacity.android.bakingtime.api;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import udacity.android.bakingtime.R;

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
