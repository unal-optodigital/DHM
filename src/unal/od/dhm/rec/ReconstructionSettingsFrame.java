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

import java.awt.Toolkit;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.prefs.Preferences;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import unal.od.dhm.PreferencesKeys;

/**
 *
 * @author: Carlos Buitrago-Duque <cabuitragod@unal.edu.co>
 * @author: Jorge Garcia-Sucerquia <jisucerquia@unal.edu.co>
 * 
 * Some functions keep the implementation set in previous plugins from the
 * Opto-digital Processing Group, as designed by 
 * @author: Pablo Piedrahita-Quintero <jppiedrahitaq@unal.edu.co>
 */
public class ReconstructionSettingsFrame extends javax.swing.JFrame implements PreferencesKeys {

    // Auxiliary objects
    private final ParametersVerifier verifier;
    private final ReconstructionWindow parentFrame;
    
    // Adimensional user inputs
    private float moMagUser;
    private float moNAUser;
    private float fxTuneUser = 0.0f;
    private float fyTuneUser = 0.0f;
    private float linearTuneUser = 0.0f;
    
    // User inputs in user units
    private float lambdaUser;   
    private float tlFocalUser;
    private float inputWUser;
    private float inputHUser;
    private float zTuneUser = 0.0f;

    // User inputs converted to um
    private float lambdaUm; 
    private float tlFocalUm;
    private float inputWUm;
    private float inputHUm;
    private float zTuneUm = 0.0f;
    
    // Boolean for the user parameters
    private boolean lambdaSet;
    private boolean moMagSet;
    private boolean moNASet;
    private boolean tlFocalSet;
    private boolean inputWSet;
    private boolean inputHSet;
    
    // Optional parameters, default to 0.0 without pref loading
    private boolean zTuneSet = true;
    private boolean fxTuneSet = true;
    private boolean fyTuneSet = true;
    private boolean linearTuneSet = true;

    // Formatter
    private final DecimalFormat df;

    // Preferences
    private final Preferences pref;

    // Frame location
    private int locX;
    private int locY;

    // Last parameters used
    private String lambdaString;
    private String moMagString;
    private String moNAString;
    private String tlFocalString;
    private String inputWString;
    private String inputHString;
    private boolean difLimitEnabled;

    // Parameters units
    final private String lambdaUnits = "nm";
    final private String tlFocalUnits = "mm";
    final private String inputSizeUnits = "mm";
    final private String zTuneUnits = "um";

    /**
     * Creates the main frame
     * 
     * @param parentFrame Main reconstruction window that calls the settings
     */
    public ReconstructionSettingsFrame(ReconstructionWindow parentFrame) {
        // Save the parent reference
        this.parentFrame = parentFrame;
        
        // Initialize auxiliar objects
        df = new DecimalFormat("#.####", new DecimalFormatSymbols(Locale.US));
        pref = Preferences.userNodeForPackage(getClass());
        verifier = new ParametersVerifier();

        // Gets the current open images and load the last preferences
        loadPrefs();

        // Initialize the GUI
        initComponents();
    }

    /**
     * Saves the preferences when the frame is closed.
     */
    private void savePrefs() {
        // Frame location
        pref.putInt(FRAME_LOC_X, getLocation().x);
        pref.putInt(FRAME_LOC_Y, getLocation().y);

        // Imaging Parameters fields
        pref.putFloat(REC_LAMBDA, lambdaUser);
        pref.putFloat(REC_MO_MAGNIFICATION, moMagUser);
        pref.putFloat(REC_MO_NA, moNAUser);
        pref.putFloat(REC_TL_FOCAL, tlFocalUser);
        pref.putFloat(REC_INPUT_WIDTH, inputWUser);
        pref.putFloat(REC_INPUT_HEIGHT, inputHUser);
    }

    /**
     * Loads the preferences when the plugin starts.
     */
    private void loadPrefs() {
        // Frame location
        locX = pref.getInt(FRAME_LOC_X, 300);
        locY = pref.getInt(FRAME_LOC_Y, 300);

        // Parameters strings for text input fields
        loadParameters();
    }
    
