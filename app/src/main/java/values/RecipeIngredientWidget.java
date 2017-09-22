package values;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.widget.RemoteViews;

import udacity.android.bakingtime.R;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in
 * {@link RecipeIngredientWidgetConfigureActivity RecipeIngredientWidgetConfigureActivity}
 */
public class RecipeIngredientWidget extends AppWidgetProvider {
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        int recipeId = RecipeIngredientWidgetConfigureActivity
                .loadRecipePref(context, appWidgetId);

        String recipe = "";
        String ingredients = "";

        if (recipeId != -1) {
            Cursor cursor = context.getContentResolver().query(
                    RecipeContentProvider.contentUriWithId(recipeId),
                    null,
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToNext()) {
                recipe = cursor.getString(cursor.getColumnIndex(RecipeDatabaseHelper.RECIPE));
                ingredients += cursor.getString(
                        cursor.getColumnIndex(RecipeDatabaseHelper.INGREDIENTS)
                );
            }
        }

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.recipe_ingredient_widget);

        views.setTextViewText(R.id.ingredients, Html.fromHtml(context.getString(
                R.string.widget_ingredients,
                recipe,
                ingredients
        )));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            RecipeIngredientWidgetConfigureActivity.deleteRecipePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

