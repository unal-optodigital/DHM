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
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.ImageWindow;
import ij.measure.Calibration;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;
import java.awt.Button;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.jtransforms.fft.FloatFFT_2D;
import unal.od.dhm.PreferencesKeys;
import unal.od.jdiffraction.cpu.FloatAngularSpectrum;
import unal.od.jdiffraction.cpu.FloatPropagator;
import unal.od.jdiffraction.cpu.utils.ArrayUtils;

/**
 * Creates a superimposed visualization with the reconstruction in Amplitude,
 * Intensity or Phase. This version was tailored for a telecentric image-plane
 * system and leaves all the processing requirements to the CPU. A GPU version 
 * is being developed but will be constrained to CUDA-capable devices.
 *
 * @version 1.0
 *
 * @author Carlos Buitrago <cabuitragod@unal.edu.co>
 * @author: Jorge Garcia-Sucerquia <jisucerquia@unal.edu.co>
 */
public class LiveReconstruction_ implements PlugInFilter, PreferencesKeys {

    // Objects to be aware of
    private ImagePlus imp;
    private ImageProcessor ip;
    private ImagePlus impReco;
    private ImageProcessor ipReco;
    private VisualizationThread visThread;
    private ReconstructionWindow imgWindow;

    // Visualization parameters
    private int bitDepth;
    private int imgWidth;
    private int imgHeight;
    private boolean logScale;
    private String imageTitle;
    private static final int RECO_FFT = 0;
    private static final int RECO_INTENSITY = 1;
    private static final int RECO_AMPLITUDE = 2;
    private static final int RECO_PHASE = 3;
    private int recoType = RECO_FFT;

    // Imaging parameters
    private float wavelength;
    private float indx, indy;
    
    // Microscope parameters
    private float moNA;
    private float moMag;
    private float tlFocal;
    
    // Tuning parameters
    private float refocusZ = 0.0f;
    private float fxTuning = 0.0f;
    private float fyTuning = 0.0f;
    private float linTuning = 0.0f;
    private float compFxTune = 0.0f;
    private float compFyTune = 0.0f;
    
    // Field parameters
    private float[][] field;
    private float[][] field_fft;
    private float[][] filteredField;
    private float[][] outputField;

    // Processing objects
    private FloatFFT_2D fft;
    private FloatPropagator propagator;
    private boolean filtered;
    private int roi_x;
    private int roi_y;
    private int roi_width;
    private int roi_height;
    private int[][] roi_mask;

    // Filter objects
    ImagePlus impFFT;
    ImageWindow filterWnd;
    private final Calibration cali = new Calibration();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ImageJ ij = new ImageJ();
        ImagePlus imp = new ImagePlus("src/DM_01.png");
        imp.show();