    /**
     * Loads the last used parameters.
     */
    private void loadParameters() {
        // Gets the saved floats and checks for NaN values
        lambdaUser = pref.getFloat(REC_LAMBDA, Float.NaN);
        if (Float.isNaN(lambdaUser)) {
            lambdaSet = false;
            lambdaString = "";
        } else {
            lambdaSet = true;
            lambdaString = df.format(lambdaUser);
            lambdaUm = unitsToUm(lambdaUser, lambdaUnits);
        }

        moMagUser = pref.getFloat(REC_MO_MAGNIFICATION, Float.NaN);
        if (Float.isNaN(moMagUser)) {
            moMagSet = false;
            moMagString = "";
        } else {
            moMagSet = true;
            moMagString = df.format(moMagUser);
        }
        
        moNAUser = pref.getFloat(REC_MO_NA, Float.NaN);
        if (Float.isNaN(moNAUser)) {
            moNASet = false;
            moNAString = "";
        } else {
            moNASet = true;
            moNAString = df.format(moNAUser);
        }
        
        tlFocalUser = pref.getFloat(REC_TL_FOCAL, Float.NaN);
        if (Float.isNaN(tlFocalUser)) {
            tlFocalSet = false;
            tlFocalString = "";
        } else {
            tlFocalSet = true;
            tlFocalString = df.format(tlFocalUser);
            tlFocalUm = unitsToUm(tlFocalUser, tlFocalUnits);
        }

        inputWUser = pref.getFloat(REC_INPUT_WIDTH, Float.NaN);
        if (Float.isNaN(inputWUser)) {
            inputWSet = false;
            inputWString = "";
        } else {
            inputWSet = true;
            inputWString = df.format(inputWUser);
            inputWUm = unitsToUm(inputWUser, inputSizeUnits);
        }

        inputHUser = pref.getFloat(REC_INPUT_HEIGHT, Float.NaN);
        if (Float.isNaN(inputHUser)) {
            inputHSet = false;
            inputHString = "";
        } else {
            inputHSet = true;
            inputHString = df.format(inputHUser);
            inputHUm = unitsToUm(inputHUser, inputSizeUnits);
        }

    }
    
    /**
     * Helper method to convert from {units} to um.
     *
     * @param val
     * @param units
     * @return
     */
    private float unitsToUm(float val, String units) {
        switch (units) {
            case "nm":
                return val * 1E-3f;
            case "um":
                return val;
            case "mm":
                return val * 1E3f;
            case "cm":
                return val * 1E4f;
            case "m":
                return val * 1E6f;
            default:
                break;
        }

        return val;
    }

    /**
     * Helper method to convert from um to {units}.
     *
     * @param val
     * @param units
     * @return
     */
    private float umToUnits(float val, String units) {
        switch (units) {
            case "nm":
                return val * 1E3f;
            case "mm":
                return val * 1E-3f;
            case "cm":
                return val * 1E-4f;
            case "m":
                return val * 1E-6f;
            default:
                break;
        }

        return val;
    }
    
    private boolean parseValues() {

        // Imaging parameters
        boolean imagParSet = lambdaSet && inputWSet && inputHSet;
        int N = parentFrame.getImagePlus().getWidth();
        int M = parentFrame.getImagePlus().getHeight();
        if(imagParSet) {
            parentFrame.liveReco.setImagingParameters(lambdaUm, inputWUm / N, inputHUm / M);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid imaging conditions");
            return false;
        }
        
        // Microscope parameters
        boolean microParSet = moNASet && moMagSet && tlFocalSet;
        if(microParSet) {
            parentFrame.liveReco.setMicroscopeParameters(moMagUser, moNAUser, tlFocalUm);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid microscope values");
            return false;
        }
        
        // Fine-tunning parameters
        boolean tunningParSet = zTuneSet && fxTuneSet && fyTuneSet && linearTuneSet;   
        if(tunningParSet) {
            parentFrame.liveReco.setTuningParameters(zTuneUm, fxTuneUser, fyTuneUser, linearTuneUser);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid fine-tunning values");
            return false;
        }        

        return true;
    }
    
    /**
     * Verifies the inputs when a text field is modified
     */
    private class ParametersVerifier extends InputVerifier {

