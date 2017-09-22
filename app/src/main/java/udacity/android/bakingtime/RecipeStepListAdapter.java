package udacity.android.bakingtime;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import udacity.android.bakingtime.api.Step;

/**
 * Created by rudilee on 9/19/17.
 */

public class RecipeStepListAdapter
        extends RecyclerView.Adapter<RecipeStepListAdapter.ViewHolder> {
    private final RecipeStepItemClickListener listener;
    private final List<Step> stepList;

    public RecipeStepListAdapter(List<Step> stepList, RecipeStepItemClickListener listener) {
        this.stepList = stepList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recipe_step_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Step step = stepList.get(position);
        Activity activity = (Activity) listener;

        holder.shortDescription.setText(activity.getString(
                R.string.step_description,
                step.id + 1,
                step.shortDescription
        ));
    }

    @Override
    public int getItemCount() {
        return stepList == null ? 0 : stepList.size();
    }

    interface RecipeStepItemClickListener {
        void onClick(Step step);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.short_description) TextView shortDescription;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Step step = stepList.get(getAdapterPosition());

            listener.onClick(step);
        }
    }
}
