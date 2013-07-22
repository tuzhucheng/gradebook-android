package com.amrak.gradebook;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.amrak.gradebook.db.adapter.CategoriesDBAdapter;
import com.amrak.gradebook.db.adapter.CoursesDBAdapter;
import com.amrak.gradebook.db.adapter.EvaluationsDBAdapter;
import com.amrak.gradebook.db.adapter.TaskDBAdapter;

public class CustomSuggestionProvider extends ContentProvider {
	
	private static final String TAG = "CustomSuggestionProvider";
	public static String AUTHORITY = "com.amrak.gradebook.CustomSuggestionProvider";
	
	CoursesDBAdapter coursesDBAdapter;
	CategoriesDBAdapter catsDBAdapter;
	EvaluationsDBAdapter evalsDBAdapter;
	TaskDBAdapter taskDBAdapter;
	
	private static final int SEARCH_COURSES = 0;
	private static final int SEARCH_CATEGORIES = 1;
	private static final int SEARCH_EVALUATIONS = 2;
	private static final int SEARCH_TASKS = 3;
	private static final UriMatcher uriMatcher = buildUriMatcher();
	
	public CustomSuggestionProvider() {
	}
	
	private static UriMatcher buildUriMatcher() {
		UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		//to get suggestions...
		matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/" + CoursesDBAdapter.DATABASE_TABLE + "/*", SEARCH_COURSES);
		matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/" + CategoriesDBAdapter.DATABASE_TABLE + "/*", SEARCH_CATEGORIES);
		matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/" + EvaluationsDBAdapter.DATABASE_TABLE + "/*", SEARCH_EVALUATIONS);
		matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/" + TaskDBAdapter.DATABASE_TABLE + "/*", SEARCH_TASKS);
		return matcher;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// Implement this to handle requests to delete one or more rows.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public String getType(Uri uri) {
		// TODO: Implement this to handle requests for the MIME type of the data
		// at the given URI.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO: Implement this to handle requests to insert a new row.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public boolean onCreate() {
		coursesDBAdapter = new CoursesDBAdapter(getContext());
		catsDBAdapter = new CategoriesDBAdapter(getContext());
		evalsDBAdapter = new EvaluationsDBAdapter(getContext());
		taskDBAdapter = new TaskDBAdapter(getContext());
		
		coursesDBAdapter.open();
		catsDBAdapter.open();
		evalsDBAdapter.open();
		taskDBAdapter.open();
		
		//Note, in Content Provider, DB Adapters do not need to be closed, please refer to
		//http://stackoverflow.com/questions/4547461/closing-the-database-in-a-contentprovider
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		Log.i(TAG, "Content provider received query. Uri is " + uri.toString());

		switch(uriMatcher.match(uri)) {
		case SEARCH_COURSES:
			Log.i(TAG, "URI Matcher matched SEARCH_EVALUATIONS");
			return coursesDBAdapter.getCoursesSortByName(uri.getLastPathSegment());
		case SEARCH_CATEGORIES:
			Log.i(TAG, "URI Matcher matched SEARCH_CATEGORIES");
			return catsDBAdapter.getCategoriesSortByName(uri.getLastPathSegment());
		case SEARCH_EVALUATIONS:
			Log.i(TAG, "URI Matcher matched SEARCH_EVALUATIONS");
			return evalsDBAdapter.getEvaluationSortByName(uri.getLastPathSegment());
		case SEARCH_TASKS:
			Log.i(TAG, "URI Matcher matched SEARCH_TASKS");
			return taskDBAdapter.getTasksSortByName(uri.getLastPathSegment());
		default:
			Log.i(TAG, "URI Matcher did not match anything");
			return null;
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO: Implement this to handle requests to update one or more rows.
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
