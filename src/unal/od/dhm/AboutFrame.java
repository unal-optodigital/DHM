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

package unal.od.dhm;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author: Carlos Buitrago-Duque <cabuitragod@unal.edu.co>
 * @author: Jorge Garcia-Sucerquia <jisucerquia@unal.edu.co>
 */
public class AboutFrame extends javax.swing.JFrame {

    /**
     * Creates new form AboutFrame.
     */
    public AboutFrame() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        logoLabel = new javax.swing.JLabel();
        titleLabel = new javax.swing.JLabel();
        versionLabel = new javax.swing.JLabel();
        devsLabel = new javax.swing.JLabel();
        raulLabel = new javax.swing.JLabel();
        groupLabel = new javax.swing.JLabel();
        univLabel = new javax.swing.JLabel();
        closeBtn = new javax.swing.JButton();
        raulLabel1 = new javax.swing.JLabel();
        devsLabel1 = new javax.swing.JLabel();
        webLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("About DHM");
        setBounds(new java.awt.Rectangle(400, 400, 408, 205));
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));
        setResizable(false);

        logoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/logoOD.png"))); // NOI18N

        titleLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        titleLabel.setText("DHM Imaging Plugin");

        versionLabel.setText("Version 1.0, May 2021");

        devsLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        devsLabel.setText("Developed by:");

        raulLabel.setText("Carlos Buitrago-Duque");

        groupLabel.setText("Opto-digital Processing Group");

        univLabel.setText("Universidad Nacional de Colombia - Sede Medellín");

        closeBtn.setText("Close");
        closeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeBtnActionPerformed(evt);
            }
        });

        raulLabel1.setText("Jorge Garcia-Sucerquia");

        devsLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        devsLabel1.setText("License pending");

        webLabel.setText("<html><a href=\\\"\\\">https://github.com/unal-optodigital/DHM</a></html>");
        webLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        webLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                webLabelMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(logoLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(webLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(versionLabel)
                            .addComponent(titleLabel)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(closeBtn, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(devsLabel)
                                    .addComponent(raulLabel)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(groupLabel)
                                            .addGap(68, 68, 68))
                                        .addComponent(univLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                                    .addComponent(raulLabel1))
                                .addComponent(devsLabel1)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(logoLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(titleLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(versionLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(devsLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(devsLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(raulLabel)
                        .addGap(6, 6, 6)
                        .addComponent(raulLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(groupLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(univLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(webLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeBtn)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeBtnActionPerformed
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeBtnActionPerformed

    private void webLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_webLabelMouseClicked
        try {
            Desktop.getDesktop().
                    browse(new URI("https://unal-optodigital.github.io/"));
        } catch (IOException e) {
            //It looks like there's a problem
        } catch (URISyntaxException e) {
            //It looks like there's a problem
        }
    }//GEN-LAST:event_webLabelMouseClicked

//    ImageIcon icon = new ImageIcon("/icon.png");

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeBtn;
    private javax.swing.JLabel devsLabel;
    private javax.swing.JLabel devsLabel1;
    private javax.swing.JLabel groupLabel;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JLabel raulLabel;
    private javax.swing.JLabel raulLabel1;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JLabel univLabel;
    private javax.swing.JLabel versionLabel;
    private javax.swing.JLabel webLabel;
    // End of variables declaration//GEN-END:variables
}
