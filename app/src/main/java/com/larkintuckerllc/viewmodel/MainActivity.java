package com.larkintuckerllc.viewmodel;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TodosAdapter mTodosAdapter;
    private TodosViewModel mTodosViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView lvTodos = findViewById(R.id.lvTodos);
        // VM
        mTodosViewModel = ViewModelProviders.of(this).get(TodosViewModel.class);
        List<Todo> todos = mTodosViewModel.getTodos();
        // VIEW
        mTodosAdapter = new TodosAdapter(this, todos);
        lvTodos.setAdapter(mTodosAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_todo:
                final EditText nameEditText = new EditText(this);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Add a new todo")
                        .setMessage("What do you want to do next?")
                        .setView(nameEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String name= String.valueOf(nameEditText.getText());
                                long date = (new Date()).getTime();
                                // VM AND VIEW
                                mTodosAdapter.add(mTodosViewModel.addTodo(name, date));
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void deleteTodo(View view) {
        View parent = (View) view.getParent();
        int position = (int) parent.getTag(R.id.POS);
        Todo todo = mTodosAdapter.getItem(position);
        // VM
        mTodosViewModel.removeTodo(todo.getId());
        // VIEW
        mTodosAdapter.remove(todo);
    }

    public class TodosAdapter extends ArrayAdapter<Todo> {

        private class ViewHolder {
            TextView tvName;
            TextView tvDate;
        }

        public TodosAdapter(Context context, List<Todo> todos) {
            super(context, R.layout.item_todo , todos);
        }

        @Override
        @NonNull
        public View getView(int position, View convertView, ViewGroup parent) {
            Todo todo = getItem(position);
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_todo, parent, false);
                viewHolder.tvName = convertView.findViewById(R.id.tvName);
                viewHolder.tvDate = convertView.findViewById(R.id.tvDate);
                convertView.setTag(R.id.VH, viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag(R.id.VH);
            }
            viewHolder.tvName.setText(todo.getName());
            viewHolder.tvDate.setText((new Date(todo.getDate()).toString()));
            convertView.setTag(R.id.POS, position);
            return convertView;
        }

    }


}
