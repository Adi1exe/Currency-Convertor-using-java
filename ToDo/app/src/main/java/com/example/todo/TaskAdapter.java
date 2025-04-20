package com.example.todo;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> taskList;
    private Context context;
    private TaskItemListener listener;

    // Interface for handling item clicks
    public interface TaskItemListener {
        void onTaskClick(Task task, int position);
        void onCheckboxClick(Task task, int position);
    }

    public TaskAdapter(Context context, List<Task> taskList, TaskItemListener listener) {
        this.context = context;
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        // Set task title
        holder.tvTitle.setText(task.getTitle());

        // Apply strike-through style if task is done
        if (task.isDone()) {
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.cardView.setAlpha(0.7f);
        } else {
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.cardView.setAlpha(1.0f);
        }

        // Set checkbox state
        holder.checkBox.setChecked(task.isDone());

        // Set description (if available)
        if (task.getDescription() != null && !task.getDescription().isEmpty()) {
            holder.tvDescription.setVisibility(View.VISIBLE);
            holder.tvDescription.setText(task.getDescription());

            if (task.isDone()) {
                holder.tvDescription.setPaintFlags(holder.tvDescription.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                holder.tvDescription.setPaintFlags(holder.tvDescription.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        } else {
            holder.tvDescription.setVisibility(View.GONE);
        }

        // Set category label (if available)
        if (task.getCategory() != null && !task.getCategory().isEmpty()) {
            holder.tvCategory.setVisibility(View.VISIBLE);
            holder.tvCategory.setText(task.getCategory());

            // Set different background colors based on category
            int colorResId;
            switch (task.getCategory().toLowerCase()) {
                case "work":
                    colorResId = R.color.category_work;
                    break;
                case "personal":
                    colorResId = R.color.category_personal;
                    break;
                case "college":
                    colorResId = R.color.category_college;
                    break;
                default:
                    colorResId = R.color.category_default;
                    break;
            }
            holder.tvCategory.setBackgroundColor(ContextCompat.getColor(context, colorResId));
        } else {
            holder.tvCategory.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return taskList == null ? 0 : taskList.size();
    }

    public void updateTasks(List<Task> newTasks) {
        this.taskList = newTasks;
        notifyDataSetChanged();
    }

    public void removeTask(int position) {
        if (position >= 0 && position < taskList.size()) {
            taskList.remove(position);
            notifyItemRemoved(position);
        }
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvCategory;
        CheckBox checkBox;
        CardView cardView;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvDescription = itemView.findViewById(R.id.tvTaskDescription);
            tvCategory = itemView.findViewById(R.id.tvTaskCategory);
            checkBox = itemView.findViewById(R.id.checkboxTask);
            cardView = itemView.findViewById(R.id.cardView);

            // Set click listeners
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onTaskClick(taskList.get(position), position);
                }
            });

            checkBox.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onCheckboxClick(taskList.get(position), position);
                }
            });
        }
    }
}