        Class<LiveReconstruction_> clazz = LiveReconstruction_.class;
        String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
        String pluginsDir = url.substring(5, url.length() - clazz.getName().length() - 6);
        System.setProperty("plugins.dir", pluginsDir);
        IJ.runPlugIn((String) clazz.getName(), (String) "");
    }

    @Override
    public int setup(String string, ImagePlus ip) {
        
        if(ip == null) {
            JOptionPane.showMessageDialog(null,
                    "This plugin requires an existing image or video feed\r\nPlease open a valid input source and retry");
            return DONE;
        }
        
        // Image parameters
        this.bitDepth = ip.getBitDepth();
        this.imgWidth = ip.getWidth();
        this.imgHeight = ip.getHeight();
        this.imageTitle = ip.getTitle();
        this.imp = ip;

        // Reconstruction parameters
        this.impReco = null;
        this.ipReco = null;
        this.filtered = false;

        // Window parameters
        this.visThread = null;
        this.imgWindow = null;
        this.logScale = true;
        
        // User preferences
        Preferences pref = Preferences.userNodeForPackage(getClass());
        float lambdaUser = pref.getFloat(REC_LAMBDA, Float.NaN);
        float inputWUser = pref.getFloat(REC_INPUT_WIDTH, Float.NaN);
        float inputHUser = pref.getFloat(REC_INPUT_HEIGHT, Float.NaN);
        moMag = pref.getFloat(REC_MO_MAGNIFICATION, Float.NaN);
        wavelength = (float)(lambdaUser * 1E-6);
        indx = (float)(inputWUser / imgWidth);
        indy = (float)(inputHUser / imgHeight);
        
        // Set initial size calibration
        cali.setUnit("um");
        cali.pixelWidth = indx * 1E3 / moMag; // User size stored in mm
        cali.pixelHeight = indy * 1E3 / moMag;
        
        // PluginFilter parameters
        int flags = DOES_ALL + NO_CHANGES;

        // Set Look-and-Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            System.out.println("Couldn't load the LAF: " + ex.getMessage());
        }

        // Used to be: 137
        return flags;
    }

    @Override
    public void run(ImageProcessor ip) {
        this.ip = ip;
        this.startLive();
    }

    /**
     * Updates the image information to the Visualization Thread. This is where
     * the magic happens
     *
     * @return success
     */
    public boolean updateImage() {
        
        if(this.ip == null) {
            JOptionPane.showMessageDialog(null,
                    "This plugin requires an existing image or video feed\r\nPlease open a valid input source and retry");
            return false;
        }
        
    	// Elevate 16-bit images to 32-bit
    	if(this.bitDepth == 16) {
    		ImagePlus copyImp = this.imp;
    		new ImageConverter(copyImp).convertToGray32();
    		this.ip = copyImp.getProcessor();
    	}
    	
        // Sets the input field creating a complex array with null imaginaries
        this.field = ArrayUtils.complexAmplitude2(this.ip.getFloatArray(), null);

        if (recoType != RECO_FFT) {
            propagator = new FloatAngularSpectrum(imgWidth, imgHeight, wavelength, refocusZ, indx, indy);
            getDisplayableFFT();
            center();
            outputField = new float[imgWidth][2 * imgHeight];
            for (int i = 0; i < imgWidth; i++) {
                System.arraycopy(filteredField[i], 0, outputField[i], 0, 2 * imgHeight);	
            }
            propagator.diffract(outputField);
        }

        switch (recoType) {
            case RECO_FFT:
                getDisplayableFFT();
                showSpectrum();
                impReco.setCalibration(null);
                //showReco("FFT");
                break;
            case RECO_INTENSITY:
                showIntensity();
                impReco.setCalibration(cali);
                break;
            case RECO_AMPLITUDE:
                showAmplitude();
                impReco.setCalibration(cali);
                break;
            case RECO_PHASE:
                if(fxTuning != 0.0 || fyTuning != 0.0 || linTuning != 0.0)
                    compensatePhase();
                showPhase();
                impReco.setCalibration(cali);
                break;
            default:
                break;
        }

        if (this.imgWindow == null || this.imgWindow.isClosed()) {
            this.imgWindow = new ReconstructionWindow(impReco, "Reco", this);
        } else if (this.imgWindow != null) {
            this.imgWindow.newImage(impReco);
        }
        recoType = this.imgWindow.getRecoType();

        return true;
    }

    /**
     * Creates ImagePLus and displays it
     */
    private void showReco(String titlePrefix) {
        if (logScale) { // Logaritmic representation
            ipReco.log();
        }
        
        // Calibration data
        double minVal = ipReco.getMin();
        double maxVal = ipReco.getMax();
        double[] coeff = new double[2]; // y = m*x + b
        coeff[0] = minVal; // b coefficient
        coeff[1] = (maxVal - minVal)/255; // m coefficient
        String unit = (recoType == RECO_PHASE) ? "rad" : "adim";
        
        String recoTitle = titlePrefix + " of " + imageTitle;
        impReco = new ImagePlus(recoTitle, ipReco.convertToByteProcessor());
        cali.setFunction(Calibration.STRAIGHT_LINE, coeff, unit); // Calibration
        
        if (recoType == RECO_FFT && !logScale) {
            IJ.run(impReco, "Enhance Contrast...", "saturated=0 equalize");
        }
    }

    /**
     * Shows the FFT information and allows filtering
     */
    private void getDisplayableFFT() {
        // Calculate FFT
        // Clone the field to avoid thread collision
        this.field_fft = new float[imgWidth][2 * imgHeight];
        for (int i = 0; i < imgWidth; i++) {
            System.arraycopy(field[i], 0, field_fft[i], 0, 2 * imgHeight);	
        }
        
        fft = new FloatFFT_2D(imgWidth, imgHeight);
        fft.complexForward(field_fft);
        ArrayUtils.complexShift(field_fft);
    }
    
    /**
     * Adds a linear phase compensation to the reconstructed information
     */
    private void compensatePhase() {
        // Create amplitude
        float[][] uniformAmplitude = new float[imgWidth][imgHeight];
        for (int i = 0; i < imgWidth; i++) {
            for (int j = 0; j < imgHeight; j++) {
                uniformAmplitude[i][j] = 1;
            }
        }
        
        float k = (float) (2 * Math.PI / wavelength);
        
        float[][] compenPhase = new float[imgWidth][imgHeight];
        for (int i = 0; i < imgWidth; i++) {
            int i2 = i - (imgWidth / 2);
            for (int j = 0; j < imgHeight; j++) {
                int j2 = j - (imgHeight / 2);
                compenPhase[i][j] = (float) (k * ( Math.sin(compFxTune) * i2 * indx 
                        + Math.sin(compFyTune) * j2 * indy ));
                compenPhase[i][j] += linTuning; 
            }
        }
        
        float[][] compenField = ArrayUtils.complexAmplitude(compenPhase, uniformAmplitude);
        ArrayUtils.complexMultiplication2(outputField,compenField);
    }
    
    /**
     * Shows the FFT spectrum
     */
    private void showSpectrum() {
        // Calculate the power spectrum from FFT
        float[][] modulus = ArrayUtils.modulus(field_fft);
        // Create Image with resulting information
        ipReco = new FloatProcessor(modulus);
        // Display the result
        showReco("FFT");
    }
    
    /**
     * Shows the Intensity reconstruction
     */
    private void showIntensity() {
        // Calculate intensity
        ipReco = new FloatProcessor(ArrayUtils.modulusSq(outputField));
        // Display the result
        showReco("Intensity");
    }

    /**
     * Shows the Amplitude reconstruction
     */
    private void showAmplitude() {
        // Calculate the amplitude
        ipReco = new FloatProcessor(ArrayUtils.modulus(outputField));
        // Display the result
        showReco("Amplitude");
    }

    /**
     * Shows the Phase reconstruction
     */
    private void showPhase() {
        // Calculate the phase
        ipReco = new FloatProcessor(ArrayUtils.phase(outputField));
        // Display the result
        showReco("Phase");
    }

    /**
     * Starts the visualization thread
     */
    public void startLive() {
        if (this.visThread == null) {
            this.visThread = new VisualizationThread(this);
        }
        this.visThread.start();
    }

    /**
     * Stops the visualization thread nicely
     */
    public void stopLive() {
        this.visThread.stopDisplay();
        try {
            this.visThread.join();
        } catch (InterruptedException ex) {
            System.out.println("Couldn't stop live display: " + ex.getMessage());
        }
        this.visThread = null;
    }

    /**
     * Gets the ID of the processed image
     *
     * @return image ID
     */
    public int getID() {
        return this.imp.getID();
    }

    /**
     * Gets the visualization window
     *
     * @return active reconstruction window
     */
    public ReconstructionWindow getImageWindow() {
        return this.imgWindow;
    }

    /**
     * Sets the logarithmic scaling
     *
     * @param log
     */
    void setLogScale(boolean log) {
        this.logScale = log;
    }

    /**
     * Creates filter selection window
     */
    public void createFilterWindow() {
        System.out.println("Setting new filter");

        float[][] field_filter_fft = new float[imgWidth][2 * imgHeight];
        for (int i = 0; i < imgWidth; i++) {
            System.arraycopy(field[i], 0, field_filter_fft[i], 0, 2 * imgHeight);	
        }
        
        // Calculate FFT        
        FloatFFT_2D fftWindow = new FloatFFT_2D(imgWidth, imgHeight);
        fftWindow.complexForward(field_filter_fft);
        ArrayUtils.complexShift(field_filter_fft);
        float[][] modulus = ArrayUtils.modulus(field_filter_fft);

        // Create Image with resulting information
        ImageProcessor ipFFT = new FloatProcessor(modulus);
        ipFFT.log();
        
        if (filterWnd == null || !filterWnd.isVisible()) {
            impFFT = new ImagePlus("Filter FFT", ipFFT.convertToByteProcessor());
            filterWnd = new ImageWindow(impFFT);
            Panel panel = new Panel();
            Button doneBtn = new Button("Done!");
            doneBtn.addActionListener((ActionEvent ae) -> {
                LiveReconstruction_.this.setNewFilter();
            });
            panel.add(doneBtn);
            filterWnd.add(panel);
            filterWnd.pack();
        } else {
            impFFT.setProcessor("Filter FFT", ipFFT.convertToByteProcessor());
            filterWnd.updateImage(impFFT);
            filterWnd.setVisible(true);
            filterWnd.toFront();
        }

    }
    
    protected void setImagingParameters(float lambda, float dx, float dy) {
        this.wavelength = (float) (lambda * 1E-6);
        this.indx = (float) (dx * 1E-6);
        this.indy = (float) (dy * 1E-6);
        
        cali.setUnit("um");
        cali.pixelWidth = dx;
        cali.pixelHeight = dy;
    }
    protected void setMicroscopeParameters(float magnification, float NA, float tubeLens) {
        this.moMag = magnification;
        this.moNA = NA;
        this.tlFocal = (float) (tubeLens * 1E-6);
        
        cali.pixelWidth = cali.pixelWidth / magnification;
        cali.pixelHeight = cali.pixelHeight / magnification;
    }
    protected void setTuningParameters(float zTune, float fxTune, float fyTune, float linTune) {
        this.refocusZ = (float) (zTune * 1E-6);       
        
        this.fxTuning = fxTune;
        this.fyTuning = fyTune;
        this.linTuning = linTune;
        
        reloadTuning();
    }
    
    private void reloadTuning() {
        this.compFxTune = (float) Math.asin(fxTuning * wavelength / (imgWidth * indx));
        this.compFyTune = (float) Math.asin(fyTuning * wavelength / (imgHeight * indy));
    }

    /**
     * Sets the currently selected ROI as the new filter
     */
    private void setNewFilter() {
        // Get the ROI
        ImageProcessor ipROI = impFFT.getProcessor();
        Rectangle roi = ipROI.getRoi();
        ImageProcessor ipMask = impFFT.getMask();

        // Update the information
        this.roi_x = roi.x;
        this.roi_y = roi.y;
        this.roi_width = roi.width;
        this.roi_height = roi.height;
        this.roi_mask = (ipMask != null) ? ipMask.getIntArray() : null;

        // Enables the filtered flag
        this.filtered = true;
        // Enables propagation options
        this.imgWindow.enableRecoTypes();
    }

    /**
     * Filters the field and centers its information
     */
    private void center() {
        if (!filtered) {
            System.out.println("No filter selected");
            return;
        }

        // If a regular ROI was selected...
        if (this.roi_mask == null) {
            //System.out.println("Null mask");
            filteredField = new float[imgWidth][2 * imgHeight];

            for (int i = 0; i < imgWidth; i++) {
                for (int j = 0; j < imgHeight; j++) {
                    filteredField[i][2 * j] = 0;
                    filteredField[i][2 * j + 1] = 0;
                }
            }

            int a = (imgWidth - roi_width - (2 * roi_x)) / 2;
            int b = (imgHeight - roi_height - (2 * roi_y)) / 2;
            for (int i = roi_x; i < roi_x + roi_width - 1; i++) {
                for (int j = roi_y; j < roi_y + roi_height - 1; j++) {
                    filteredField[i + a][2 * (j + b)] = field_fft[i][2 * j];
                    filteredField[i + a][2 * (j + b) + 1] = field_fft[i][2 * j + 1];
                }
            }

            ArrayUtils.complexShift(filteredField);
            fft.complexInverse(filteredField, true);
            return;
        }

        // Create complex matrix filled with zeros
        filteredField = new float[imgWidth][2 * imgHeight];
        for (int i = 0; i < imgWidth; i++) {
            for (int j = 0; j < imgHeight; j++) {
                filteredField[i][2 * j] = 0;
                filteredField[i][2 * j + 1] = 0;
            }
        }

        // New size and index
        int a = (imgWidth - roi_width - 2 * roi_x) / 2;
        int b = (imgHeight - roi_height - 2 * roi_y) / 2;
        int i2 = 0;

        for (int i = roi_x; i < (roi_x + roi_width); i++) {
            int j2 = 0;
            for (int j = roi_y; j < (roi_y + roi_height); j++) {
                if (roi_mask[i2][j2] != 0) {
                    filteredField[i + a][2 * (j + b)] = field_fft[i][2 * j];
                    filteredField[i + a][2 * (j + b) + 1] = field_fft[i][(2 * j) + 1];
                }
                j2++;
            }
            i2++;
        }

        ArrayUtils.complexShift(filteredField);        
        fft.complexInverse(filteredField, true);
    }

}
