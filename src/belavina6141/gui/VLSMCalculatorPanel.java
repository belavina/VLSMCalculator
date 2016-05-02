package belavina6141.gui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Olga Belavina on 2016-03-31.
 */


public class VLSMCalculatorPanel extends JPanel {


    private JLabel subnetLabel;
    private JLabel netIpLabel;
    private JLabel howManyLabel;

    private JButton doneBtn;

    private JTextField ipAddress;
    private JSpinner numOfSubnetsSp;
    private static int numOfMax = 0;

    private List<ParamCombo> paramComboList = new ArrayList<>();
    private ArrayList<SubnetField> subnetFields = new ArrayList<>();

    private Box subnetBox;


    /* === Event Listener === */
    private VLSMCalcListener calcListener;

    public void setCalcListener(VLSMCalcListener calcListener) {
        this.calcListener = calcListener;
    }


    class ParamCombo extends JComboBox {

        DefaultComboBoxModel paramModel = new DefaultComboBoxModel();

        ParamCombo() {
            super();

            paramModel.addElement("Min");
            paramModel.addElement("Max");
            paramModel.addElement("Bal");

            this.setModel(paramModel);
            paramModel.setSelectedItem("Min");
        }
    }


    class SubnetField {

        ParamCombo paramCombo;
        JTextField subnetName;
        JTextField numOfHosts;

        SubnetField(JTextField numOfHosts,JTextField subnetName,ParamCombo paramCombo ) {
            this.paramCombo = paramCombo;
            this.subnetName = subnetName;
            this.numOfHosts = numOfHosts;
        }

        @Override
        public String toString() {
            return "SubnetField{" +
                    "paramCombo=" + paramCombo +
                    ", subnetNames=" + subnetName +
                    ", numOfHosts=" + numOfHosts +
                    '}';
        }
    }



