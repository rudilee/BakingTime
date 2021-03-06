package udacity.android.bakingtime;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import udacity.android.bakingtime.api.Recipe;

/**
 * Created by rudilee on 8/30/17.
 */

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.ViewHolder> {
    private final RecipeCardClickListener listener;
    private List<Recipe> recipeList;

    public RecipeListAdapter(RecipeCardClickListener listener) {
        this.listener = listener;
    }

    public void setRecipeList(List<Recipe> recipeList) {
        this.recipeList = recipeList;

        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recipe_card, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        Activity activity = (Activity) listener;

        if (!recipe.image.isEmpty()) {
            Picasso.with(holder.image.getContext())
                    .load(recipe.image)
                    .placeholder(R.drawable.ic_mix)
                    .into(holder.image);
        }

        holder.name.setText(recipe.name);
        holder.servings.setText(Html.fromHtml(activity.getString(
                R.string.servings,
                recipe.servings
        )));
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return recipeList == null ? 0 : recipeList.size();
    }

    public interface RecipeCardClickListener {
        void onClick(Recipe recipe);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.recipe_image) ImageView image;
        @BindView(R.id.recipe_name) TextView name;
        @BindView(R.id.servings) TextView servings;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Recipe recipe = recipeList.get(getAdapterPosition());

            listener.onClick(recipe);
        }
    }
}
