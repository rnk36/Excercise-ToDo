package com.home.nattapop.todo.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.home.nattapop.todo.R;
import com.home.nattapop.todo.adapter.TasksAdapter;
import com.home.nattapop.todo.dao.DatabaseHelper;
import com.home.nattapop.todo.dao.model.Task;
import com.home.nattapop.todo.manager.Contextor;
import com.home.nattapop.todo.utils.DividerItemDecoration;
import com.home.nattapop.todo.utils.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {

    /***********************************
     * Variables
     ***********************************/
    private TasksAdapter tasksAdapter;
    private List<Task> tasksList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextView tvEmptyTask;
    private DatabaseHelper databaseHelper;

    /***********************************
     * Android Functions & Lifecycle
     ***********************************/
    public MainFragment() {
        super();
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    @SuppressWarnings("UnusedParameters")
    private void init(Bundle savedInstanceState) {
        databaseHelper = new DatabaseHelper(getActivity());
        tasksList.addAll(databaseHelper.getAllTasks());
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {

        recyclerView = rootView.findViewById(R.id.recyclerView);
        tvEmptyTask = rootView.findViewById(R.id.tvEmptyTask);
        tasksAdapter = new TasksAdapter(getActivity(), tasksList);
        RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(Contextor.getInstance().getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new
                DividerItemDecoration(getActivity(),
                LinearLayoutManager.VERTICAL,
                16));
        recyclerView.setAdapter(tasksAdapter);

        toggleEmptyTasks();

        recyclerView.addOnItemTouchListener(
                new RecyclerTouchListener(getActivity(),
                        recyclerView,
                        new RecyclerTouchListener.ClickListener() {
                            @Override
                            public void onClick(View view, int position) {

                            }

                            @Override
                            public void onLongClick(View view, int position) {
                                showActionsDialog(position);
                            }
                        }));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_add_todo:
                showTaskDialog(false, null, -1);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    /***********************************
     * App Functions
     ***********************************/
    private void createTask(String task) {
        long id = databaseHelper.insertTask(task);

        Task t = databaseHelper.getTask(id);

        if (t != null) {
            tasksList.add(0, t);

            tasksAdapter.notifyDataSetChanged();

            toggleEmptyTasks();
        }
    }

    private void updateTask(String task, int position) {

        Task t = tasksList.get(position);
        t.setTask(task);
        databaseHelper.updateTask(t);

        tasksList.set(position, t);
        tasksAdapter.notifyItemChanged(position);

        toggleEmptyTasks();
    }

    private void deleteTask(int position) {
        databaseHelper.deleteTask(tasksList.get(position));

        tasksList.remove(position);
        tasksAdapter.notifyItemRemoved(position);

        toggleEmptyTasks();
    }

    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_option_choose);
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showTaskDialog(
                            true,
                            tasksList.get(position),
                            position);
                } else {
                    deleteTask(position);
                }
            }
        });
        builder.show();
    }

    private void showTaskDialog(final boolean shouldUpdate, final Task task, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity());
        View view = layoutInflaterAndroid.inflate(R.layout.task_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getActivity());
        alertDialogBuilderUserInput.setView(view);

        final EditText inputTask = view.findViewById(R.id.task);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(
                !shouldUpdate ? getString(R.string.label_new_task_title)
                        : getString(R.string.label_edit_task_title));
        if (shouldUpdate && task != null) {
            inputTask.setText(task.getTask());
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(
                        shouldUpdate ? "update" : "save",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                            }
                        })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(inputTask.getText().toString())) {
                    Toast.makeText(getActivity(),
                            R.string.toast_empty_task_input,
                            Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }
                if (shouldUpdate && task != null) {
                    updateTask(inputTask.getText().toString(), position);
                } else {
                    createTask(inputTask.getText().toString());
                }
            }
        });
    }

    private void toggleEmptyTasks() {
        if (databaseHelper.getTasksCount() > 0) {
            tvEmptyTask.setVisibility(View.GONE);
        } else {
            tvEmptyTask.setVisibility(View.VISIBLE);
        }
    }

}
