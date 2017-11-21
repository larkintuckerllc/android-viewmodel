package com.larkintuckerllc.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.larkintuckerllc.viewmodel.db.TodoContract;
import com.larkintuckerllc.viewmodel.db.TodoDbHelper;
import java.util.ArrayList;
import java.util.List;

public class TodosViewModel extends AndroidViewModel {

    private TodoDbHelper mTodoDbHelper;
    private ArrayList<Todo> mTodos;

    TodosViewModel(Application application) {
        super(application);
        mTodoDbHelper = new TodoDbHelper(application);
    }

    public List<Todo> getTodos() {
        if (mTodos == null) {
           mTodos = new ArrayList<Todo>();
           loadTodos();
        }
        ArrayList<Todo> clonedTodos = new ArrayList<Todo>(mTodos.size());
        for(int i = 0; i < mTodos.size(); i++){
            clonedTodos.add(new Todo(mTodos.get(i)));
        }
        return clonedTodos;
    }

    private void loadTodos() {
        SQLiteDatabase db = mTodoDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TodoContract.TodoEntry.TABLE,
                new String[]{
                        TodoContract.TodoEntry._ID,
                        TodoContract.TodoEntry.COL_TODO_NAME,
                        TodoContract.TodoEntry.COL_TODO_DATE
                },
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            int idxId = cursor.getColumnIndex(TodoContract.TodoEntry._ID);
            int idxName = cursor.getColumnIndex(TodoContract.TodoEntry.COL_TODO_NAME);
            int idxDate = cursor.getColumnIndex(TodoContract.TodoEntry.COL_TODO_DATE);
            mTodos.add(new Todo(cursor.getLong(idxId), cursor.getString(idxName), cursor.getLong(idxDate)));
        }
        cursor.close();
        db.close();
    }

    public Todo addTodo(String name, long date) {
        // PERSIST
        SQLiteDatabase db = mTodoDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TodoContract.TodoEntry.COL_TODO_NAME, name);
        values.put(TodoContract.TodoEntry.COL_TODO_DATE, date);
        long id = db.insertWithOnConflict(TodoContract.TodoEntry.TABLE,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        // VM
        Todo todo = new Todo(id, name, date);
        mTodos.add(todo);
        return new Todo(todo);
    }

    public void removeTodo(long id) {
        // PERSIST
        SQLiteDatabase db = mTodoDbHelper.getWritableDatabase();
        db.delete(
                TodoContract.TodoEntry.TABLE,
                TodoContract.TodoEntry._ID + " = ?",
                new String[]{Long.toString(id)}
        );
        db.close();
        // VM
        int index = -1;
        for(int i = 0; i < mTodos.size(); i++){
            Todo todo = mTodos.get(i);
            if (todo.getId() == id) {
               index = i;
            }
        }
        if (index != -1) {
            mTodos.remove(index);
        }
    }

}
