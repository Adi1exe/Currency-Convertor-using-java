package com.example.todo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.TaskItemListener {

    private static final int REQUEST_ADD_TASK = 1;
    private static final int REQUEST_EDIT_TASK = 2;

    private RecyclerView recyclerViewTasks;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private TextView tvEmptyState;
    private FloatingActionButton fabAddTask;

    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        fabAddTask = findViewById(R.id.fabAddTask);

        // Initialize SharedPrefManager
        sharedPrefManager = new SharedPrefManager(this);

        // Load tasks from SharedPreferences
        taskList = sharedPrefManager.loadTasks();

        // Setup RecyclerView
        setupRecyclerView();

        // Setup FAB click listener
        fabAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
            startActivityForResult(intent, REQUEST_ADD_TASK);
        });

        // Update empty state visibility
        updateEmptyState();
    }

    private void setupRecyclerView() {
        // Initialize adapter
        taskAdapter = new TaskAdapter(this, taskList, this);

        // Set layout manager
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));

        // Set adapter
        recyclerViewTasks.setAdapter(taskAdapter);

        // Setup swipe gestures
        setupSwipeGestures();
    }

    private void setupSwipeGestures() {
        // Create ItemTouchHelper for swipe actions
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,@NonNull RecyclerView.ViewHolder target) {
                return false; // We don't support moving items
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Task swipedTask = taskList.get(position);

                if (direction == ItemTouchHelper.LEFT) {
                    // LEFT SWIPE - DELETE
                    Task deletedTask = new Task(
                            swipedTask.getTitle(),
                            swipedTask.getDescription(),
                            swipedTask.getCategory());
                    deletedTask.setDone(swipedTask.isDone());

                    // Remove task and update UI
                    taskList.remove(position);
                    taskAdapter.notifyItemRemoved(position);

                    // Save changes
                    sharedPrefManager.saveTasks(taskList);

                    // Update empty state
                    updateEmptyState();

                    // Show undo snackbar
                    Snackbar.make(recyclerViewTasks, R.string.task_deleted, Snackbar.LENGTH_LONG)
                            .setAction(R.string.undo, v -> {
                                // Restore the task
                                taskList.add(position, deletedTask);
                                taskAdapter.notifyItemInserted(position);

                                // Save changes
                                sharedPrefManager.saveTasks(taskList);

                                // Update empty state
                                updateEmptyState();
                            })
                            .show();
                } else if (direction == ItemTouchHelper.RIGHT) {
                    // RIGHT SWIPE - TOGGLE COMPLETION
                    swipedTask.toggleDone();
                    taskAdapter.notifyItemChanged(position);

                    // Save changes
                    sharedPrefManager.saveTasks(taskList);

                    // Show confirmation snackbar
                    Snackbar.make(recyclerViewTasks,swipedTask.isDone() ? R.string.task_marked_done : "Task marked as not done", Snackbar.LENGTH_SHORT).show();
                }
            }
        }).attachToRecyclerView(recyclerViewTasks);
    }

    private void updateEmptyState() {
        if (taskList.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerViewTasks.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            recyclerViewTasks.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTaskClick(Task task, int position) {
        // Open task in edit mode
        Intent intent = new Intent(this, AddEditTaskActivity.class);
        intent.putExtra("task", task);
        intent.putExtra("position", position);
        startActivityForResult(intent, REQUEST_EDIT_TASK);
    }

    @Override
    public void onCheckboxClick(Task task, int position) {
        // Toggle task completion status
        task.toggleDone();
        taskAdapter.notifyItemChanged(position);

        // Save changes
        sharedPrefManager.saveTasks(taskList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_ADD_TASK) {
                // Handle add task result
                Task newTask = (Task) data.getSerializableExtra("task");
                taskList.add(newTask);
                taskAdapter.notifyItemInserted(taskList.size() - 1);

                // Save changes
                sharedPrefManager.saveTasks(taskList);

                // Update empty state
                updateEmptyState();
            } else if (requestCode == REQUEST_EDIT_TASK) {
                // Handle edit task result
                Task updatedTask = (Task) data.getSerializableExtra("task");
                int position = data.getIntExtra("position", -1);

                if (position != -1) {
                    taskList.set(position, updatedTask);
                    taskAdapter.notifyItemChanged(position);

                    // Save changes
                    sharedPrefManager.saveTasks(taskList);
                }
            }
        }
    }
}