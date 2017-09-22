package udacity.android.bakingtime;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import udacity.android.bakingtime.api.AsyncTaskCompleteListener;
import udacity.android.bakingtime.api.FetchRecipesTask;
import udacity.android.bakingtime.api.Recipe;

public class MainActivity extends AppCompatActivity
        implements RecipeListAdapter.RecipeCardClickListener {
    private static final String RECIPES_LIST_KEY = "recipes-list";

    private final RecipeListAdapter recipeListAdapter = new RecipeListAdapter(this);
    private List<Recipe> recipeList;

    @BindView(R.id.recipes_list_view) RecyclerView recipesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        recipesListView.setAdapter(recipeListAdapter);

        if (savedInstanceState == null) {
            new FetchRecipesTask(this, new FetchRecipesTaskCompleteListener()).execute();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(
                RECIPES_LIST_KEY,
                (ArrayList<? extends Parcelable>) recipeList
        );
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        recipeList = savedInstanceState.getParcelableArrayList(RECIPES_LIST_KEY);

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (recipeList != null) {
            recipeListAdapter.setRecipeList(recipeList);
        }
    }

    @Override
    public void onClick(Recipe recipe) {
            Intent showStepsIntent = new Intent(this, RecipeActivity.class);
            showStepsIntent.putExtra(RecipeActivity.RECIPE_KEY, recipe);

            startActivity(showStepsIntent);
    }

    private class FetchRecipesTaskCompleteListener
            implements AsyncTaskCompleteListener<List<Recipe>> {

        @Override
        public void onTaskComplete(List<Recipe> result) {
            recipeList = result;

            recipeListAdapter.setRecipeList(recipeList);
        }
    }
}
