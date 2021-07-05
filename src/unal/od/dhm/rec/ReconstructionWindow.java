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
package unal.od.dhm.rec;

import ij.IJ;
import ij.ImageListener;
import ij.ImagePlus;
import ij.gui.ImageWindow;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.WindowEvent;
import java.util.Locale;
import javax.swing.JTextField;

/**
 *
 * @author Carlos Buitrago <cabuitragod@unal.edu.co>
 * @author: Jorge Garcia-Sucerquia <jisucerquia@unal.edu.co>
 */
public class ReconstructionWindow extends ImageWindow implements ImageListener {

    // Construction parameters
    private static final String TITLE_PREFIX = "Reconstruction of ";
    private static final int RECO_FFT = 0;
    private static final int RECO_INTENSITY = 1;
    private static final int RECO_AMPLITUDE = 2;
    private static final int RECO_PHASE = 3;
    protected final LiveReconstruction_ liveReco;
    private ReconstructionSettingsFrame settingFrame = null;
    private final int ID;

    // GUI elements
    private Button snapBtn;
    private Button liveBtn;
    private Button amplitudeBtn;
    private Button intensityBtn;
    private Button phaseBtn;
    private Button fftBtn;
    private Button newFilterBtn;
    private Panel panel;
    private Checkbox logScaleChk;
    private JTextField fpsField;
    private Button settingsBtn;

    // Status Parameters
    private int recoType;
    private boolean log;
    private boolean imageClosed;
    private boolean live;

    public ReconstructionWindow(ImagePlus imp, String name, LiveReconstruction_ liveReco) {
        // Creation
        super(imp);
        imp.setTitle(TITLE_PREFIX + name);
        this.liveReco = liveReco;
        
        this.recoType = RECO_FFT;
        this.log = true;
        this.live = true;
        this.imageClosed = false;
        this.ID = liveReco.getID();
        this.initializeGUI();
    }

    @Override
    public void imageOpened(ImagePlus ip) {
        // Nothing to do
    }

    @Override
    public void imageClosed(ImagePlus ip) {
        if (ip.getID() == this.ID) {
            if (this.live) {
                this.liveReco.stopLive();
                Font font = this.liveBtn.getFont();
                this.liveBtn.setFont(new Font(font.getName(), 0, font.getSize()));
                this.liveBtn.setForeground(Color.BLACK);
            }
            this.imageClosed = true;
        }
    }

    @Override
    public void imageUpdated(ImagePlus ip) {
        // Nothing to do
    }

    @Override
    public void windowClosing(WindowEvent e) {
        this.liveReco.stopLive();
        ImagePlus.removeImageListener((ImageListener) this);
        super.windowClosing(e);
    }

    /**
     * Updates the image information
     *
     * @param newImage - ImagePlus to be displayed
     */
    public void newImage(Object newImage) {
        ImagePlus imp1 = this.getImagePlus();
        if (imp1 != null) {
            imp1.setImage((ImagePlus) newImage);
            imp1.updateAndDraw();
        }
    }

