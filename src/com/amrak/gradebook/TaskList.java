package com.amrak.gradebook;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TaskList extends Activity {

	// database
	TaskDBAdapter tasksDB = new TaskDBAdapter(this);

	// context
	Context context = this;

	// view(s)
	ListView listView;
	TextView title;

	// listAdapters
	private TaskListAdapter taskslistAdapter;

	// data
	public static final int CONTEXT_EDIT = 0;
	public static final int CONTEXT_DELETE = 1;
	// tasks
	ArrayList<TaskData> tasks = new ArrayList<TaskData>();

	// variables
	final private String TAG = "Tasks";
	int[] refIDPass_Task;
	int contextSelection;

	@TargetApi(11)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_list);

		// initialization of views
		// change title back to Tasks since default title is the app's name
		getActionBar().setTitle("Tasks");
		// read data from database
		dataReadToList();

		// input listview data
		taskslistAdapter = new TaskListAdapter(this, tasks);
		listView.setAdapter(taskslistAdapter);
		registerForContextMenu(listView);

	}

	@Override
	protected void onResume() {
		super.onResume();
		dataReset();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_task_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.preferences:
			Intent iSettings = new Intent("com.amrak.gradebook.SETTINGS");
			startActivity(iSettings);
			break;
		case R.id.addtask:
			Intent iAddTask = new Intent("com.amrak.gradebook.ADDTASK");
			startActivity(iAddTask);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	// Context menu
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

		TaskData taskSelected = (TaskData) taskslistAdapter
				.getItem(info.position);
		String taskTitle = taskSelected.getTitle();
		menu.setHeaderTitle(taskTitle);
		menu.add(0, CONTEXT_EDIT, 0, "Edit Task");
		menu.add(0, CONTEXT_DELETE, 0, "Delete Task");
	}

	public boolean onContextItemSelected(MenuItem menuItem) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem
				.getMenuInfo();
		contextSelection = info.position;

		switch (menuItem.getItemId()) {
		case CONTEXT_EDIT:
			Intent iAddTask = new Intent("com.amrak.gradebook.ADDTASK");
			iAddTask.putExtra("idEdit_Item", refIDPass_Task[contextSelection]);
			iAddTask.putExtra("id_Mode", 1);
			startActivity(iAddTask);
			dataReset();
			return true;
		case CONTEXT_DELETE:

			tasksDB.open();
			Cursor cDelete = tasksDB.getTask(refIDPass_Task[contextSelection]);
			String titleDelete = cDelete.getString(cDelete
					.getColumnIndex("taskTitle"));
			tasksDB.close();

			new AlertDialog.Builder(this)
			.setTitle("Delete Task?")
			.setMessage(
					"Are you sure you want to delete \"" + titleDelete
					+ "\"?")
					.setPositiveButton("Delete",
							new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {

							tasksDB.open();
							Cursor cDelete = tasksDB
									.getTask(refIDPass_Task[contextSelection]);
							String titleDelete = cDelete.getString(cDelete
									.getColumnIndex("taskTitle"));
							tasksDB.deleteTask(refIDPass_Task[contextSelection]);
							tasksDB.close();

							Toast toast = Toast
									.makeText(
											context,
											titleDelete
											+ " was deleted successfully.",
											Toast.LENGTH_SHORT);
							try {
								// center toast
								((TextView) ((LinearLayout) toast
										.getView()).getChildAt(0))
										.setGravity(Gravity.CENTER_HORIZONTAL);
							} catch (ClassCastException cce) {
								Log.d(TAG, cce.getMessage());
							}
							toast.show();
							dataReset();
						}
					}).setNegativeButton("Cancel", null).show();

			return true;
		default:
			return super.onContextItemSelected(menuItem);
		}
	}

	public void dataReset() {
		// clear data
		tasks.clear();
		// read database
		dataReadToList();

		// input listview data
		taskslistAdapter.notifyDataSetChanged();
	}

	public void dataReadToList() {
		tasksDB.open();
		Cursor c = tasksDB.getAllTasks();
		int i = 0;
		refIDPass_Task = new int[c.getCount()];
		if (c.moveToFirst()) {
			do {
				// get ids of each
				refIDPass_Task[i] = c.getInt(c.getColumnIndex("_id")); 
				tasks.add(new TaskData(c.getInt(c.getColumnIndex("_id")), c
						.getString(c.getColumnIndex("taskTitle")), c
						.getString(c.getColumnIndex("taskDateMade")), c
						.getString(c.getColumnIndex("taskDateDue")), c
						.getString(c.getColumnIndex("taskDateDueTime")),
						context));
				i++;
			} while (c.moveToNext());
		}

		tasksDB.close();
	}

}