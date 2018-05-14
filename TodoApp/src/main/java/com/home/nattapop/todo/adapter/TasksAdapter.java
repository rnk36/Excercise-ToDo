package com.home.nattapop.todo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.home.nattapop.todo.R;
import com.home.nattapop.todo.dao.model.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.MyViewHolder> {

    private Context context;
    private List<Task> tasksList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView task;
        public TextView dot;
        public TextView timestamp;

        public MyViewHolder(View view) {
            super(view);
            task = view.findViewById(R.id.task);
            dot = view.findViewById(R.id.dot);
            timestamp = view.findViewById(R.id.timestamp);
        }
    }

    public TasksAdapter(Context context, List<Task> tasksList) {
        this.context = context;
        this.tasksList = tasksList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Task task = tasksList.get(position);
        holder.task.setText(task.getTask());
        holder.dot.setText(Html.fromHtml("&#8226;"));
        holder.timestamp.setText(
                context.getResources().getString(R.string.txt_task_created_date)
                        + " "
                        + formatDate(task.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            return fmtOut.format(date);
        } catch (ParseException e) {
            Log.e("MyApp", "ParseException ", e);
        }

        return "";
    }
}
