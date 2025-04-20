package com.example.todo;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPrefManager {
    private static final String PREF_NAME = "todo_app_prefs";
    private static final String KEY_TASKS = "tasks";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;

    public SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();
    }

    /**
     * Save the list of tasks to SharedPreferences
     * @param tasks List of tasks to save
     */
    public void saveTasks(List<Task> tasks) {
        String tasksJson = gson.toJson(tasks);
        editor.putString(KEY_TASKS, tasksJson);
        editor.apply();
    }

    /**
     * Load the list of tasks from SharedPreferences
     * @return List of tasks
     */
    public List<Task> loadTasks() {
        String tasksJson = sharedPreferences.getString(KEY_TASKS, null);
        if (tasksJson == null) {
            return new ArrayList<>();
        }

        Type type = new TypeToken<List<Task>>(){}.getType();
        return gson.fromJson(tasksJson, type);
    }

    /**
     * Clear all tasks from SharedPreferences
     */
    public void clearTasks() {
        editor.remove(KEY_TASKS);
        editor.apply();
    }
}