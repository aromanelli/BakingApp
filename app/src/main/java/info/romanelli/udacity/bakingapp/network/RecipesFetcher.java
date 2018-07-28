package info.romanelli.udacity.bakingapp.network;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class RecipesFetcher {

    final static private String TAG = RecipesFetcher.class.getSimpleName();

    static private Retrofit RETROFIT = new Retrofit.Builder()
            .baseUrl("https://go.udacity.com")
            .addConverterFactory( GsonConverterFactory.create() )
            .build();

    /**
     * @param context Used for determining if the caller is online or offline.
     * @param listener The code called when the fetching is completed.
     *                 Will return a {@code null} if an error occurred.
     */
    synchronized static public void fetchRecipes(final Context context, final Listener listener) {

        if (NetUtil.isOnline(context)) {

            Call<List<RecipeData>> call =
                    RETROFIT.create(RecipesFetcher.Service.class).fetchRecipes();

            call.enqueue(new Callback<List<RecipeData>>() {
                @Override
                public void onResponse(@NonNull Call<List<RecipeData>> call,
                                       @NonNull retrofit2.Response<List<RecipeData>> response) {
                    if (response.body() != null) {
                        //noinspection ConstantConditions
                        listener.fetchedRecipes(response.body());
                    } else {
                        listener.fetchedRecipes(Collections.<RecipeData>emptyList());
                    }
                }
                @Override
                public void onFailure(@NonNull Call<List<RecipeData>> call, @NonNull Throwable t) {
                    Log.e(TAG, "Failure fetching Recipes! ", t);
                    listener.fetchedRecipes(null);
                }
            });

        } else {
            Log.w(TAG, "Not online, so cannot fetch recipes.");
        }

    }

    interface Service {
        // http://go.udacity.com/android-baking-app-json
        @GET("android-baking-app-json")
        Call<List<RecipeData>> fetchRecipes();
    }

    public interface Listener {
        void fetchedRecipes(List<RecipeData> recipes);
    }

}
