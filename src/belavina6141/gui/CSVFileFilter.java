package belavina6141.gui;
import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * Created by Olga Belavina on 2016-04-14.
 */
public class CSVFileFilter extends FileFilter{
    @Override
    public boolean accept(File pathname) {
        String name = pathname.getName();

        String extension = Utils.getFileExtension(name);
        if (extension == null) {
            return false;
        }

        if (extension.equals("csv")) {
            return true;
        }

        return false;
    }

    @Override
    public String getDescription() {
        return "CSV file (*.csv)";
    }
}
