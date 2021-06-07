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

import ij.ImageListener;
import ij.ImagePlus;
import ij.WindowManager;
import ij.measure.Calibration;
import ij.process.ImageProcessor;
import java.awt.Frame;
import java.awt.Toolkit;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.prefs.Preferences;
import javax.swing.DefaultComboBoxModel;
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
public class ImagingFrame extends javax.swing.JFrame implements ImageListener, PreferencesKeys {

    // Auxiliary objects
    private static Imager imager;
    private final ParametersVerifier verifier;
    
    // Adimensional user inputs
    private float roughnessUser;
    private float moMagUser;
    private float moNAUser;
    private float azimuthUser;
    private float polarUser;
    private float ampMinUser;
    private float ampMaxUser;
    private float phaseMinUser;
    private float phaseMaxUser;
    
    // User inputs in user units
    private float lambdaUser;   
    private float tlFocalUser;
    private float inputWUser;
    private float inputHUser;

    // User inputs converted to um
    private float lambdaUm; 
    private float tlFocalUm;
    private float inputWUm;
    private float inputHUm;
    
    // Boolean for the user parameters
    private boolean lambdaSet;
    private boolean roughnessSet;
    private boolean moMagSet;
    private boolean moNASet;
    private boolean tlFocalSet;
    private boolean inputWSet;
    private boolean inputHSet;
    private boolean azimuthSet;
    private boolean polarSet;
    private boolean ampMinSet;
    private boolean ampMaxSet;
    private boolean phaseMinSet;
    private boolean phaseMaxSet;

    // Input field dimensions, useful for output calibration
    private int M, N;
    private int newIDinput1;
    private int newMinput1;
    private int newNinput1;
    private int newIDinput2;
    private int newMinput2;
    private int newNinput2;

    // Arrays with the current opened images information
    private int[] windowsId;
    private String[] titles, titles2;

    // Calibration object for the output images
    private Calibration cal;

    // Formatter
    private final DecimalFormat df;

    // Preferences
    private final Preferences pref;

    // Auxiliar frames
    private ImagingSettingsFrame settingsFrame = null;

    // Frame location
    private int locX;
    private int locY;

    // Last parameters used
    private String lambdaString;
    private String roughnessString;
    private String moMagString;
    private String moNAString;
    private String tlFocalString;
    private String inputWString;
    private String inputHString;
    private String ampMinString;
    private String ampMaxString;
    private String phaseMinString;
    private String phaseMaxString;
    private String azimuthString;
    private String polarString;
    private boolean difLimitEnabled;

    // Parameters units
    private String lambdaUnits;
    private String tlFocalUnits;
    private String inputSizeUnits;

    // Last outputs used
    private boolean phaseEnabled;
    private boolean amplitudeEnabled;
    private boolean intensityEnabled;
    private boolean realEnabled;
    private boolean imaginaryEnabled;
    private boolean hologramEnabled;

    /**
     * Creates the main frame
     */
    public ImagingFrame() {
        // Initialize auxiliar objects
        df = new DecimalFormat("#.####", new DecimalFormatSymbols(Locale.US));
        pref = Preferences.userNodeForPackage(getClass());        
        imager = new Imager(this);
        verifier = new ParametersVerifier();

        // Gets the current open images and load the last preferences
        getOpenedImages();
        loadPrefs();

        // Initialize the GUI
        initComponents();

        // Adds this class as ImageListener
        ImagePlus.addImageListener(this);
    }

    /**
     * Fills the arrays with the information of the open images.
     */
    private void getOpenedImages() {
        // Gets the IDs of the opened images in ImageJ
        windowsId = WindowManager.getIDList();

        if (windowsId == null) {
            titles = new String[]{"<none>"};
            titles2 = new String[]{"<none>"};
        } else {
            // Titles for input 1
            titles = new String[windowsId.length + 1];
            titles[0] = "<none>";
            for (int i = 0; i < windowsId.length; i++) {
                ImagePlus imp = WindowManager.getImage(windowsId[i]);
                if (imp != null) {
                    titles[i + 1] = imp.getTitle();
                } else {
                    titles[i + 1] = "";
                }
            }
        }
    }

    /**
     * Saves the preferences when the frame is closed.
     */
    private void savePrefs() {
        // Frame location
        pref.putInt(FRAME_LOC_X, getLocation().x);
        pref.putInt(FRAME_LOC_Y, getLocation().y);

        // Imaging Parameters fields
        pref.putFloat(IMG_LAMBDA, lambdaUser);
        pref.putFloat(IMG_ROUGHNESS, roughnessUser);
        pref.putFloat(IMG_MO_MAGNIFICATION, moMagUser);
        pref.putFloat(IMG_MO_NA, moNAUser);
        pref.putFloat(IMG_TL_FOCAL, tlFocalUser);
        pref.putFloat(IMG_INPUT_WIDTH, inputWUser);
        pref.putFloat(IMG_INPUT_HEIGHT, inputHUser);
        
        // Interference Parameters fields
        pref.putBoolean(INTF_DIFFLIMITED, diffLimitChk.isSelected());
        pref.putFloat(INTF_AZIMUTH, azimuthUser);
        pref.putFloat(INTF_POLAR, polarUser);
        
        // Input Parameters fields
        pref.putFloat(IN_AMPLI_MIN, ampMinUser);
        pref.putFloat(IN_AMPLI_MAX, ampMaxUser);
        pref.putFloat(IN_PHASE_MIN, phaseMinUser);
        pref.putFloat(IN_PHASE_MAX, phaseMaxUser);

        // Selected outputs
        pref.putBoolean(OUT_PHASE_CHECKED, phaseChk.isSelected());
        pref.putBoolean(OUT_AMPLITUDE_CHECKED, amplitudeChk.isSelected());
        pref.putBoolean(OUT_INTENSITY_CHECKED, intensityChk.isSelected());
        pref.putBoolean(OUT_REAL_CHECKED, realChk.isSelected());
        pref.putBoolean(OUT_IMAGINARY_CHECKED, imaginaryChk.isSelected());
        pref.putBoolean(OUT_HOLOGRAM_CHECKED, hologramChk.isSelected());
    }