        @Override
        public boolean verify(JComponent input) {

            boolean valid;

            if (input == lambdaField) {
                valid = checkLambdaField();
            } else if (input == moMagField) {
                valid = checkMOMagField();
            } else if (input == moNAField) {
                valid = checkMONAField();
            } else if (input == tlFocalField) {
                valid = checkTlFocalField();
            } else if (input == inputWField) {
                valid = checkInputWField();
            } else if (input == inputHField) {
                valid = checkInputHField();
            } else if (input == fxTuneField) {
                valid = checkFxField();
            } else if (input == fyTuneField) {
                valid = checkFyField();
            } else if (input == linearTuneField) {
                valid = checkLinField();
            } else if (input == zTuneField) {
                valid = checkZField();            
            } else {
                valid = true;
            }

            return valid;
        }

        private boolean checkLambdaField() {
            try {
                String txt = lambdaField.getText();

                if (txt.isEmpty()) {
                    lambdaSet = false;
                    lambdaUser = Float.NaN;
                    return true;
                }

                lambdaUser = Float.parseFloat(txt);

                if (lambdaUser <= 0) {
                    Toolkit.getDefaultToolkit().beep();
                    lambdaField.selectAll();

                    lambdaSet = false;
                    lambdaUser = Float.NaN;
                    return false;
                }

                lambdaUm = unitsToUm(lambdaUser, lambdaUnits);

                lambdaSet = true;
                return true;

            } catch (NumberFormatException e) {
                Toolkit.getDefaultToolkit().beep();
                lambdaField.selectAll();

                lambdaSet = false;
                lambdaUser = Float.NaN;
                return false;
            }
        }

        private boolean checkMOMagField() {
            try {
                String txt = moMagField.getText();

                if (txt.isEmpty()) {
                    moMagSet = false;
                    moMagUser = Float.NaN;
                    return true;
                }

                moMagUser = Float.parseFloat(txt);

                if (moMagUser <= 0) {
                    Toolkit.getDefaultToolkit().beep();
                    moMagField.selectAll();

                    moMagSet = false;
                    moMagUser = Float.NaN;
                    return false;
                }

                moMagSet = true;
                return true;

            } catch (NumberFormatException e) {
                Toolkit.getDefaultToolkit().beep();
                moMagField.selectAll();

                moMagSet = false;
                moMagUser = Float.NaN;
                return false;
            }
        }
        
        private boolean checkMONAField() {
            try {
                String txt = moNAField.getText();

                if (txt.isEmpty()) {
                    moNASet = false;
                    moNAUser = Float.NaN;
                    return true;
                }

                moNAUser = Float.parseFloat(txt);

                if (moNAUser <= 0 || moNAUser > 1) {
                    Toolkit.getDefaultToolkit().beep();
                    moNAField.selectAll();

                    moNASet = false;
                    moNAUser = Float.NaN;
                    return false;
                }

                moNASet = true;
                return true;

            } catch (NumberFormatException e) {
                Toolkit.getDefaultToolkit().beep();
                moNAField.selectAll();

                moNASet = false;
                moNAUser = Float.NaN;
                return false;
            }
        }
        
        private boolean checkTlFocalField() {
            try {
                String txt = tlFocalField.getText();

                if (txt.isEmpty()) {
                    tlFocalSet = false;
                    tlFocalUser = Float.NaN;
                    return true;
                }

                tlFocalUser = Float.parseFloat(txt);

                if (tlFocalUser <= 0) {
                    Toolkit.getDefaultToolkit().beep();
                    tlFocalField.selectAll();

                    tlFocalSet = false;
                    tlFocalUser = Float.NaN;
                    return false;
                }

                tlFocalUm = unitsToUm(tlFocalUser, tlFocalUnits);

                tlFocalSet = true;
                return true;

            } catch (NumberFormatException e) {
                Toolkit.getDefaultToolkit().beep();
                tlFocalField.selectAll();

                tlFocalSet = false;
                tlFocalUser = Float.NaN;
                return false;
            }
        }
        
