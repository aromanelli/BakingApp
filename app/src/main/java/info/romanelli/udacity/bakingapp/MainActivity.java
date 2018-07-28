package info.romanelli.udacity.bakingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    final static private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (NetUtil.ifOnline(this)) {
//            RecipesFetcher.fetchRecipes(this,
//                    new RecipesFetcher.Listener(){
//                        @Override
//                        public void fetchedRecipes(List<RecipeData> recipes) {
//                            Log.d(TAG, "fetchedRecipes() called with: recipes = [" + recipes + "]");
//                        }
//                    }
//            );
//        }

    }
}
