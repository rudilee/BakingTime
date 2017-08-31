package udacity.android.bakingtime.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by rudilee on 8/30/17.
 */

public interface BakingService {

    @GET("{json_path}")
    Call<List<Recipe>> listRecipes(@Path("json_path") String jsonPath);
}