    VLSMCalculatorPanel() {

        setBackground(new Color(244,244,244));
        /* Initializing Labels & Fields */

        netIpLabel         = new JLabel("Network IP in CIDR notation");
        ipAddress          = new JTextField(10);
        ipAddress.setText("192.168.1.0/22");

        howManyLabel       = new JLabel("How many subnets do you need?");
        subnetLabel        = new JLabel("Subnet Names  Num of Hosts");

        /* Buttons */
        setOpaque(true);
        doneBtn            = new JButton("Calculate");
        doneBtn.setBackground(Color.BLACK);
        doneBtn.setForeground(new Color(0, 119, 251));
        doneBtn.setFont(new Font("", Font.PLAIN, 16));
        doneBtn.setSize(80,80);

        subnetBox          = Box.createVerticalBox();

        SpinnerModel numOfSubnetsSpModel  = new SpinnerNumberModel(5,
                1,
                999,
                1);

        numOfSubnetsSp = new JSpinner(numOfSubnetsSpModel);


        numOfSubnetsSp.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (! ipAddress.getText().trim().isEmpty()) {

                    int numOfSubnets = (int)numOfSubnetsSp.getValue();
                    addNewSubnetFields(numOfSubnets);

                } // else : do nothing
            }

        });

        doneBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                VLSMCalcEvent calcEvent = new VLSMCalcEvent(this, ipAddress.getText());

                for (int i= 0; i < subnetFields.size(); i++) {
                    if (!subnetFields.get(i).numOfHosts.getText().trim().isEmpty()) {
                        Long hosts = Long.parseLong(subnetFields.get(i).numOfHosts.getText());

                        String subnetName = new String();

                        if (subnetFields.get(i).subnetName.getText().trim().isEmpty()) {
                            subnetName = "Host "+(i+1);
                        } else {
                            subnetName = subnetFields.get(i).subnetName.getText();
                        }

                        calcEvent.add(subnetName, hosts, subnetFields.get(i).paramCombo.getSelectedIndex());
                    }
                }

                if(calcListener != null ) {
                    calcListener.VLSMCalclEventOccurred(calcEvent);
                }
            }
        });



      /* === Adding border  === */
        Border outerBorder = BorderFactory.createLineBorder(new Color(110,110,110), 3);
        Border innerBorder = BorderFactory.createEmptyBorder(14, 14, 14, 14);
        setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));


        /* == Working with the layout */
        setupGridLayout();
        addNewSubnetFields(5);

    }

    private void setupGridLayout() {

        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        /////////////////// NEW ROW ///////////////////
        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridy++;


        /* One cell takes one X one */

        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LINE_START;
        add(netIpLabel, gc);

        /* Add space between label and the field*/
        gc.insets = new Insets(0, 0, 0, 5);

        /////////////////// NEW ROW ///////////////////
        gc.weightx = 0;
        gc.weighty = 0.1;

        gc.gridx = 0;
        gc.gridy++;

        gc.insets = new Insets(0,0,0,0);
        gc.anchor = GridBagConstraints.LINE_START;
        add(ipAddress, gc);

        /////////////////// NEW ROW ///////////////////

        gc.weightx = 0;
        gc.weighty = 0;
        gc.gridx = 0;
        gc.gridy++;
        gc.insets = new Insets(0,0,0,0);
        gc.anchor = GridBagConstraints.LINE_START;
        add(howManyLabel, gc);


        /////////////////// NEW ROW ///////////////////

        gc.gridx = 0;
        gc.gridy++;
        gc.insets = new Insets(0,0,0,0);
        gc.anchor = GridBagConstraints.FIRST_LINE_START;
        add(numOfSubnetsSp, gc);

        /////////////////// NEW ROW ///////////////////

        gc.gridx = 0;
        gc.gridy++;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LINE_START;

        add(subnetLabel, gc);

        /////////////////// NEW ROW ///////////////////

        gc.gridx = 0;
        gc.gridy++;
        gc.insets = new Insets(0,0,0,0);
        gc.anchor = GridBagConstraints.LINE_START;

        add(subnetBox, gc);

        /////////////////// NEW ROW ///////////////////
        gc.weighty = 30;
        gc.weightx = 2;

        gc.gridy++;
        gc.gridx = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LAST_LINE_START;
        add(doneBtn, gc);

    }

    private void addNewSubnetFields(int numOfSubnets) {

        if ( numOfSubnets < subnetFields.size()){

            int size = subnetFields.size();
            for (int i = numOfSubnets; i < size; i++) {
                subnetBox.remove(subnetFields.size() - 1);
                subnetFields.remove(subnetFields.size() - 1);
                paramComboList.remove(paramComboList.size()-1);
            }
        } else {



            for (int i = subnetFields.size(); i < numOfSubnets; i++) {


                Box box = Box.createHorizontalBox();

                JTextField numOfHosts = new JTextField(5) {

                    /* Accept only numbers to the input text field (number of hosts) */
                    public void processKeyEvent(KeyEvent ev) {
                        char c = ev.getKeyChar();
                        try {
                            // Ignore all non-printable characters. Just check the printable ones.
                            if (c > 31 && c < 127) {
                                Integer.parseInt(c + "");
                            }
                            super.processKeyEvent(ev);
                        } catch (NumberFormatException nfe) {
                            // Do nothing. Character inputted is not a number, so ignore it.
                        }
                    }
                };

                JTextField   name = new JTextField(7);
                numOfHosts.setToolTipText("Number of hosts");
                name.setToolTipText("Subnet Name");

                ParamCombo paramCombo = new ParamCombo();
                paramComboList.add(paramCombo);

                paramCombo.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        if(paramCombo.getSelectedItem() == "Max"){
                            for(ParamCombo pc : paramComboList){
                                if (pc.getSelectedItem() == "Max" && !pc.equals(paramCombo)){
                                    paramCombo.setSelectedItem("Bal");
                                    pc.setSelectedItem("Bal");
                                }
                            }
                        }
                    }
                });


                subnetFields.add(new SubnetField(numOfHosts, name, paramCombo));

                box.add(name);
                box.add(numOfHosts);
                box.add(paramCombo);

                subnetBox.add(box);

            }
        }

        revalidate();
        validate();
    }


}
