package id.prasetiyo.imagefiltering.helper;

import android.support.annotation.NonNull;

import com.nononsenseapps.filepicker.FilePickerFragment;

import java.io.File;

/**
 * Created by aoktox on 13/06/16.
 */
public class FilteredFilePickerFragment extends FilePickerFragment {

    // File extension to filter on
    private static final String EXTENSION = ".txt";

    /**
     *
     * @param file
     * @return The file extension. If file has no extension, it returns null.
     */
    private String getExtension(@NonNull File file) {
        String path = file.getPath();
        int i = path.lastIndexOf(".");
        if (i < 0) {
            return null;
        } else {
            return path.substring(i);
        }
    }

    @Override
    protected boolean isItemVisible(final File file) {
        boolean ret = super.isItemVisible(file);
        if (ret && !isDir(file) && (mode == MODE_FILE || mode == MODE_FILE_AND_DIR)) {
            String ext = getExtension(file);
            return ext != null && EXTENSION.equalsIgnoreCase(ext);
        }
        return ret;
    }
}