        private boolean checkInputWField() {
            try {
                String txt = inputWField.getText();

                if (txt.isEmpty()) {
                    inputWSet = false;
                    inputWUser = Float.NaN;
                    return true;
                }

                inputWUser = Float.parseFloat(txt);

                if (inputWUser <= 0) {
                    Toolkit.getDefaultToolkit().beep();
                    inputWField.selectAll();

                    inputWSet = false;
                    inputWUser = Float.NaN;
                    return false;
                }

                inputWUm = unitsToUm(inputWUser, inputSizeUnits);

                inputWSet = true;
                return true;

            } catch (NumberFormatException e) {
                Toolkit.getDefaultToolkit().beep();
                inputWField.selectAll();

                inputWSet = false;
                inputWUser = Float.NaN;
                return false;
            }
        }

        private boolean checkInputHField() {
            try {
                String txt = inputHField.getText();

                if (txt.isEmpty()) {
                    inputHSet = false;
                    inputHUser = Float.NaN;
                    return true;
                }

                inputHUser = Float.parseFloat(txt);

                if (inputHUser <= 0) {
                    Toolkit.getDefaultToolkit().beep();
                    inputWField.selectAll();

                    inputHSet = false;
                    inputHUser = Float.NaN;
                    return false;
                }

                inputHUm = unitsToUm(inputHUser, inputSizeUnits);

                inputHSet = true;
                return true;

            } catch (NumberFormatException e) {
                Toolkit.getDefaultToolkit().beep();
                inputWField.selectAll();

                inputHSet = false;
                inputHUser = Float.NaN;
                return false;
            }
        }

        private boolean checkZField() {
            try {
                String txt = zTuneField.getText();

                if (txt.isEmpty()) {
                    zTuneSet = false;
                    zTuneUser = Float.NaN;
                    return true;
                }

                zTuneUser = Float.parseFloat(txt);
                zTuneUm = unitsToUm(zTuneUser, zTuneUnits);

                zTuneSet = true;
                return true;

            } catch (NumberFormatException e) {
                Toolkit.getDefaultToolkit().beep();
                zTuneField.selectAll();

                zTuneSet = false;
                zTuneUser = Float.NaN;
                return false;
            }
        }

        private boolean checkFxField() {
            try {
                String txt = fxTuneField.getText();

                if (txt.isEmpty()) {
                    fxTuneSet = false;
                    fxTuneUser = Float.NaN;
                    return true;
                }

                fxTuneUser = Float.parseFloat(txt);                
                fxTuneSet = true;
                return true;

            } catch (NumberFormatException e) {
                Toolkit.getDefaultToolkit().beep();
                fxTuneField.selectAll();

                fxTuneSet = false;
                fxTuneUser = Float.NaN;
                return false;
            }
        }

        private boolean checkFyField() {
            try {
                String txt = fyTuneField.getText();

                if (txt.isEmpty()) {
                    fyTuneSet = false;
                    fyTuneUser = Float.NaN;
                    return true;
                }

                fyTuneUser = Float.parseFloat(txt);
                fyTuneSet = true;
                return true;

            } catch (NumberFormatException e) {
                Toolkit.getDefaultToolkit().beep();
                fyTuneField.selectAll();

                fyTuneSet = false;
                fyTuneUser = Float.NaN;
                return false;
            }
        }
        
