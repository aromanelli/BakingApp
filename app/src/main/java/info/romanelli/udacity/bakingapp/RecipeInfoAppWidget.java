package info.romanelli.udacity.bakingapp;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeInfoAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_info_app_widget);

        views.setTextViewText(R.id.appwidget_title, context.getString(R.string.app_name));
        views.setImageViewResource(R.id.appwidget_recipe_image, R.drawable.ic_baseline_fastfood_24px);
        views.setTextViewText(R.id.appwidget_recipe_title, context.getString(R.string.appwidget_desc_recipe_title));
        views.setTextViewText(R.id.appwidget_recipe_text, context.getString(R.string.appwidget_desc_recipe_text));

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
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

