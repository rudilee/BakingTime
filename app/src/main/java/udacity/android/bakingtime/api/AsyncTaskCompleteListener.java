package udacity.android.bakingtime.api;

// From extracting out AsyncTask article: http://www.jameselsey.co.uk/blogs/techblog/
// extracting-out-your-asynctasks-into-separate-classes-makes-your-code-cleaner/

/**
 * This is a useful callback mechanism so we can abstract our AsyncTasks out into separate,
 * re-usableand testable classes yet still retain a hook back into the calling activity.
 * Basically, it'll make classescleaner and easier to unit test.
 *
 * @param <T>
 */

public interface AsyncTaskCompleteListener<T> {
    /**
     * Invoked when the AsyncTask has completed its execution.
     * @param result The resulting object from the AsyncTask.
     */
    void onTaskComplete(T result);
}
