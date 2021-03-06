package ca.projectkarma.gradetrackr.db.adapter;

import ca.projectkarma.gradetrackr.CustomSuggestionProvider;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class CoursesDBAdapter extends DBAdapter {

    // database fields
    public static final String ROW_ID = "_id";
    public static final String COURSE_TITLE = "courseTitle";
    public static final String COURSE_CODE = "courseCode";
    public static final String COURSE_UNITS = "courseUnits";
    public static final String COURSE_NOTES = "notes";
    public static final String TERM_REFERENCE = "termRef";
    private static final String TAG = "CoursesDBAdapter";

    // database name
    public static final String DATABASE_TABLE = "courses";
    
    //Uri for Search Suggest
  	public static final Uri SUGGEST_URI = Uri.parse("content://" + CustomSuggestionProvider.AUTHORITY + "/" + SearchManager.SUGGEST_URI_PATH_QUERY + "/" + DATABASE_TABLE);

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx
     *            The Context within which to work
     */
    public CoursesDBAdapter(Context ctx) {
        super(ctx);
    }

    /**
     * Create a course. If the course is successfully created return the new
     * rowId for that course, otherwise return a -1 to indicate failure.
     * 
     * @param courseTitle
     *            title of the course
     * @param courseCode
     *            code of the course
     * @param courseUnits
     *            units of the course
     * @param notes
     *            notes of the course
     * @param termRef
     *            id reference to the term
     * @return rowId or -1 if failed
     */
    public long createCourse(String courseTitle, String courseCode, double courseUnits,
            String notes, int termRef) {
        ContentValues initialValues = new ContentValues();

        initialValues.put(COURSE_TITLE, courseTitle);
        initialValues.put(COURSE_CODE, courseCode);
        initialValues.put(COURSE_UNITS, courseUnits);
        initialValues.put(COURSE_NOTES, notes);
        initialValues.put(TERM_REFERENCE, termRef);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the course with the given rowId
     * 
     * @param rowId
     * @return true if deleted, false otherwise
     */
    public boolean deleteCourse(long rowId) {

        return mDb.delete(DATABASE_TABLE, ROW_ID + "=" + rowId, null) > 0; //$NON-NLS-1$
    }

    /**
     * Delete the course with the given termRefID
     * 
     * @param termRefID
     * @return true if deleted, false otherwise
     */
    public boolean deleteCoursesOfTerm(long termRefID) {

        return mDb.delete(DATABASE_TABLE, TERM_REFERENCE + "=" + termRefID, null) > 0; //$NON-NLS-1$
    }

    /**
     * Return a Cursor over the list of all courses in the database
     * 
     * @return Cursor over all courses
     */
    public Cursor getAllCourses() {

        return mDb.query(DATABASE_TABLE, new String[] { ROW_ID, COURSE_TITLE, COURSE_CODE,
                COURSE_UNITS, COURSE_NOTES, TERM_REFERENCE }, null, null, null, null, null);
    }

    public Cursor getCoursesOfTerm(long termRefID) throws SQLException {

        Cursor mCursor =

        mDb.query(true, DATABASE_TABLE, new String[] { ROW_ID, COURSE_TITLE, COURSE_CODE,
                COURSE_UNITS, COURSE_NOTES, TERM_REFERENCE }, TERM_REFERENCE + "=" + termRefID,
                null, null, null, null, null);
        if (mCursor != null)
        {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    /**
     * Return a cursor that has courses from the entire database whose title
     * partially matches the query.
     * @param query
     * @return cursor of courses sorted by COURSE_TITLE in ascending order
     * */
    public Cursor getCoursesSortByName(String query) {
    	SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
    	String[] columns = new String[] { ROW_ID, TERM_REFERENCE, COURSE_TITLE};
    	String selection = COURSE_TITLE + " LIKE ?";
    	String[] selectionArgs = new String[] {"%" + query + "%"};
    	String orderBy = COURSE_TITLE;
    	
		builder.setTables(DATABASE_TABLE);
		Cursor c = builder.query(mDb, columns, selection, selectionArgs, null, null, orderBy);

		return c;
    }

    /**
     * Return a Cursor positioned at the course that matches the given rowId
     * 
     * @param rowId
     * @return Cursor positioned to matching course, if found
     * @throws SQLException
     *             if course could not be found/retrieved
     */
    public Cursor getCourse(long rowId) throws SQLException {

        Cursor mCursor =

        mDb.query(true, DATABASE_TABLE, new String[] { ROW_ID, COURSE_TITLE, COURSE_CODE,
                COURSE_UNITS, COURSE_NOTES, TERM_REFERENCE }, ROW_ID + "=" + rowId, null, null,
                null, null, null);
        if (mCursor != null)
        {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * Update the course.
     * 
     * @param rowId
     * @param courseTitle
     *            title of the course
     * @param courseCode
     *            code of the course
     * @param courseUnits
     *            units of the course
     * @param notes
     *            notes of the course
     * @param termRef
     *            id reference to the term
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateCourse(long rowId, String courseTitle, String courseCode,
            double courseUnits, String notes, int termRef) {
        ContentValues args = new ContentValues();

        args.put(COURSE_TITLE, courseTitle);
        args.put(COURSE_CODE, courseCode);
        args.put(COURSE_UNITS, courseUnits);
        args.put(COURSE_NOTES, notes);
        args.put(TERM_REFERENCE, termRef);

        return mDb.update(DATABASE_TABLE, args, ROW_ID + "=" + rowId, null) > 0;
    }

}