    /**
     * Initializes the GUI elements and their action listeners
     */
    private void initializeGUI() {
        this.panel = new Panel();

        // Snap button: for saving current view
        this.snapBtn = new Button("Snap");
        this.snapBtn.addActionListener((ActionEvent ae) -> {
            ImagePlus snapImp = ReconstructionWindow.this.getImagePlus().duplicate();
            snapImp.setTitle("Snapped Reconstruction");
            snapImp.show();
        });
        this.panel.add(this.snapBtn);
        
        // Live button: to toggle the live visualization
        this.liveBtn = new Button("Live");
        this.liveBtn.addActionListener((ActionEvent ae) -> {
            // If the image is closed, stop
            if (ReconstructionWindow.this.imageClosed) {
                return;
            }
            // Modify the conditions
            if (ReconstructionWindow.this.liveBtn.getForeground() == Color.RED) {
                // Stop the live display
                ReconstructionWindow.this.liveReco.stopLive();
                // Change button appearance
                Font font1 = ReconstructionWindow.this.liveBtn.getFont();
                ReconstructionWindow.this.liveBtn.setFont(new Font(font1.getName(), 0, font1.getSize()));
                ReconstructionWindow.this.liveBtn.setForeground(Color.BLACK);
                // Set the status parameters
                ReconstructionWindow.this.live = false;
                this.fpsField.setText("Static");
            } else {
                // Start the live display
                ReconstructionWindow.this.liveReco.startLive();
                // Change button appearance
                Font font2 = ReconstructionWindow.this.liveBtn.getFont();
                ReconstructionWindow.this.liveBtn.setFont(new Font(font2.getName(), 1, font2.getSize()));
                ReconstructionWindow.this.liveBtn.setForeground(Color.RED);
                // Set the status parameters
                ReconstructionWindow.this.live = true;
                this.fpsField.setText("Live");
            }
        });
        this.panel.add(this.liveBtn);
        
        // New filter button: Sets the current ROI as filter
        this.newFilterBtn = new Button("Set filter");
        this.newFilterBtn.setEnabled(true);
        this.newFilterBtn.addActionListener((ActionEvent ae) -> {
            ReconstructionWindow.this.liveReco.createFilterWindow();
            IJ.setTool(1);
        });
        this.panel.add(this.newFilterBtn);
        
        // Reconstruction buttons: Set the reconstruction type
        // FFT
        this.fftBtn = new Button("FFT");
        this.fftBtn.addActionListener((ActionEvent ae) -> {
            ReconstructionWindow.this.recoType = RECO_FFT;
            //setFFTInteractionEnable(true);
        });
        this.panel.add(this.fftBtn);
        
        // Intensity
        this.intensityBtn = new Button("Intensity");
        this.intensityBtn.addActionListener((ActionEvent ae) -> {
            ReconstructionWindow.this.recoType = RECO_INTENSITY;
            //setFFTInteractionEnable(false);
        });
        this.intensityBtn.setEnabled(false);
        this.panel.add(this.intensityBtn);
        
        // Amplitude
        this.amplitudeBtn = new Button("Amplitude");
        this.amplitudeBtn.addActionListener((ActionEvent ae) -> {
            ReconstructionWindow.this.recoType = RECO_AMPLITUDE;
            //setFFTInteractionEnable(false);
        });
        this.amplitudeBtn.setEnabled(false);
        this.panel.add(this.amplitudeBtn);
        
        // Phase
        this.phaseBtn = new Button("Phase");
        this.phaseBtn.addActionListener((ActionEvent ae) -> {
            ReconstructionWindow.this.recoType = RECO_PHASE;
            //setFFTInteractionEnable(false);
        });
        this.phaseBtn.setEnabled(false);
        this.panel.add(this.phaseBtn);
        
        // FPS Text Field: Updates with the current FPS rate
        this.fpsField = new JTextField(String.format(Locale.US, "%.3f fps", 0.0), 8);
        this.fpsField.setHorizontalAlignment(0);
        this.fpsField.setEditable(false);
        this.panel.add(this.fpsField);
        
        // Logaritmic scaling check box: Allows to toggle visualization
        this.logScaleChk = new Checkbox("Log. Scaling");
        this.logScaleChk.addItemListener((ItemEvent ie) -> {
            ReconstructionWindow.this.log = ReconstructionWindow.this.logScaleChk.getState();
            ReconstructionWindow.this.liveReco.setLogScale(ReconstructionWindow.this.log);
        });
        this.logScaleChk.setEnabled(true);
        this.logScaleChk.setState(this.log);
        this.panel.add(this.logScaleChk);
        
        // Settings button
        this.settingsBtn = new Button("Settings");
        this.settingsBtn.addActionListener((ActionEvent ae) -> {
            ReconstructionWindow.this.showSettings();
        });
        this.settingsBtn.setEnabled(true);
        this.panel.add(this.settingsBtn);
        
        // Pack everything in the window
        this.add((Component) this.panel);
        this.pack();
        ImagePlus.addImageListener((ImageListener) this);
        
        // Set the initial state of the live button
        Font font = this.liveBtn.getFont();
        this.liveBtn.setFont(new Font(font.getName(), 1, font.getSize()));
        this.liveBtn.setForeground(Color.RED);
    }

    public void showSettings() {
        if (settingFrame == null || !settingFrame.isDisplayable()) {
            settingFrame = new ReconstructionSettingsFrame(this);
            settingFrame.setVisible(true);
        } else {
            settingFrame.setState(Frame.NORMAL);
            settingFrame.toFront();
        }
    }
    
    /**
     * Updates the fps count in the window
     *
     * @param fps - New FPS count
     */
    public void setFPS(double fps) {
        this.fpsField.setText(String.format(Locale.US, "%.3f", fps) + " fps");
    }

    public void enableRecoTypes() {
        this.intensityBtn.setEnabled(true);
        this.amplitudeBtn.setEnabled(true);
        this.phaseBtn.setEnabled(true);
    }

    /**
     * Returns the currently selected reconstruction type
     *
     * @return recoType - 0 = FFT, 1 = Intensity, 2 = Amplitude, 3 = Phase
     */
    public int getRecoType() {
        return recoType;
    }

}
