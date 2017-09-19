package udacity.android.bakingtime;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import udacity.android.bakingtime.api.AsyncTaskCompleteListener;
import udacity.android.bakingtime.api.FetchRecipesTask;
import udacity.android.bakingtime.api.Recipe;

public class MainActivity extends AppCompatActivity
        implements RecipesListAdapter.RecipeCardClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String RECIPES_LIST_KEY = "recipes-list";

    private final RecipesListAdapter recipesListAdapter = new RecipesListAdapter(this);
    private List<Recipe> recipesList;

    @BindView(R.id.recipes_list_view) RecyclerView recipesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        recipesListView.setAdapter(recipesListAdapter);

        if (savedInstanceState == null) {
            new FetchRecipesTask(this, new FetchRecipesTaskCompleteListener()).execute();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(RECIPES_LIST_KEY,
                (ArrayList<? extends Parcelable>) recipesList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        recipesList = savedInstanceState.getParcelableArrayList(RECIPES_LIST_KEY);

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (recipesList != null) {
            recipesListAdapter.setRecipesList(recipesList);
        }
    }

    @Override
    public void onClick(int recipeId) {
        Intent showStepsIntent = new Intent(this, RecipeStepListActivity.class);
        startActivity(showStepsIntent);

        Log.d(TAG, "Recipe ID:" + recipeId);
    }

    private class FetchRecipesTaskCompleteListener
            implements AsyncTaskCompleteListener<List<Recipe>> {

        @Override
        public void onTaskComplete(List<Recipe> result) {
            recipesList = result;

            recipesListAdapter.setRecipesList(recipesList);
        }
    }
}
