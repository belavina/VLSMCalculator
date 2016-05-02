package belavina6141.gui;

import belavina6141.Calculator.VLSMReport;
import belavina6141.Calculator.VLSMReportSubnet;
import belavina6141.Controller.VLSMController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * Created by Olga Belavina on 2016-03-31.
 */
public class MainFrame extends JFrame {

    private VLSMCalculatorPanel calculatorPanel;
    private JScrollPane leftScrollPane;
    private JScrollPane rightScrollPane;

    private VLSMController controller;
    private VLSMTablePanel tablePanel;
    private JFileChooser fileChooser;


    public MainFrame() {
        super("VLSM Calculator");

        /* Set layout : */
        setLayout(new BorderLayout());
        calculatorPanel = new VLSMCalculatorPanel();
        leftScrollPane  = new JScrollPane(calculatorPanel);
        controller      = new VLSMController();
        tablePanel      = new VLSMTablePanel();
        rightScrollPane = new JScrollPane(tablePanel);
        fileChooser     = new JFileChooser();
        fileChooser.addChoosableFileFilter(new CSVFileFilter());

        rightScrollPane.setPreferredSize(tablePanel.getPreferredSize());
        setJMenuBar(createMenuBar());

        /* Set data for the table */
        tablePanel.setData(controller.getReport());
        tablePanel.add(tablePanel.getTableHeader(), BorderLayout.NORTH);

        calculatorPanel.setCalcListener(new VLSMCalcListener() {
            @Override
            public void VLSMCalclEventOccurred(VLSMCalcEvent e) {

                controller.calculateVLSM(e);
                tablePanel.setLog(controller.getLogText());

                tablePanel.refresh();
            }
        });




        leftScrollPane.setPreferredSize(new Dimension(300,350));


        leftScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        leftScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        rightScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        rightScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        add(leftScrollPane, BorderLayout.WEST);
        add(rightScrollPane, BorderLayout.CENTER);

        setMinimumSize(new Dimension(347, 330));
        setSize(870 ,350 );
        /* Quit when you close the window */
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }


    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenu windowMenu = new JMenu("Window");

        JMenuItem exportDataItem = new JMenuItem("Export Into CSV...");
        JMenuItem exitItem = new JMenuItem("Exit");

        JMenu showMenu = new JMenu("Show");
        JCheckBoxMenuItem showCalculator = new JCheckBoxMenuItem("Calculator");

        showCalculator.setSelected(true);

        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        showCalculator.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBoxMenuItem menuItem =  (JCheckBoxMenuItem)e.getSource();
                leftScrollPane.setVisible(menuItem.isSelected());
                validate();
                revalidate();
            }
        });

        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));

        exportDataItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION){
                    File file = fileChooser.getSelectedFile();
                    tablePanel.saveCSVFile(file);
                }
            }
        });


        showMenu.add(showCalculator);
        windowMenu.add(showMenu);

        fileMenu.add(exportDataItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);
        menuBar.add(windowMenu);

        return menuBar;
    }
}
