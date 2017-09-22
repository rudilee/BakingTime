package values;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import udacity.android.bakingtime.R;

/**
 * The configuration screen for the {@link RecipeIngredientWidget RecipeIngredientWidget} AppWidget.
 */
public class RecipeIngredientWidgetConfigureActivity extends Activity {

    private static final String PREFERENCES_NAME = "values.RecipeIngredientWidget";
    private static final String PREFERENCE_PREFIX_KEY = "appwidget_";

    int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @BindView(R.id.recipes) RadioGroup recipes;
    @BindView(R.id.add_button) View addButton;

    View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = RecipeIngredientWidgetConfigureActivity.this;

            // When the addButton is clicked, store the string locally
            int recipeId = recipes.getCheckedRadioButtonId();
            saveRecipePref(context, appWidgetId, recipeId);

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RecipeIngredientWidget.updateAppWidget(context, appWidgetManager, appWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(RESULT_OK, resultValue);

            finish();
        }
    };

    public RecipeIngredientWidgetConfigureActivity() {
        super();
    }

    private void loadRecipeOptions()
    {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(
                RecipeContentProvider.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                RadioButton recipeOption = new RadioButton(this);
                recipeOption.setId(cursor.getInt(
                        cursor.getColumnIndex(RecipeDatabaseHelper.ID)
                ));
                recipeOption.setText(cursor.getString(
                        cursor.getColumnIndex(RecipeDatabaseHelper.RECIPE)
                ));

                recipes.addView(recipeOption);
            }

            cursor.close();
        }
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveRecipePref(Context context, int appWidgetId, int recipeId) {
        SharedPreferences.Editor preferencesEditor = context
                .getSharedPreferences(PREFERENCES_NAME, 0).edit();

        preferencesEditor.putInt(PREFERENCE_PREFIX_KEY + appWidgetId, recipeId);
        preferencesEditor.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static int loadRecipePref(Context context, int appWidgetId) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, 0);

        return preferences.getInt(PREFERENCE_PREFIX_KEY + appWidgetId, -1);
    }

    static void deleteRecipePref(Context context, int appWidgetId) {
        SharedPreferences.Editor preferencesEditor = context
                .getSharedPreferences(PREFERENCES_NAME, 0).edit();

        preferencesEditor.remove(PREFERENCE_PREFIX_KEY + appWidgetId);
        preferencesEditor.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.recipe_ingredient_widget_configure);

        ButterKnife.bind(this);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back addButton.
        setResult(RESULT_CANCELED);

        addButton.setOnClickListener(onClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();

        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID
            );
        }

        // If this activity was started with an intent without an app widget ID, finish with
        // an error.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        loadRecipeOptions();
    }
}