    /**
     * Loads the preferences when the plugin starts.
     */
    private void loadPrefs() {
        // Frame location
        locX = pref.getInt(FRAME_LOC_X, 300);
        locY = pref.getInt(FRAME_LOC_Y, 300);

        // Outputs
        phaseEnabled = pref.getBoolean(OUT_PHASE_CHECKED, false);
        amplitudeEnabled = pref.getBoolean(OUT_AMPLITUDE_CHECKED, false);
        intensityEnabled = pref.getBoolean(OUT_INTENSITY_CHECKED, false);
        realEnabled = pref.getBoolean(OUT_REAL_CHECKED, false);
        imaginaryEnabled = pref.getBoolean(OUT_IMAGINARY_CHECKED, false);
        hologramEnabled = pref.getBoolean(OUT_HOLOGRAM_CHECKED, false);
        
        // Diffraction limit
        difLimitEnabled = pref.getBoolean(INTF_DIFFLIMITED, false);

        // Parameters units
        loadUnitsPrefs();

        // Parameters strings for text input fields
        loadParameters();
    }
    
    /**
     * Loads the last used parameters.
     */
    private void loadParameters() {
        // Gets the saved floats and checks for NaN values
        lambdaUser = pref.getFloat(IMG_LAMBDA, Float.NaN);
        if (Float.isNaN(lambdaUser)) {
            lambdaSet = false;
            lambdaString = "";
        } else {
            lambdaSet = true;
            lambdaString = df.format(lambdaUser);
            lambdaUm = unitsToUm(lambdaUser, lambdaUnits);
        }

        roughnessUser = pref.getFloat(IMG_ROUGHNESS, Float.NaN);
        if (Float.isNaN(roughnessUser)) {
            roughnessSet = false;
            roughnessString = "";
        } else {
            roughnessSet = true;
            roughnessString = df.format(roughnessUser);
        }

        moMagUser = pref.getFloat(IMG_MO_MAGNIFICATION, Float.NaN);
        if (Float.isNaN(moMagUser)) {
            moMagSet = false;
            moMagString = "";
        } else {
            moMagSet = true;
            moMagString = df.format(moMagUser);
        }
        
        moNAUser = pref.getFloat(IMG_MO_NA, Float.NaN);
        if (Float.isNaN(moNAUser)) {
            moNASet = false;
            moNAString = "";
        } else {
            moNASet = true;
            moNAString = df.format(moNAUser);
        }
        
        tlFocalUser = pref.getFloat(IMG_TL_FOCAL, Float.NaN);
        if (Float.isNaN(tlFocalUser)) {
            tlFocalSet = false;
            tlFocalString = "";
        } else {
            tlFocalSet = true;
            tlFocalString = df.format(tlFocalUser);
            tlFocalUm = unitsToUm(tlFocalUser, tlFocalUnits);
        }

        inputWUser = pref.getFloat(IMG_INPUT_WIDTH, Float.NaN);
        if (Float.isNaN(inputWUser)) {
            inputWSet = false;
            inputWString = "";
        } else {
            inputWSet = true;
            inputWString = df.format(inputWUser);
            inputWUm = unitsToUm(inputWUser, inputSizeUnits);
        }

        inputHUser = pref.getFloat(IMG_INPUT_HEIGHT, Float.NaN);
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
     * Loads the units of the parameters.
     */
    private void loadUnitsPrefs() {
        lambdaUnits = pref.get(IMG_LAMBDA_UNITS, "nm");
        tlFocalUnits = pref.get(IMG_TL_FOCAL_UNITS, "mm");
        inputSizeUnits = pref.get(IMG_INPUT_UNITS, "mm");
    }

    /**
     * Updates units labels.
     */
    public void updateUnitsPrefs() {
        loadUnitsPrefs();

        lambdaLabel.setText("Wavelength [" + lambdaUnits + "]:");
        roughnessLabel.setText("Roughness [%λ]:");
        moMagLabel.setText("MO Mag. [X]:");
        moNALabel.setText("MO NA [adim]:");
        tlFocalLabel.setText("TL focal [" + tlFocalUnits + "]:");
        inputWLabel.setText("Input width [" + inputSizeUnits + "]:");
        inputHLabel.setText("Input height [" + inputSizeUnits + "]:");
    }
    
    /**
     * Updates the displayed interference angle values after a diffraction-
     * limited run is completed
     * 
     * @param newPolar Value to display on Polar Angle field
     * @param newAzimuth Value to display on Azimuthal Angle field
     */
    protected void updateAngleValues(String newPolar, String newAzimuth) {
        polarField.setText(newPolar);
        azimuthField.setText(newAzimuth);
    }

    /**
     * Creates and array with the user-defined outputs for a given run as given
     * by the selected check-boxes in the GUI.
     * 
     * @return 
     */
    public char[] getSelectedOutputs(){
        char[] selOut = new char[6];
        int i = 0;
        if (amplitudeChk.isSelected()) {
            selOut[i] = 'A';
            i++;
        }
        if (hologramChk.isSelected()) {
            selOut[i] = 'H';
            i++;
        }
        if (imaginaryChk.isSelected()) {
            selOut[i] = 'J';
            i++;
        }
        if (intensityChk.isSelected()) {
            selOut[i] = 'I';
            i++;
        }
        if (phaseChk.isSelected()) {
            selOut[i] = 'P';
            i++;
        }
        if (realChk.isSelected()) {
            selOut[i] = 'R';
            i++;
        }
        if (i < 6) {
            selOut[i] = '\0';
        }
        return selOut;
    }

    /**
     * Helper method to convert from {units} to um.
     *
     * @param val
     * @param units
     * @return
     */
    private float unitsToUm(float val, String units) {
        if (units.equals("nm")) {
            return val * 1E-3f;
        } else if (units.equals("mm")) {
            return val * 1E3f;
        } else if (units.equals("cm")) {
            return val * 1E4f;
        } else if (units.equals("m")) {
            return val * 1E6f;
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

    private boolean setInputImages(int realIdx, int imaginaryIdx) {
        String in1Title = titles[realIdx];
        String in2Title = titles[imaginaryIdx];

        boolean hasInput1 = !in1Title.equalsIgnoreCase("<none>");
        boolean hasInput2 = !in2Title.equalsIgnoreCase("<none>");
        
        int inputType = inputTypeCombo.getSelectedIndex() + 1;

        if (!hasInput1 && !hasInput2) {
            // If no inputs are chosen, show an error message
            JOptionPane.showMessageDialog(this, "Please select at least one input image.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (hasInput1 && hasInput2) {
            ImagePlus input1Imp = WindowManager.getImage(windowsId[realIdx - 1]);
            ImageProcessor input1Ip = input1Imp.getProcessor();

            newIDinput1 = input1Imp.getID();
            newMinput1 = input1Ip.getWidth();
            newNinput1 = input1Ip.getHeight();

            ImagePlus input2Imp = WindowManager.getImage(windowsId[imaginaryIdx - 1]);
            ImageProcessor input2Ip = input2Imp.getProcessor();

            newIDinput2 = input2Imp.getID();
            newMinput2 = input2Ip.getWidth();
            newNinput2 = input2Ip.getHeight();

            // Checks dimensions
            if (newMinput1 != newMinput2 || newNinput1 != newNinput2) {
                JOptionPane.showMessageDialog(this, "Input images must have the same dimensions.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            M = newMinput1;
            N = newNinput1;
            imager.setInputImages(inputType,M, N, input1Ip.getFloatArray(), input2Ip.getFloatArray());

        } else if (hasInput1 && !hasInput2) {
            ImagePlus inut1Imp = WindowManager.getImage(windowsId[realIdx - 1]);
            ImageProcessor input1Ip = inut1Imp.getProcessor();

            newIDinput1 = inut1Imp.getID();
            newMinput1 = input1Ip.getWidth();
            newNinput1 = input1Ip.getHeight();

            newIDinput2 = Integer.MAX_VALUE;
            newMinput2 = -1;
            newNinput2 = -1;

            M = newMinput1;
            N = newNinput1;
            imager.setInputImages(inputType,M, N, input1Ip.getFloatArray(), null);

        } else if (!hasInput1 && hasInput2) {
            ImagePlus input2Imp = WindowManager.getImage(windowsId[imaginaryIdx - 1]);
            ImageProcessor input2Ip = input2Imp.getProcessor();

            newIDinput1 = Integer.MAX_VALUE;
            newMinput1 = -1;
            newNinput1 = -1;

            newIDinput2 = input2Imp.getID();
            newMinput2 = input2Ip.getWidth();
            newNinput2 = input2Ip.getHeight();

            M = newMinput2;
            N = newNinput2;
            imager.setInputImages(inputType,M, N, null, input2Ip.getFloatArray());
        }

        return true;
    }
    
    private boolean parseValues() {
        // Input parameters
        boolean inputParSet = inputHSet && inputWSet;
        if (inputTypeCombo.getSelectedIndex() == 0) {
            if(inputParSet) {
            imager.setInputParameters(inputWUm, inputHUm);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid input values");
                return false;
            }
        } else {
           inputParSet = inputParSet && ampMinSet && ampMaxSet
                    && phaseMinSet && phaseMaxSet;
            if(inputParSet) {
                imager.setInputParameters(inputWUm, inputHUm, ampMinUser, ampMaxUser,
                        phaseMinUser, phaseMaxUser);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid input values");
                return false;
            }
        }
        
        // Illumination parameters
        boolean illumParSet = lambdaSet && roughnessSet;
        if(illumParSet) {
            imager.setIllumination(lambdaUm, roughnessUser);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid illumination values");
            return false;
        }

        // Imaging parameters
        boolean imagParSet = moMagSet && moNASet && tlFocalSet;
        if(imagParSet) {
            imager.setImaging(moMagUser, moNAUser, tlFocalUm);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid imaging conditions");
            return false;
        }
        
        // Reference angle parameters
        boolean isDifLimited = diffLimitChk.isSelected();
        boolean intfParSet = polarSet && azimuthSet;
        
        if(isDifLimited) {
            imager.setAngles(isDifLimited, 0.0, 0.0);
        } else {
            if(intfParSet) {
                imager.setAngles(isDifLimited, azimuthUser, polarUser);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid angle values");
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the calibration object.
     *
     * @return
     */
    public Calibration getCalibration() {
        return cal;
    }

    /**
     * Listener method, updates the input combos.
     *
     * @param imp
     */
    @Override
    public void imageClosed(ImagePlus imp) {
        updateCombos();
    }

    /**
     * Listener method, updates the input combos.
     *
     * @param imp
     */
    @Override
    public void imageOpened(ImagePlus imp) {
        updateCombos();
    }

    /**
     * Listener method, updates the input combos.
     *
     * @param imp
     */
    @Override
    public void imageUpdated(ImagePlus imp) {
        updateCombos();
    }
    
    /**
     * Enables or disables the scaling range input fields
     * 
     * @param state 
     */
    private void setLimitsEnable(boolean state) {
        ampMinField.setEnabled(state);
        ampMaxField.setEnabled(state);
        phaseMinField.setEnabled(state);
        phaseMaxField.setEnabled(state);
    }
    
    /**
     * Updates the information on the combos.
     */
    private void updateCombos() {
        int input1Idx = input1Combo.getSelectedIndex();
        int input2Idx = input2Combo.getSelectedIndex();

        getOpenedImages();
        input1Combo.setModel(new DefaultComboBoxModel<>(titles));
        input1Combo.setSelectedIndex((input1Idx >= titles.length)
                ? titles.length - 1 : input1Idx);

        input2Combo.setModel(new DefaultComboBoxModel<>(titles));
        input2Combo.setSelectedIndex((input2Idx >= titles.length)
                ? titles2.length - 1 : input2Idx);
    }
    
    private class ParametersVerifier extends InputVerifier {

        @Override
        public boolean verify(JComponent input) {
//            Component parent = SwingUtilities.getRoot(input);

            boolean valid;

            if (input == lambdaField) {
                valid = checkLambdaField();
            } else if (input == roughnessField) {
                valid = checkRoughnessField();
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
            } else if (input == azimuthField) {
                valid = checkAzimuthField();
            } else if (input == polarField) {
                valid = checkPolarField();
            } else if (input == ampMinField) {
                valid = checkAmpMinField();
            } else if (input == ampMaxField) {
                valid = checkAmpMaxField();
            } else if (input == phaseMinField) {
                valid = checkPhaseMinField();
            } else if (input == phaseMaxField) {
                valid = checkPhaseMaxField();
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

        private boolean checkRoughnessField() {
            try {
                String txt = roughnessField.getText();

                if (txt.isEmpty()) {
                    roughnessSet = false;
                    roughnessUser = Float.NaN;
                    return true;
                }

                roughnessUser = Float.parseFloat(txt);

                roughnessSet = true;
                return true;

            } catch (NumberFormatException e) {
                Toolkit.getDefaultToolkit().beep();
                roughnessField.selectAll();

                roughnessSet = false;
                roughnessUser = Float.NaN;
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
        
        private boolean checkAzimuthField() {
            try {
                String txt = azimuthField.getText();

                if (txt.isEmpty()) {
                    azimuthSet = false;
                    azimuthUser = Float.NaN;
                    return true;
                }

                azimuthUser = Float.parseFloat(txt);

                if (azimuthUser < -2*Math.PI || azimuthUser > 2*Math.PI) {
                    Toolkit.getDefaultToolkit().beep();
                    azimuthField.selectAll();

                    azimuthSet = false;
                    azimuthUser = Float.NaN;
                    return false;
                }

                azimuthSet = true;
                return true;

            } catch (NumberFormatException e) {
                Toolkit.getDefaultToolkit().beep();
                azimuthField.selectAll();

                azimuthSet = false;
                azimuthUser = Float.NaN;
                return false;
            }
        }
        
        private boolean checkPolarField() {
            try {
                String txt = polarField.getText();

                if (txt.isEmpty()) {
                    polarSet = false;
                    polarUser = Float.NaN;
                    return true;
                }

                polarUser = Float.parseFloat(txt);

                if (polarUser < -2*Math.PI || polarUser > 2*Math.PI) {
                    Toolkit.getDefaultToolkit().beep();
                    polarField.selectAll();

                    polarSet = false;
                    polarUser = Float.NaN;
                    return false;
                }

                polarSet = true;
                return true;

            } catch (NumberFormatException e) {
                Toolkit.getDefaultToolkit().beep();
                polarField.selectAll();

                polarSet = false;
                polarUser = Float.NaN;
                return false;
            }
        }
        
        private boolean checkAmpMinField() {
            try {
                String txt = ampMinField.getText();

                if (txt.isEmpty()) {
                    ampMinSet = false;
                    ampMinUser = Float.NaN;
                    return true;
                }

                ampMinUser = Float.parseFloat(txt);

                ampMinSet = true;
                return true;

            } catch (NumberFormatException e) {
                Toolkit.getDefaultToolkit().beep();
                ampMinField.selectAll();

                ampMinSet = false;
                ampMinUser = Float.NaN;
                return false;
            }
        }
        
        private boolean checkAmpMaxField() {
            try {
                String txt = ampMaxField.getText();

                if (txt.isEmpty()) {
                    ampMaxSet = false;
                    ampMaxUser = Float.NaN;
                    return true;
                }

                ampMaxUser = Float.parseFloat(txt);

                ampMaxSet = true;
                return true;

            } catch (NumberFormatException e) {
                Toolkit.getDefaultToolkit().beep();
                ampMaxField.selectAll();

                ampMaxSet = false;
                ampMaxUser = Float.NaN;
                return false;
            }
        }
        
        private boolean checkPhaseMinField() {
            try {
                String txt = phaseMinField.getText();

                if (txt.isEmpty()) {
                    phaseMinSet = false;
                    phaseMinUser = Float.NaN;
                    return true;
                }

                phaseMinUser = Float.parseFloat(txt);

                phaseMinSet = true;
                return true;

            } catch (NumberFormatException e) {
                Toolkit.getDefaultToolkit().beep();
                phaseMinField.selectAll();

                phaseMinSet = false;
                phaseMinUser = Float.NaN;
                return false;
            }
        }
        
        private boolean checkPhaseMaxField() {
            try {
                String txt = phaseMaxField.getText();

                if (txt.isEmpty()) {
                    phaseMaxSet = false;
                    phaseMaxUser = Float.NaN;
                    return true;
                }

                phaseMaxUser = Float.parseFloat(txt);

                phaseMaxSet = true;
                return true;

            } catch (NumberFormatException e) {
                Toolkit.getDefaultToolkit().beep();
                phaseMaxField.selectAll();

                phaseMaxSet = false;
                phaseMaxUser = Float.NaN;
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
        roughnessField = new javax.swing.JTextField();
        moNAField = new javax.swing.JTextField();
        tlFocalField = new javax.swing.JTextField();
        inputWField = new javax.swing.JTextField();
        inputHField = new javax.swing.JTextField();
        inputHLabel = new javax.swing.JLabel();
        inputWLabel = new javax.swing.JLabel();
        tlFocalLabel = new javax.swing.JLabel();
        moNALabel = new javax.swing.JLabel();
        roughnessLabel = new javax.swing.JLabel();
        lambdaLabel = new javax.swing.JLabel();
        moMagLabel = new javax.swing.JLabel();
        moMagField = new javax.swing.JTextField();
        parametersInputPanel = new javax.swing.JPanel();
        inputTypeCombo = new javax.swing.JComboBox();
        input1Combo = new javax.swing.JComboBox();
        input2Combo = new javax.swing.JComboBox();
        inputTypeLabel = new javax.swing.JLabel();
        input1Label = new javax.swing.JLabel();
        input2Label = new javax.swing.JLabel();
        ampLimitsLabel = new javax.swing.JLabel();
        phaseLimitsLabel = new javax.swing.JLabel();
        ampMinField = new javax.swing.JTextField();
        phaseMinField = new javax.swing.JTextField();
        ampMaxField = new javax.swing.JTextField();
        phaseMaxField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        parametersInterfPanel = new javax.swing.JPanel();
        azimuthField = new javax.swing.JTextField();
        polarField = new javax.swing.JTextField();
        azimuthLabel = new javax.swing.JLabel();
        polarLabel = new javax.swing.JLabel();
        diffLimitChk = new javax.swing.JCheckBox();
        chkPanel = new javax.swing.JPanel();
        phaseChk = new javax.swing.JCheckBox();
        amplitudeChk = new javax.swing.JCheckBox();
        intensityChk = new javax.swing.JCheckBox();
        realChk = new javax.swing.JCheckBox();
        imaginaryChk = new javax.swing.JCheckBox();
        hologramChk = new javax.swing.JCheckBox();
        btnsPanel = new javax.swing.JPanel();
        settingsBtn = new javax.swing.JButton();
        runBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Realistic DHM Imaging");
        setBounds(new java.awt.Rectangle(locX, locY, 0, 0)
        );
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));
        setMinimumSize(new java.awt.Dimension(545, 311));
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

        roughnessField.setText(roughnessString);
        roughnessField.setToolTipText("Roughness percentage must be a floating point number.");
        roughnessField.setInputVerifier(verifier);
        roughnessField.setMaximumSize(new java.awt.Dimension(115, 20));
        roughnessField.setMinimumSize(new java.awt.Dimension(115, 20));
        roughnessField.setPreferredSize(new java.awt.Dimension(115, 20));
        roughnessField.addFocusListener(new java.awt.event.FocusAdapter() {
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
        inputHLabel.setText("Sample height [" + inputSizeUnits + "]:");
        inputHLabel.setEnabled(true);
        inputHLabel.setMaximumSize(new java.awt.Dimension(100, 14));
        inputHLabel.setMinimumSize(new java.awt.Dimension(100, 14));
        inputHLabel.setPreferredSize(new java.awt.Dimension(100, 14));

        inputWLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        inputWLabel.setText("Sample width [" + inputSizeUnits + "]:");
        inputWLabel.setEnabled(true);
        inputWLabel.setMaximumSize(new java.awt.Dimension(100, 14));
        inputWLabel.setMinimumSize(new java.awt.Dimension(100, 14));
        inputWLabel.setPreferredSize(new java.awt.Dimension(100, 14));

        tlFocalLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        tlFocalLabel.setText("TL focal [" + tlFocalUnits + "]:");
        tlFocalLabel.setMaximumSize(new java.awt.Dimension(100, 14));
        tlFocalLabel.setMinimumSize(new java.awt.Dimension(100, 14));
        tlFocalLabel.setPreferredSize(new java.awt.Dimension(100, 14));

        moNALabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        moNALabel.setText("MO NA [adim]:");
        moNALabel.setMaximumSize(new java.awt.Dimension(100, 14));
        moNALabel.setMinimumSize(new java.awt.Dimension(100, 14));
        moNALabel.setPreferredSize(new java.awt.Dimension(100, 14));

        roughnessLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        roughnessLabel.setText("Roughness [%λ]:");
        roughnessLabel.setToolTipText("Optical roughness");
        roughnessLabel.setMaximumSize(new java.awt.Dimension(100, 14));
        roughnessLabel.setMinimumSize(new java.awt.Dimension(100, 14));
        roughnessLabel.setPreferredSize(new java.awt.Dimension(100, 14));

        lambdaLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lambdaLabel.setText("Wavelength [" + lambdaUnits + "]:");
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
                            .addComponent(roughnessLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lambdaLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(parametersImagingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(moNAField, javax.swing.GroupLayout.PREFERRED_SIZE, 83, Short.MAX_VALUE)
                            .addComponent(tlFocalField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                            .addComponent(roughnessField, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                            .addComponent(lambdaField, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)))
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
                    .addComponent(roughnessField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(roughnessLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        parametersInputPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Input Parameters"));

        inputTypeCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Real & Imaginary", "Amplitude & Phase" }));
        inputTypeCombo.setMaximumSize(new java.awt.Dimension(115, 20));
        inputTypeCombo.setMinimumSize(new java.awt.Dimension(115, 20));
        inputTypeCombo.setPreferredSize(new java.awt.Dimension(115, 20));
        inputTypeCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputTypeComboActionPerformed(evt);
            }
        });

        input1Combo.setModel(new DefaultComboBoxModel<String>(titles));
        input1Combo.setSelectedIndex(titles.length > 1 ? 1 : 0);
        input1Combo.setMaximumSize(new java.awt.Dimension(115, 20));
        input1Combo.setMinimumSize(new java.awt.Dimension(115, 20));
        input1Combo.setPreferredSize(new java.awt.Dimension(115, 20));

        input2Combo.setModel(new DefaultComboBoxModel<String>(titles)
        );
        input2Combo.setSelectedIndex(titles.length > 1 ? 1 : 0);
        input2Combo.setMaximumSize(new java.awt.Dimension(115, 20));
        input2Combo.setMinimumSize(new java.awt.Dimension(115, 20));
        input2Combo.setPreferredSize(new java.awt.Dimension(115, 20));

        inputTypeLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        inputTypeLabel.setText("Input type:");
        inputTypeLabel.setMaximumSize(new java.awt.Dimension(100, 14));
        inputTypeLabel.setMinimumSize(new java.awt.Dimension(100, 14));
        inputTypeLabel.setPreferredSize(new java.awt.Dimension(100, 14));

        input1Label.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        input1Label.setText("Real:");
        input1Label.setMaximumSize(new java.awt.Dimension(100, 14));
        input1Label.setMinimumSize(new java.awt.Dimension(100, 14));
        input1Label.setPreferredSize(new java.awt.Dimension(100, 14));

        input2Label.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        input2Label.setText("Imaginary:");
        input2Label.setMaximumSize(new java.awt.Dimension(100, 14));
        input2Label.setMinimumSize(new java.awt.Dimension(100, 14));
        input2Label.setPreferredSize(new java.awt.Dimension(100, 14));

        ampLimitsLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        ampLimitsLabel.setText("Amplitude range:");
        ampLimitsLabel.setMaximumSize(new java.awt.Dimension(100, 14));
        ampLimitsLabel.setMinimumSize(new java.awt.Dimension(100, 14));
        ampLimitsLabel.setPreferredSize(new java.awt.Dimension(100, 14));

        phaseLimitsLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        phaseLimitsLabel.setText("Phase range [rad]:");
        phaseLimitsLabel.setMaximumSize(new java.awt.Dimension(100, 14));
        phaseLimitsLabel.setMinimumSize(new java.awt.Dimension(100, 14));
        phaseLimitsLabel.setPreferredSize(new java.awt.Dimension(100, 14));

        ampMinField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        ampMinField.setText(ampMinString);
        ampMinField.setToolTipText("Output width must be a positive number and different from 0.");
        ampMinField.setEnabled(false);
        ampMinField.setInputVerifier(verifier);
        ampMinField.setMaximumSize(new java.awt.Dimension(83, 20));
        ampMinField.setMinimumSize(new java.awt.Dimension(83, 20));
        ampMinField.setPreferredSize(new java.awt.Dimension(83, 20));
        ampMinField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ampMinFieldtextFieldFocusGained(evt);
            }
        });

        phaseMinField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        phaseMinField.setText(phaseMinString);
        phaseMinField.setToolTipText("Output height must be a positive number and different from 0.");
        phaseMinField.setEnabled(false);
        phaseMinField.setInputVerifier(verifier);
        phaseMinField.setMaximumSize(new java.awt.Dimension(83, 20));
        phaseMinField.setMinimumSize(new java.awt.Dimension(83, 20));
        phaseMinField.setPreferredSize(new java.awt.Dimension(83, 20));
        phaseMinField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                phaseMinFieldtextFieldFocusGained(evt);
            }
        });

        ampMaxField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        ampMaxField.setText(ampMaxString);
        ampMaxField.setToolTipText("Output width must be a positive number and different from 0.");
        ampMaxField.setEnabled(false);
        ampMaxField.setInputVerifier(verifier);
        ampMaxField.setMaximumSize(new java.awt.Dimension(83, 20));
        ampMaxField.setMinimumSize(new java.awt.Dimension(83, 20));
        ampMaxField.setPreferredSize(new java.awt.Dimension(83, 20));
        ampMaxField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ampMaxFieldtextFieldFocusGained(evt);
            }
        });

        phaseMaxField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        phaseMaxField.setText(phaseMaxString);
        phaseMaxField.setToolTipText("Output height must be a positive number and different from 0.");
        phaseMaxField.setEnabled(false);
        phaseMaxField.setInputVerifier(verifier);
        phaseMaxField.setMaximumSize(new java.awt.Dimension(83, 20));
        phaseMaxField.setMinimumSize(new java.awt.Dimension(83, 20));
        phaseMaxField.setPreferredSize(new java.awt.Dimension(83, 20));
        phaseMaxField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                phaseMaxFieldtextFieldFocusGained(evt);
            }
        });

        jLabel1.setText("to");

        jLabel2.setText("to");

        javax.swing.GroupLayout parametersInputPanelLayout = new javax.swing.GroupLayout(parametersInputPanel);
        parametersInputPanel.setLayout(parametersInputPanelLayout);
        parametersInputPanelLayout.setHorizontalGroup(
            parametersInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(parametersInputPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(parametersInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(parametersInputPanelLayout.createSequentialGroup()
                        .addGroup(parametersInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(phaseLimitsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ampLimitsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(parametersInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ampMinField, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(phaseMinField, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(parametersInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(parametersInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ampMaxField, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(phaseMaxField, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)))
                    .addGroup(parametersInputPanelLayout.createSequentialGroup()
                        .addComponent(input2Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(input2Combo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(parametersInputPanelLayout.createSequentialGroup()
                        .addGroup(parametersInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(input1Label, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(inputTypeLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(parametersInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(input1Combo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(inputTypeCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        parametersInputPanelLayout.setVerticalGroup(
            parametersInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(parametersInputPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(parametersInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inputTypeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inputTypeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(parametersInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(input1Combo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(input1Label, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(parametersInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(input2Combo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(input2Label, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(parametersInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ampLimitsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ampMinField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(ampMaxField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(parametersInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(phaseLimitsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(phaseMinField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(phaseMaxField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        parametersInterfPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Interference Angle Parameters"));

        azimuthField.setText(azimuthString);
        azimuthField.setToolTipText("The azimuthal angle must be a possitive number, in radians.");
        azimuthField.setEnabled(!difLimitEnabled);
        azimuthField.setInputVerifier(verifier);
        azimuthField.setMaximumSize(new java.awt.Dimension(83, 20));
        azimuthField.setMinimumSize(new java.awt.Dimension(83, 20));
        azimuthField.setPreferredSize(new java.awt.Dimension(83, 20));
        azimuthField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                azimuthFieldtextFieldFocusGained(evt);
            }
        });

        polarField.setText(polarString);
        polarField.setToolTipText("The polar angle must be a possitive number in radians.");
        polarField.setEnabled(!difLimitEnabled);
        polarField.setInputVerifier(verifier);
        polarField.setMaximumSize(new java.awt.Dimension(83, 20));
        polarField.setMinimumSize(new java.awt.Dimension(83, 20));
        polarField.setPreferredSize(new java.awt.Dimension(83, 20));
        polarField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                polarFieldtextFieldFocusGained(evt);
            }
        });

        azimuthLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        azimuthLabel.setText("Azimuth [rad]:");
        azimuthLabel.setMaximumSize(new java.awt.Dimension(100, 14));
        azimuthLabel.setMinimumSize(new java.awt.Dimension(100, 14));
        azimuthLabel.setPreferredSize(new java.awt.Dimension(100, 14));

        polarLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        polarLabel.setText("Polar [rad]:");
        polarLabel.setMaximumSize(new java.awt.Dimension(100, 14));
        polarLabel.setMinimumSize(new java.awt.Dimension(100, 14));
        polarLabel.setPreferredSize(new java.awt.Dimension(100, 14));

        diffLimitChk.setSelected(difLimitEnabled);
        diffLimitChk.setText("Diffraction Limited");
        diffLimitChk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                diffLimitChkActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout parametersInterfPanelLayout = new javax.swing.GroupLayout(parametersInterfPanel);
        parametersInterfPanel.setLayout(parametersInterfPanelLayout);
        parametersInterfPanelLayout.setHorizontalGroup(
            parametersInterfPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(parametersInterfPanelLayout.createSequentialGroup()
                .addGroup(parametersInterfPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(parametersInterfPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(parametersInterfPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(polarLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(azimuthLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(parametersInterfPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(azimuthField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(polarField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(parametersInterfPanelLayout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(diffLimitChk)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        parametersInterfPanelLayout.setVerticalGroup(
            parametersInterfPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, parametersInterfPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(diffLimitChk)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(parametersInterfPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(azimuthLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(azimuthField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(parametersInterfPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(polarLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(polarField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        chkPanel.setMaximumSize(new java.awt.Dimension(269, 23));
        chkPanel.setMinimumSize(new java.awt.Dimension(269, 23));

        phaseChk.setSelected(phaseEnabled);
        phaseChk.setText("Phase");
        phaseChk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                phaseChkActionPerformed(evt);
            }
        });

        amplitudeChk.setSelected(amplitudeEnabled);
        amplitudeChk.setText("Amp.");
        amplitudeChk.setToolTipText("Amplitude");
        amplitudeChk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                amplitudeChkActionPerformed(evt);
            }
        });

        intensityChk.setSelected(intensityEnabled);
        intensityChk.setText("Int.");
        intensityChk.setToolTipText("Intensity");
        intensityChk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                intensityChkActionPerformed(evt);
            }
        });

        realChk.setSelected(realEnabled);
        realChk.setText("Real");
        realChk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                realChkActionPerformed(evt);
            }
        });

        imaginaryChk.setSelected(imaginaryEnabled);
        imaginaryChk.setText("Imag.");
        imaginaryChk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imaginaryChkActionPerformed(evt);
            }
        });

        hologramChk.setSelected(hologramEnabled);
        hologramChk.setText("Holo.");
        hologramChk.setToolTipText("Hologram");
        hologramChk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hologramChkActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout chkPanelLayout = new javax.swing.GroupLayout(chkPanel);
        chkPanel.setLayout(chkPanelLayout);
        chkPanelLayout.setHorizontalGroup(
            chkPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chkPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(chkPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(phaseChk)
                    .addComponent(realChk))
                .addGap(18, 18, 18)
                .addGroup(chkPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(amplitudeChk)
                    .addComponent(imaginaryChk))
                .addGap(18, 18, 18)
                .addGroup(chkPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(hologramChk)
                    .addComponent(intensityChk))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        chkPanelLayout.setVerticalGroup(
            chkPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chkPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(chkPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(phaseChk)
                    .addComponent(amplitudeChk)
                    .addComponent(intensityChk))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(chkPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(realChk)
                    .addComponent(imaginaryChk)
                    .addComponent(hologramChk))
                .addContainerGap())
        );

        btnsPanel.setMaximumSize(new java.awt.Dimension(270, 66));
        btnsPanel.setMinimumSize(new java.awt.Dimension(270, 66));

        settingsBtn.setText("Settings");
        settingsBtn.setMaximumSize(new java.awt.Dimension(132, 23));
        settingsBtn.setMinimumSize(new java.awt.Dimension(132, 23));
        settingsBtn.setPreferredSize(new java.awt.Dimension(132, 23));
        settingsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsBtnActionPerformed(evt);
            }
        });

        runBtn.setText("RUN");
        runBtn.setToolTipText("");
        runBtn.setMaximumSize(new java.awt.Dimension(132, 23));
        runBtn.setMinimumSize(new java.awt.Dimension(132, 23));
        runBtn.setPreferredSize(new java.awt.Dimension(132, 23));
        runBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout btnsPanelLayout = new javax.swing.GroupLayout(btnsPanel);
        btnsPanel.setLayout(btnsPanelLayout);
        btnsPanelLayout.setHorizontalGroup(
            btnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, btnsPanelLayout.createSequentialGroup()
                .addGroup(btnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(settingsBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(runBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        btnsPanelLayout.setVerticalGroup(
            btnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(runBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(settingsBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(parametersImagingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(parametersInterfPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(parametersInputPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(chkPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 263, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(parametersInputPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(parametersImagingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(parametersInterfPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(5, 5, 5))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void settingsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsBtnActionPerformed
        if (settingsFrame == null || !settingsFrame.isDisplayable()) {
            settingsFrame = new ImagingSettingsFrame(this);
            settingsFrame.setVisible(true);
        } else {
            settingsFrame.setState(Frame.NORMAL);
            settingsFrame.toFront();
        }
    }//GEN-LAST:event_settingsBtnActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (settingsFrame != null && settingsFrame.isVisible()) {
            settingsFrame.setVisible(false);
            settingsFrame.dispose();
        }

        savePrefs();
        ImagePlus.removeImageListener(this);
        setVisible(false);
        dispose();
    }//GEN-LAST:event_formWindowClosing

    private void textFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textFieldFocusGained
        JTextField field = (JTextField) evt.getComponent();
        field.selectAll();
    }//GEN-LAST:event_textFieldFocusGained

    private void runBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runBtnActionPerformed
        boolean statusOk = parseValues();
        if (!statusOk) {
            JOptionPane.showMessageDialog(this, "The parameters are invalid");
            return;
        }
        
        int in1ID = input1Combo.getSelectedIndex();
        int in2ID = input2Combo.getSelectedIndex();
        statusOk = setInputImages(in1ID, in2ID);
        if (!statusOk) {
            JOptionPane.showMessageDialog(this, "The input images are invalid");
            return;
        }
        
        imager.createImage();
    }//GEN-LAST:event_runBtnActionPerformed

    private void phaseChkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_phaseChkActionPerformed
        phaseEnabled = phaseChk.isSelected();
    }//GEN-LAST:event_phaseChkActionPerformed

    private void amplitudeChkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_amplitudeChkActionPerformed
        amplitudeEnabled = amplitudeChk.isSelected();
    }//GEN-LAST:event_amplitudeChkActionPerformed

    private void intensityChkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_intensityChkActionPerformed
        intensityEnabled = intensityChk.isSelected();
    }//GEN-LAST:event_intensityChkActionPerformed

    private void realChkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_realChkActionPerformed
        realEnabled = realChk.isSelected();
    }//GEN-LAST:event_realChkActionPerformed

    private void imaginaryChkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imaginaryChkActionPerformed
        imaginaryEnabled = imaginaryChk.isSelected();
    }//GEN-LAST:event_imaginaryChkActionPerformed

    private void azimuthFieldtextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_azimuthFieldtextFieldFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_azimuthFieldtextFieldFocusGained

    private void polarFieldtextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_polarFieldtextFieldFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_polarFieldtextFieldFocusGained

    private void ampMinFieldtextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ampMinFieldtextFieldFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_ampMinFieldtextFieldFocusGained

    private void phaseMinFieldtextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_phaseMinFieldtextFieldFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_phaseMinFieldtextFieldFocusGained

    private void ampMaxFieldtextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ampMaxFieldtextFieldFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_ampMaxFieldtextFieldFocusGained

    private void phaseMaxFieldtextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_phaseMaxFieldtextFieldFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_phaseMaxFieldtextFieldFocusGained

    private void hologramChkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hologramChkActionPerformed
        hologramEnabled = hologramChk.isSelected();
    }//GEN-LAST:event_hologramChkActionPerformed

    private void inputTypeComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputTypeComboActionPerformed
        if (inputTypeCombo.getSelectedIndex() == 0) {
            input1Label.setText("Real:");
            input2Label.setText("Imaginary:");
            // Disable limit fields
            setLimitsEnable(false);
        } else {
            input1Label.setText("Amplitude:");
            input2Label.setText("Phase:");
            setLimitsEnable(true);
        }
    }//GEN-LAST:event_inputTypeComboActionPerformed

    private void diffLimitChkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_diffLimitChkActionPerformed
        azimuthField.setEnabled(!diffLimitChk.isSelected());
        polarField.setEnabled(!diffLimitChk.isSelected());
        verifier.checkPolarField();
        verifier.checkAzimuthField();
    }//GEN-LAST:event_diffLimitChkActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel ampLimitsLabel;
    private javax.swing.JTextField ampMaxField;
    private javax.swing.JTextField ampMinField;
    private javax.swing.JCheckBox amplitudeChk;
    private javax.swing.JTextField azimuthField;
    private javax.swing.JLabel azimuthLabel;
    private javax.swing.JPanel btnsPanel;
    private javax.swing.JPanel chkPanel;
    private javax.swing.JCheckBox diffLimitChk;
    private javax.swing.JCheckBox hologramChk;
    private javax.swing.JCheckBox imaginaryChk;
    private javax.swing.JComboBox input1Combo;
    private javax.swing.JLabel input1Label;
    private javax.swing.JComboBox input2Combo;
    private javax.swing.JLabel input2Label;
    private javax.swing.JTextField inputHField;
    private javax.swing.JLabel inputHLabel;
    private javax.swing.JComboBox inputTypeCombo;
    private javax.swing.JLabel inputTypeLabel;
    private javax.swing.JTextField inputWField;
    private javax.swing.JLabel inputWLabel;
    private javax.swing.JCheckBox intensityChk;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField lambdaField;
    private javax.swing.JLabel lambdaLabel;
    private javax.swing.JTextField moMagField;
    private javax.swing.JLabel moMagLabel;
    private javax.swing.JTextField moNAField;
    private javax.swing.JLabel moNALabel;
    private javax.swing.ButtonGroup outputGroup;
    private javax.swing.JPanel parametersImagingPanel;
    private javax.swing.JPanel parametersInputPanel;
    private javax.swing.JPanel parametersInterfPanel;
    private javax.swing.JCheckBox phaseChk;
    private javax.swing.JLabel phaseLimitsLabel;
    private javax.swing.JTextField phaseMaxField;
    private javax.swing.JTextField phaseMinField;
    private javax.swing.JTextField polarField;
    private javax.swing.JLabel polarLabel;
    private javax.swing.JCheckBox realChk;
    private javax.swing.JTextField roughnessField;
    private javax.swing.JLabel roughnessLabel;
    private javax.swing.JButton runBtn;
    private javax.swing.JButton settingsBtn;
    private javax.swing.JTextField tlFocalField;
    private javax.swing.JLabel tlFocalLabel;
    // End of variables declaration//GEN-END:variables
}
