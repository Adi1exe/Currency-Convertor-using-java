package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class AddEditTaskActivity extends AppCompatActivity {

    private TextInputEditText editTextTitle, editTextDescription;
    private Spinner spinnerCategory;
    private Button btnSaveTask;

    private Task taskToEdit = null;
    private int taskPosition = -1;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        // Initialize UI components
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSaveTask = findViewById(R.id.btnSaveTask);

        // Setup category spinner
        setupCategorySpinner();

        // Check if we're in edit mode
        if (getIntent().hasExtra("task")) {
            isEditMode = true;
            taskToEdit = (Task) getIntent().getSerializableExtra("task");
            taskPosition = getIntent().getIntExtra("position", -1);

            // Set title
            setTitle(R.string.edit_task);

            // Fill form with task data
            populateForm();
        } else {
            // Set title
            setTitle(R.string.add_task);
        }

        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Setup save button click listener
        btnSaveTask.setOnClickListener(v -> saveTask());
    }

    private void setupCategorySpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.task_categories, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinnerCategory.setAdapter(adapter);
    }

    private void populateForm() {
        if (taskToEdit != null) {
            // Set title and description
            editTextTitle.setText(taskToEdit.getTitle());
            editTextDescription.setText(taskToEdit.getDescription());

            // Set category selection
            String category = taskToEdit.getCategory();
            if (!TextUtils.isEmpty(category)) {
                // Find the position of the category in spinner
                ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerCategory.getAdapter();
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (adapter.getItem(i).toString().equalsIgnoreCase(category)) {
                        spinnerCategory.setSelection(i);
                        break;
                    }
                }
            }
        }
    }

    private void saveTask() {
        // Get input values
        String title = editTextTitle.getText() != null ? editTextTitle.getText().toString().trim() : "";
        String description = editTextDescription.getText() != null ?
                editTextDescription.getText().toString().trim() : "";
        String category = spinnerCategory.getSelectedItemPosition() > 0 ?
                spinnerCategory.getSelectedItem().toString() : "";

        // Validate title
        if (TextUtils.isEmpty(title)) {
            editTextTitle.setError(getString(R.string.error_empty_title));
            return;
        }

        // Create or update task
        Task task;
        if (isEditMode && taskToEdit != null) {
            // Update existing task
            taskToEdit.setTitle(title);
            taskToEdit.setDescription(description);
            taskToEdit.setCategory(category);
            task = taskToEdit;
        } else {
            // Create new task
            task = new Task(title, description, category);
        }

        // Set result and finish
        Intent resultIntent = new Intent();
        resultIntent.putExtra("task", task);

        if (isEditMode) {
            resultIntent.putExtra("position", taskPosition);
        }

        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle back button click
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}