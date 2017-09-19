package udacity.android.bakingtime;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import udacity.android.bakingtime.api.Recipe;

/**
 * An activity representing a list of RecipeSteps. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipeStepActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecipeActivity extends AppCompatActivity
        implements RecipeStepListAdapter.RecipeStepItemClickListener {
    private static final String RECIPE_KEY = "recipe-detail";

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean twoPaneMode;

    @BindView(R.id.ingredients_list) TextView ingredientsList;
    @BindView(R.id.recipe_step_list) RecyclerView recyclerView;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        if (intent.hasExtra(RECIPE_KEY)) {
            Recipe recipe = intent.getParcelableExtra(RECIPE_KEY);

            setTitle(recipe.name);
            toolbar.setTitle(recipe.name);

            String ingredients = recipe
                    .ingredients
                    .stream()
                    .map(i -> getString(R.string.ingredient, i.ingredient, i.quantity, i.measure))
                    .collect(Collectors.joining("\n"));

            ingredientsList.setText(getString(R.string.ingredients_list, ingredients));
            recyclerView.setAdapter(new RecipeStepListAdapter(recipe.steps, this));
        }

        if (findViewById(R.id.recipe_step_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPaneMode = true;
        }
    }

    @Override
    public void onClick(int stepId) {
        if (twoPaneMode) {
            Bundle arguments = new Bundle();
            arguments.putString(RecipeStepFragment.ARG_ITEM_ID,
                    String.valueOf(stepId));
            RecipeStepFragment fragment = new RecipeStepFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipe_step_detail_container, fragment)
                    .commit();
        } else {
            Context context = getBaseContext();
            Intent intent = new Intent(context, RecipeStepActivity.class);
            intent.putExtra(RecipeStepFragment.ARG_ITEM_ID, stepId);

            context.startActivity(intent);
        }
    }
}
