package udacity.android.bakingtime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import udacity.android.bakingtime.api.Ingredient;
import udacity.android.bakingtime.api.Recipe;
import udacity.android.bakingtime.api.Step;

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
    public static final String RECIPE_KEY = "recipe-detail";

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean twoPaneMode;
    private Recipe recipe;

    @BindView(R.id.ingredients_list) TextView ingredientsList;
    @BindView(R.id.recipe_step_list) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent.hasExtra(RECIPE_KEY)) {
            recipe = intent.getParcelableExtra(RECIPE_KEY);

            setTitle(recipe.name);

            String ingredients = "";

            for (Ingredient i : recipe.ingredients) {
                String quantity = new DecimalFormat("#.#").format(i.quantity);

                ingredients += getString(R.string.ingredient, quantity, i.measure, i.ingredient);
            }

            ingredientsList.setText(Html.fromHtml(getString(
                    R.string.ingredients_list,
                    ingredients
            )));

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
    public void onClick(Step step) {
        if (twoPaneMode) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(RecipeStepFragment.STEP_KEY, step);

            RecipeStepFragment fragment = new RecipeStepFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.recipe_step_detail_container, fragment)
                    .commit();
        } else {
            Intent showStepDetailIntent = new Intent(this, RecipeStepActivity.class);
            showStepDetailIntent.putExtra(RecipeStepFragment.STEP_KEY, step);

            startActivity(showStepDetailIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
