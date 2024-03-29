/*
 * Copyright 2021 Universidad Nacional de Colombia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package unal.od.dhm.sim;

import java.awt.Toolkit;
import java.util.prefs.Preferences;
import unal.od.dhm.PreferencesKeys;

/**
 *
 * @author: Carlos Buitrago-Duque <cabuitragod@unal.edu.co>
 * @author: Jorge Garcia-Sucerquia <jisucerquia@unal.edu.co>
 */
public class ImagingSettingsFrame extends javax.swing.JFrame implements PreferencesKeys {

    private static final String TITLE = "Simulation units";
    private static final String[] UNITS_ABVS = new String[]{"nm", "um", "mm", "cm", "m"};

    //units
    private int lambdaIdx;
    private int tlFocalIdx;
    private int inputSizeIdx;
    
    private boolean isRefValAuto;
    private float refManVal;

    private final Preferences pref;

    private final ImagingFrame parent;

    /**
     * Creates new form SettingsFrame.
     *
     * @param parent
     */
    public ImagingSettingsFrame(ImagingFrame parent) {
        pref = Preferences.userNodeForPackage(getClass());
        this.parent = parent;

        loadPrefs();

        setLocationRelativeTo(parent);
        initComponents();
                
        refValField.setText(String.format("%.2g%n", refManVal));
        autoValCheck.setSelected(isRefValAuto);
        refValField.setEnabled(!isRefValAuto);
    }

    private void loadPrefs() {
        // Units
        lambdaIdx = unitToIdx(pref.get(IMG_LAMBDA_UNITS, "nm"));
        tlFocalIdx = unitToIdx(pref.get(IMG_TL_FOCAL_UNITS, "um"));
        inputSizeIdx = unitToIdx(pref.get(IMG_INPUT_UNITS, "mm"));
        // Reference
        isRefValAuto = pref.getBoolean(REF_AUTO_CHECKED, true);
        refManVal = pref.getFloat(REF_MANUAL_VALUE, 1.0f);
    }

    private int unitToIdx(String unit) {
        switch (unit) {
            case "nm":
                return 0;
            case "um":
                return 1;
            case "mm":
                return 2;
            case "cm":
                return 3;
            default:
                return 4;
        }
    }
    
