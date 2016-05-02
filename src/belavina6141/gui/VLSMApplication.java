package belavina6141.gui;

import javax.swing.*;

/**
 * Created by Olga Belavina on 2016-03-31.
 */
public class VLSMApplication {

    public static void main(String[] args) {
        Runnable displayWindow = () -> {
            MainFrame frame = new MainFrame();
        };

        SwingUtilities.invokeLater(displayWindow);
    }

}
