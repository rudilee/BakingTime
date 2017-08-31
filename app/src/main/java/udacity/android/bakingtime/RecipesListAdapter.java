package udacity.android.bakingtime;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import udacity.android.bakingtime.api.Recipe;

/**
 * Created by rudilee on 8/30/17.
 */

public class RecipesListAdapter extends RecyclerView.Adapter<RecipesListAdapter.ViewHolder> {
    private final RecipeCardClickListener listener;
    private List<Recipe> recipesList;

    public RecipesListAdapter(RecipeCardClickListener listener) {
        this.listener = listener;
    }

    public void setRecipesList(List<Recipe> recipesList) {
        this.recipesList = recipesList;

        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        View view = LayoutInflater
                .from(context)
                .inflate(R.layout.recipe_card, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Recipe recipe = recipesList.get(position);

        holder.name.setText(recipe.name);
        holder.servings.setText(String.valueOf(recipe.servings));
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return recipesList == null ? 0 : recipesList.size();
    }

    public interface RecipeCardClickListener {
        void onClick(int recipeId);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.recipe_name) TextView name;
        @BindView(R.id.servings) TextView servings;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Recipe recipe = recipesList.get(getAdapterPosition());

            listener.onClick(recipe.id);
        }
    }
}