    private float formatRefVal(String refValString) {
        try {
                if (refValString.isEmpty()) {
                    return 1.0f;
                } else {
                    return Float.parseFloat(refValString);
                }
            } catch (NumberFormatException e) {
                Toolkit.getDefaultToolkit().beep();
                return 1.0f;
            }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cancelBtn = new javax.swing.JButton();
        okBtn = new javax.swing.JButton();
        unitsPanel = new javax.swing.JPanel();
        lambdaCombo = new javax.swing.JComboBox();
        tlFocalCombo = new javax.swing.JComboBox();
        inputSizeCombo = new javax.swing.JComboBox();
        lambdaLabel = new javax.swing.JLabel();
        tlFocalLabel = new javax.swing.JLabel();
        inputSizeLabel = new javax.swing.JLabel();
        referencePanel = new javax.swing.JPanel();
        autoValCheck = new javax.swing.JCheckBox();
        refValLabel = new javax.swing.JLabel();
        refValField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(TITLE);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));
        setResizable(false);

        cancelBtn.setText("Cancel");
        cancelBtn.setMaximumSize(new java.awt.Dimension(70, 23));
        cancelBtn.setMinimumSize(new java.awt.Dimension(70, 23));
        cancelBtn.setPreferredSize(new java.awt.Dimension(70, 23));
        cancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelBtnActionPerformed(evt);
            }
        });

        okBtn.setText("Ok");
        okBtn.setMaximumSize(new java.awt.Dimension(70, 23));
        okBtn.setMinimumSize(new java.awt.Dimension(70, 23));
        okBtn.setPreferredSize(new java.awt.Dimension(70, 23));
        okBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okBtnActionPerformed(evt);
            }
        });

        unitsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Units"));
        unitsPanel.setMaximumSize(new java.awt.Dimension(194, 189));
        unitsPanel.setMinimumSize(new java.awt.Dimension(194, 189));

        lambdaCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Nanometers", "Micrometers", "Millimeters", "Centimeters", "Meters" }));
        lambdaCombo.setSelectedIndex(lambdaIdx);
        lambdaCombo.setMaximumSize(new java.awt.Dimension(83, 20));

        tlFocalCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Nanometers", "Micrometers", "Millimeters", "Centimeters", "Meters" }));
        tlFocalCombo.setSelectedIndex(tlFocalIdx);
        tlFocalCombo.setMaximumSize(new java.awt.Dimension(83, 20));

        inputSizeCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Nanometers", "Micrometers", "Millimeters", "Centimeters", "Meters" }));
        inputSizeCombo.setSelectedIndex(inputSizeIdx);
        inputSizeCombo.setMaximumSize(new java.awt.Dimension(83, 20));

        lambdaLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lambdaLabel.setText("Wavelength:");
        lambdaLabel.setMaximumSize(new java.awt.Dimension(69, 14));
        lambdaLabel.setMinimumSize(new java.awt.Dimension(69, 14));
        lambdaLabel.setPreferredSize(new java.awt.Dimension(69, 14));

        tlFocalLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        tlFocalLabel.setText("TL Focal:");
        tlFocalLabel.setToolTipText("Focal length of the tube lens");
        tlFocalLabel.setMaximumSize(new java.awt.Dimension(69, 14));
        tlFocalLabel.setMinimumSize(new java.awt.Dimension(69, 14));
        tlFocalLabel.setPreferredSize(new java.awt.Dimension(69, 14));

        inputSizeLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        inputSizeLabel.setText("Input size:");
        inputSizeLabel.setToolTipText("");
        inputSizeLabel.setMaximumSize(new java.awt.Dimension(69, 14));
        inputSizeLabel.setMinimumSize(new java.awt.Dimension(69, 14));
        inputSizeLabel.setPreferredSize(new java.awt.Dimension(69, 14));

        javax.swing.GroupLayout unitsPanelLayout = new javax.swing.GroupLayout(unitsPanel);
        unitsPanel.setLayout(unitsPanelLayout);
        unitsPanelLayout.setHorizontalGroup(
            unitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(unitsPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(unitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(unitsPanelLayout.createSequentialGroup()
                        .addComponent(tlFocalLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tlFocalCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(unitsPanelLayout.createSequentialGroup()
                        .addComponent(lambdaLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lambdaCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(unitsPanelLayout.createSequentialGroup()
                        .addComponent(inputSizeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(inputSizeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        unitsPanelLayout.setVerticalGroup(
            unitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(unitsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(unitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lambdaCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lambdaLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(unitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tlFocalCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tlFocalLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(unitsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inputSizeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inputSizeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        referencePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Reference"));

        autoValCheck.setSelected(true);
        autoValCheck.setText("Auto set value");
        autoValCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoValCheckActionPerformed(evt);
            }
        });

        refValLabel.setText("Reference value");

        refValField.setText("1");
        refValField.setEnabled(false);

        javax.swing.GroupLayout referencePanelLayout = new javax.swing.GroupLayout(referencePanel);
        referencePanel.setLayout(referencePanelLayout);
        referencePanelLayout.setHorizontalGroup(
            referencePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(referencePanelLayout.createSequentialGroup()
                .addGroup(referencePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(referencePanelLayout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(autoValCheck)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(referencePanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(refValLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(refValField)))
                .addContainerGap())
        );
        referencePanelLayout.setVerticalGroup(
            referencePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(referencePanelLayout.createSequentialGroup()
                .addComponent(autoValCheck)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(referencePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(refValLabel)
                    .addComponent(refValField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(okBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(unitsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(referencePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(unitsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(referencePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(okBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okBtnActionPerformed
        pref.put(IMG_LAMBDA_UNITS, UNITS_ABVS[lambdaCombo.getSelectedIndex()]);
        pref.put(IMG_TL_FOCAL_UNITS, UNITS_ABVS[tlFocalCombo.getSelectedIndex()]);
        pref.put(IMG_INPUT_UNITS, UNITS_ABVS[inputSizeCombo.getSelectedIndex()]);
        
        refManVal = formatRefVal(refValField.getText());
        
        pref.putBoolean(REF_AUTO_CHECKED,autoValCheck.isSelected());
        pref.putFloat(REF_MANUAL_VALUE, refManVal);

        parent.updateUnitsPrefs();
        parent.updateReferenceSettings(this.autoValCheck.isSelected(), 
                refValField.getText());

        setVisible(false);
        dispose();
    }//GEN-LAST:event_okBtnActionPerformed

    private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBtnActionPerformed
        setVisible(false);
        dispose();
    }//GEN-LAST:event_cancelBtnActionPerformed

    private void autoValCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoValCheckActionPerformed
        this.refValField.setEnabled(!this.autoValCheck.isSelected());
    }//GEN-LAST:event_autoValCheckActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox autoValCheck;
    private javax.swing.JButton cancelBtn;
    private javax.swing.JComboBox inputSizeCombo;
    private javax.swing.JLabel inputSizeLabel;
    private javax.swing.JComboBox lambdaCombo;
    private javax.swing.JLabel lambdaLabel;
    private javax.swing.JButton okBtn;
    private javax.swing.JTextField refValField;
    private javax.swing.JLabel refValLabel;
    private javax.swing.JPanel referencePanel;
    private javax.swing.JComboBox tlFocalCombo;
    private javax.swing.JLabel tlFocalLabel;
    private javax.swing.JPanel unitsPanel;
    // End of variables declaration//GEN-END:variables
}
