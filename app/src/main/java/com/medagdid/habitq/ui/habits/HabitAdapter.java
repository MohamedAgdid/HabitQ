package com.medagdid.habitq.ui.habits;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.medagdid.habitq.R;
import com.medagdid.habitq.model.Habit;

import java.util.List;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {

    public interface OnHabitClickListener {
        void onHabitCompleted(Habit habit);
    }

    private final List<Habit> habits;
    private final OnHabitClickListener listener;

    public HabitAdapter(List<Habit> habits, OnHabitClickListener listener) {
        this.habits = habits;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_habit, parent, false);
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        Habit habit = habits.get(position);

        holder.name.setText(habit.getName());
        holder.check.setAlpha(habit.isCompletedToday() ? 1f : 0.3f);

        holder.itemView.setOnClickListener(v -> {
            if (!habit.isCompletedToday()) {
                listener.onHabitCompleted(habit);
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return habits.size();
    }

    static class HabitViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView check;

        HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.habitName);
            check = itemView.findViewById(R.id.checkIcon);
        }
    }
}
