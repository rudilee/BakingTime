package values;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

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

    @BindView(R.id.recipe_ingredients) EditText ingredients;
    @BindView(R.id.add_button) View addButton;

    View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = RecipeIngredientWidgetConfigureActivity.this;

            // When the addButton is clicked, store the string locally
            String widgetText = ingredients.getText().toString();
            saveTitlePref(context, appWidgetId, widgetText);

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

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor preferencesEditpr = context
                .getSharedPreferences(PREFERENCES_NAME, 0).edit();

        preferencesEditpr.putString(PREFERENCE_PREFIX_KEY + appWidgetId, text);
        preferencesEditpr.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, 0);

        String titleValue = preferences.getString(PREFERENCE_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFERENCES_NAME, 0).edit();
        prefs.remove(PREFERENCE_PREFIX_KEY + appWidgetId);
        prefs.apply();
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

            return;
        }

        ingredients.setText(loadTitlePref(RecipeIngredientWidgetConfigureActivity.this,
                appWidgetId));
    }
}

