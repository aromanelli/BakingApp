package info.romanelli.udacity.bakingapp;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;

import info.romanelli.udacity.bakingapp.data.RecipeData;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeInfoAppWidget extends AppWidgetProvider {

    final static private String TAG = RecipeInfoAppWidget.class.getSimpleName();

    private static RecipeData RECIPE_DATA;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // Construct the RemoteViews object
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipeinfo_app_widget);

        if (RECIPE_DATA == null) {
            views.setImageViewResource(R.id.appwidget_recipe_image, 0);
            views.setTextViewText(R.id.appwidget_recipe_title, "");
            views.setTextViewText(R.id.appwidget_recipe_text, "");
        } else {

            // Since we can't supply an error image in Picasso when
            // its working with views, set the default up front ...
            views.setImageViewResource(R.id.appwidget_recipe_image, R.drawable.ic_baseline_fastfood_24px);

            final String urlPicture = RECIPE_DATA.getImage();
            if (!AppUtil.isEmpty(urlPicture)) {
                Picasso.get().load(urlPicture).into(views, R.id.appwidget_recipe_image, appWidgetIds );
            }

            views.setTextViewText(R.id.appwidget_recipe_title, RECIPE_DATA.getName());

            views.setTextViewText(
                    R.id.appwidget_recipe_text,
                    AppUtil.getIngredientsText(context, RECIPE_DATA.getIngredients())
            );

        }

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
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

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the Bundle and RecipeData reference that was sent over (from MainActivity) ...
        Log.d(RecipeInfoAppWidget.class.getSimpleName(), "onReceive() called with: context = [" + context + "], intent = [" + intent + "]");
        Bundle bundle = intent.getParcelableExtra(RecipeInfoAppWidget.class.getSimpleName());
        if (bundle != null) {
            RECIPE_DATA = bundle.getParcelable(MainActivity.KEY_RECIPE_DATA);
        }
        super.onReceive(context, intent);
    }

}