        private boolean checkLinField() {
            try {
                String txt = linearTuneField.getText();

                if (txt.isEmpty()) {
                    linearTuneSet = false;
                    linearTuneUser = Float.NaN;
                    return true;
                }

                linearTuneUser = Float.parseFloat(txt);
                linearTuneSet = true;
                return true;

            } catch (NumberFormatException e) {
                Toolkit.getDefaultToolkit().beep();
                linearTuneField.selectAll();

                linearTuneSet = false;
                linearTuneUser = Float.NaN;
                return false;
            }
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

        outputGroup = new javax.swing.ButtonGroup();
        parametersImagingPanel = new javax.swing.JPanel();
        lambdaField = new javax.swing.JTextField();
        moNAField = new javax.swing.JTextField();
        tlFocalField = new javax.swing.JTextField();
        inputWField = new javax.swing.JTextField();
        inputHField = new javax.swing.JTextField();
        inputHLabel = new javax.swing.JLabel();
        inputWLabel = new javax.swing.JLabel();
        tlFocalLabel = new javax.swing.JLabel();
        moNALabel = new javax.swing.JLabel();
        lambdaLabel = new javax.swing.JLabel();
        moMagLabel = new javax.swing.JLabel();
        moMagField = new javax.swing.JTextField();
        fineTunePanel = new javax.swing.JPanel();
        zTuneLabel = new javax.swing.JLabel();
        fxTuneField = new javax.swing.JTextField();
        fyTuneField = new javax.swing.JTextField();
        fxTuneLabel = new javax.swing.JLabel();
        fyTuneLabel = new javax.swing.JLabel();
        zTuneField = new javax.swing.JTextField();
        reduceZBtn = new javax.swing.JButton();
        increaseZBtn = new javax.swing.JButton();
        titPhaseCompLabel = new javax.swing.JLabel();
        titPhaseCompLabel1 = new javax.swing.JLabel();
        linearTuneField = new javax.swing.JTextField();
        linearTuneLabel = new javax.swing.JLabel();
        buttonPanel = new javax.swing.JPanel();
        applyBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Reconstruction settings");
        setBounds(new java.awt.Rectangle(locX, locY, 0, 0)
        );
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        parametersImagingPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Imaging Parameters"));
        parametersImagingPanel.setMaximumSize(new java.awt.Dimension(255, 301));
        parametersImagingPanel.setMinimumSize(new java.awt.Dimension(255, 301));

        lambdaField.setText(lambdaString);
        lambdaField.setToolTipText("Wavelength must be a positive number and different from 0.");
        lambdaField.setInputVerifier(verifier);
        lambdaField.setMaximumSize(new java.awt.Dimension(115, 20));
        lambdaField.setMinimumSize(new java.awt.Dimension(115, 20));
        lambdaField.setPreferredSize(new java.awt.Dimension(115, 20));
        lambdaField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        moNAField.setText(moNAString);
        moNAField.setToolTipText("Numerical Aperture of the MO must be a positive floating point number.");
        moNAField.setInputVerifier(verifier);
        moNAField.setMaximumSize(new java.awt.Dimension(115, 20));
        moNAField.setMinimumSize(new java.awt.Dimension(115, 20));
        moNAField.setPreferredSize(new java.awt.Dimension(115, 20));
        moNAField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        tlFocalField.setText(tlFocalString);
        tlFocalField.setToolTipText("Focal length of the TL must be a floating point number different from 0.");
        tlFocalField.setInputVerifier(verifier);
        tlFocalField.setMaximumSize(new java.awt.Dimension(115, 20));
        tlFocalField.setMinimumSize(new java.awt.Dimension(115, 20));
        tlFocalField.setPreferredSize(new java.awt.Dimension(115, 20));
        tlFocalField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        inputWField.setText(inputWString);
        inputWField.setToolTipText("Input width must be a positive number and different from 0.");
        inputWField.setEnabled(true);
        inputWField.setInputVerifier(verifier);
        inputWField.setMaximumSize(new java.awt.Dimension(83, 20));
        inputWField.setMinimumSize(new java.awt.Dimension(83, 20));
        inputWField.setPreferredSize(new java.awt.Dimension(83, 20));
        inputWField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        inputHField.setText(inputHString);
        inputHField.setToolTipText("Input height must be a positive number and different from 0.");
        inputHField.setEnabled(true);
        inputHField.setInputVerifier(verifier);
        inputHField.setMaximumSize(new java.awt.Dimension(83, 20));
        inputHField.setMinimumSize(new java.awt.Dimension(83, 20));
        inputHField.setPreferredSize(new java.awt.Dimension(83, 20));
        inputHField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        inputHLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        inputHLabel.setText("Input height [mm]:");
        inputHLabel.setEnabled(true);
        inputHLabel.setMaximumSize(new java.awt.Dimension(100, 14));
        inputHLabel.setMinimumSize(new java.awt.Dimension(100, 14));
        inputHLabel.setPreferredSize(new java.awt.Dimension(100, 14));

        inputWLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        inputWLabel.setText("Input width [mm]:");
        inputWLabel.setEnabled(true);
        inputWLabel.setMaximumSize(new java.awt.Dimension(100, 14));
        inputWLabel.setMinimumSize(new java.awt.Dimension(100, 14));
        inputWLabel.setPreferredSize(new java.awt.Dimension(100, 14));

        tlFocalLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        tlFocalLabel.setText("TL focal [mm]:");
        tlFocalLabel.setMaximumSize(new java.awt.Dimension(100, 14));
        tlFocalLabel.setMinimumSize(new java.awt.Dimension(100, 14));
        tlFocalLabel.setPreferredSize(new java.awt.Dimension(100, 14));

        moNALabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        moNALabel.setText("MO NA [adim]:");
        moNALabel.setMaximumSize(new java.awt.Dimension(100, 14));
        moNALabel.setMinimumSize(new java.awt.Dimension(100, 14));
        moNALabel.setPreferredSize(new java.awt.Dimension(100, 14));

        lambdaLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lambdaLabel.setText("Wavelength [nm]:");
        lambdaLabel.setMaximumSize(new java.awt.Dimension(100, 14));
        lambdaLabel.setMinimumSize(new java.awt.Dimension(100, 14));
        lambdaLabel.setPreferredSize(new java.awt.Dimension(100, 14));

        moMagLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        moMagLabel.setText("MO Magn. [X]:");
        moMagLabel.setToolTipText("Magnification of the microscope objective");
        moMagLabel.setMaximumSize(new java.awt.Dimension(100, 14));
        moMagLabel.setMinimumSize(new java.awt.Dimension(100, 14));
        moMagLabel.setPreferredSize(new java.awt.Dimension(100, 14));

        moMagField.setText(moMagString);
        moMagField.setToolTipText("Magnification of the MO must be a positive number and different from 0.");
        moMagField.setInputVerifier(verifier);
        moMagField.setMaximumSize(new java.awt.Dimension(115, 20));
        moMagField.setMinimumSize(new java.awt.Dimension(115, 20));
        moMagField.setPreferredSize(new java.awt.Dimension(115, 20));
        moMagField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
        });

        javax.swing.GroupLayout parametersImagingPanelLayout = new javax.swing.GroupLayout(parametersImagingPanel);
        parametersImagingPanel.setLayout(parametersImagingPanelLayout);
        parametersImagingPanelLayout.setHorizontalGroup(
            parametersImagingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(parametersImagingPanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(parametersImagingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(parametersImagingPanelLayout.createSequentialGroup()
                        .addGroup(parametersImagingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tlFocalLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(moNALabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lambdaLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(parametersImagingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tlFocalField, javax.swing.GroupLayout.PREFERRED_SIZE, 83, Short.MAX_VALUE)
                            .addComponent(lambdaField, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                            .addComponent(moNAField, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)))
                    .addGroup(parametersImagingPanelLayout.createSequentialGroup()
                        .addGroup(parametersImagingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(inputHLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(inputWLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(parametersImagingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(inputHField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(inputWField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(parametersImagingPanelLayout.createSequentialGroup()
                        .addComponent(moMagLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(moMagField, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        parametersImagingPanelLayout.setVerticalGroup(
            parametersImagingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(parametersImagingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(parametersImagingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lambdaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lambdaLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(parametersImagingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(moMagField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(moMagLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(parametersImagingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(moNAField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(moNALabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(parametersImagingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tlFocalField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tlFocalLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(parametersImagingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inputWLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inputWField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(parametersImagingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inputHLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inputHField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        fineTunePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Fine-tunning parameters"));

        zTuneLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        zTuneLabel.setText("z [um]");
        zTuneLabel.setMaximumSize(new java.awt.Dimension(100, 14));
        zTuneLabel.setMinimumSize(new java.awt.Dimension(100, 14));
        zTuneLabel.setPreferredSize(new java.awt.Dimension(100, 14));

        fxTuneField.setText("0.0");
        fxTuneField.setToolTipText("fx-directioned linear phase compensation");
        fxTuneField.setEnabled(!difLimitEnabled);
        fxTuneField.setInputVerifier(verifier);
        fxTuneField.setMaximumSize(new java.awt.Dimension(83, 20));
        fxTuneField.setMinimumSize(new java.awt.Dimension(83, 20));
        fxTuneField.setPreferredSize(new java.awt.Dimension(83, 20));
        fxTuneField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fxTuneFieldtextFieldFocusGained(evt);
            }
        });

        fyTuneField.setText("0.0");
        fyTuneField.setToolTipText("fy-directioned linear phase compensation");
        fyTuneField.setEnabled(!difLimitEnabled);
        fyTuneField.setInputVerifier(verifier);
        fyTuneField.setMaximumSize(new java.awt.Dimension(83, 20));
        fyTuneField.setMinimumSize(new java.awt.Dimension(83, 20));
        fyTuneField.setPreferredSize(new java.awt.Dimension(83, 20));
        fyTuneField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fyTuneFieldtextFieldFocusGained(evt);
            }
        });

        fxTuneLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        fxTuneLabel.setText("fx-phase [px]");
        fxTuneLabel.setMaximumSize(new java.awt.Dimension(100, 14));
        fxTuneLabel.setMinimumSize(new java.awt.Dimension(100, 14));
        fxTuneLabel.setPreferredSize(new java.awt.Dimension(100, 14));

        fyTuneLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        fyTuneLabel.setText("fy-phase [px]");
        fyTuneLabel.setMaximumSize(new java.awt.Dimension(100, 14));
        fyTuneLabel.setMinimumSize(new java.awt.Dimension(100, 14));
        fyTuneLabel.setPreferredSize(new java.awt.Dimension(100, 14));

        zTuneField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        zTuneField.setText("0.0");
        zTuneField.setToolTipText("The z value must be a float-point number in micrometers");
        zTuneField.setEnabled(!difLimitEnabled);
        zTuneField.setInputVerifier(verifier);
        zTuneField.setMaximumSize(new java.awt.Dimension(83, 20));
        zTuneField.setMinimumSize(new java.awt.Dimension(83, 20));
        zTuneField.setPreferredSize(new java.awt.Dimension(83, 20));
        zTuneField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                zTuneFieldtextFieldFocusGained(evt);
            }
        });

        reduceZBtn.setText("-");
        reduceZBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reduceZBtnActionPerformed(evt);
            }
        });

        increaseZBtn.setText("+");
        increaseZBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                increaseZBtnActionPerformed(evt);
            }
        });

        titPhaseCompLabel.setText("Linear phase compensations:");

        titPhaseCompLabel1.setText("Focus position compensation:");

        linearTuneField.setText("0.0");
        linearTuneField.setToolTipText("Linear phase compensation");
        linearTuneField.setEnabled(!difLimitEnabled);
        linearTuneField.setInputVerifier(verifier);
        linearTuneField.setMaximumSize(new java.awt.Dimension(83, 20));
        linearTuneField.setMinimumSize(new java.awt.Dimension(83, 20));
        linearTuneField.setPreferredSize(new java.awt.Dimension(83, 20));
        linearTuneField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                linearTuneFieldtextFieldFocusGained(evt);
            }
        });

        linearTuneLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        linearTuneLabel.setText("Linear phase [rad]");
        linearTuneLabel.setToolTipText("");
        linearTuneLabel.setMaximumSize(new java.awt.Dimension(100, 14));
        linearTuneLabel.setMinimumSize(new java.awt.Dimension(100, 14));
        linearTuneLabel.setPreferredSize(new java.awt.Dimension(100, 14));

        javax.swing.GroupLayout fineTunePanelLayout = new javax.swing.GroupLayout(fineTunePanel);
        fineTunePanel.setLayout(fineTunePanelLayout);
        fineTunePanelLayout.setHorizontalGroup(
            fineTunePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fineTunePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(fineTunePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(fineTunePanelLayout.createSequentialGroup()
                        .addGroup(fineTunePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(fineTunePanelLayout.createSequentialGroup()
                                .addGroup(fineTunePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(fyTuneLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(fxTuneLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(fineTunePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(fxTuneField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(fyTuneField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(fineTunePanelLayout.createSequentialGroup()
                                .addComponent(zTuneLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(reduceZBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(zTuneField, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(increaseZBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(fineTunePanelLayout.createSequentialGroup()
                                .addComponent(titPhaseCompLabel)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(fineTunePanelLayout.createSequentialGroup()
                        .addGroup(fineTunePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(fineTunePanelLayout.createSequentialGroup()
                                .addComponent(titPhaseCompLabel1)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(fineTunePanelLayout.createSequentialGroup()
                                .addComponent(linearTuneLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(linearTuneField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        fineTunePanelLayout.setVerticalGroup(
            fineTunePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, fineTunePanelLayout.createSequentialGroup()
                .addComponent(titPhaseCompLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(fineTunePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(zTuneField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(reduceZBtn)
                    .addComponent(increaseZBtn)
                    .addComponent(zTuneLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(titPhaseCompLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(fineTunePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fxTuneLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fxTuneField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(fineTunePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fyTuneLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fyTuneField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(fineTunePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(linearTuneField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(linearTuneLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        applyBtn.setText("Apply");
        applyBtn.setToolTipText("");
        applyBtn.setMaximumSize(new java.awt.Dimension(132, 23));
        applyBtn.setMinimumSize(new java.awt.Dimension(132, 23));
        applyBtn.setPreferredSize(new java.awt.Dimension(132, 23));
        applyBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(applyBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(applyBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(parametersImagingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(fineTunePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(fineTunePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(parametersImagingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        savePrefs();
        setVisible(false);
        dispose();
    }//GEN-LAST:event_formWindowClosing

    private void textFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textFieldFocusGained
        JTextField field = (JTextField) evt.getComponent();
        field.selectAll();
    }//GEN-LAST:event_textFieldFocusGained

    private void applyBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyBtnActionPerformed
        boolean statusOk = parseValues();
        if (!statusOk) {
            JOptionPane.showMessageDialog(this, "The parameters are invalid");
        }
    }//GEN-LAST:event_applyBtnActionPerformed

    private void fxTuneFieldtextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_fxTuneFieldtextFieldFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_fxTuneFieldtextFieldFocusGained

    private void fyTuneFieldtextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_fyTuneFieldtextFieldFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_fyTuneFieldtextFieldFocusGained

    private void zTuneFieldtextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_zTuneFieldtextFieldFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_zTuneFieldtextFieldFocusGained

    private void reduceZBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reduceZBtnActionPerformed
        float newZTune = zTuneUm - 10.0f;
        String newZTuneString = Float.toString(newZTune);
        zTuneField.setText(newZTuneString);
        verifier.verify(zTuneField);
    }//GEN-LAST:event_reduceZBtnActionPerformed

    private void increaseZBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_increaseZBtnActionPerformed
        float newZTune = zTuneUm + 10.0f;
        String newZTuneString = Float.toString(newZTune);
        zTuneField.setText(newZTuneString);
        verifier.verify(zTuneField);
    }//GEN-LAST:event_increaseZBtnActionPerformed

    private void linearTuneFieldtextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_linearTuneFieldtextFieldFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_linearTuneFieldtextFieldFocusGained


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton applyBtn;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JPanel fineTunePanel;
    private javax.swing.JTextField fxTuneField;
    private javax.swing.JLabel fxTuneLabel;
    private javax.swing.JTextField fyTuneField;
    private javax.swing.JLabel fyTuneLabel;
    private javax.swing.JButton increaseZBtn;
    private javax.swing.JTextField inputHField;
    private javax.swing.JLabel inputHLabel;
    private javax.swing.JTextField inputWField;
    private javax.swing.JLabel inputWLabel;
    private javax.swing.JTextField lambdaField;
    private javax.swing.JLabel lambdaLabel;
    private javax.swing.JTextField linearTuneField;
    private javax.swing.JLabel linearTuneLabel;
    private javax.swing.JTextField moMagField;
    private javax.swing.JLabel moMagLabel;
    private javax.swing.JTextField moNAField;
    private javax.swing.JLabel moNALabel;
    private javax.swing.ButtonGroup outputGroup;
    private javax.swing.JPanel parametersImagingPanel;
    private javax.swing.JButton reduceZBtn;
    private javax.swing.JLabel titPhaseCompLabel;
    private javax.swing.JLabel titPhaseCompLabel1;
    private javax.swing.JTextField tlFocalField;
    private javax.swing.JLabel tlFocalLabel;
    private javax.swing.JTextField zTuneField;
    private javax.swing.JLabel zTuneLabel;
    // End of variables declaration//GEN-END:variables
}
