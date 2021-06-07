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

import ij.ImagePlus;
import ij.measure.Calibration;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import java.util.Random;
import javax.swing.JOptionPane;
import org.jtransforms.fft.FloatFFT_2D;
import unal.od.jdiffraction.cpu.utils.ArrayUtils;

/**
 *
 * @author: Carlos Buitrago-Duque <cabuitragod@unal.edu.co>
 * @author: Jorge Garcia-Sucerquia <jisucerquia@unal.edu.co>
 */
public class Imager {

    static float PI = (float) Math.PI;
    private final ImagingFrame parentFrame;

    private float[][] inputField;
    private float[][] outputField;
    private float[][] hologramField;
    private float[][] hologram;
    private float ampMin = 1, ampMax = 2;                  // Amplitude limits
    private float phaMin = -1 * PI / 4, phaMax = PI / 4;   // Phase limits
    private double lambda = 633;        // Ilumination wavelenght [nm]
    private double roughness = 0.5;     // Superficial roughness [%λ]
    private double magnification = 40.0;     // MO magnificaiton [X]
    private double moNA = 0.65;         // MO numerical aperture [adim]
    private double tlFocal = 200;       // TL focal length [mm]
    private double width = 200;         // Sample width [um]
    private double height = 200;        // Sample height [um]
    private int M, N;                   // Image size [px]

    private double polarDir = 0.01;
    private double azimuthalDir = PI / 4;
    private boolean isDiffractionLimited = false;

    private boolean isInputReady = false;
    private boolean isFieldReady = false;
    private boolean isIlluminationReady = false;
    private boolean isImagingReady = false;
    private boolean isAnglesReady = false;
    private boolean isReady = false;

    private double objectiveFocal;
    private double inputPitch;
    private double focalPitch;
    private double outputPitch;

    private FloatFFT_2D fft;
    private Calibration cali;

    public Imager(ImagingFrame parent) {
        this.parentFrame = parent;
    }

    public void setInputParameters(double width, double height) {
        this.width = width;
        this.height = height;
        this.isInputReady = true;
    }

    public void setInputParameters(double width, double height, double minAmp,
            double maxAmp, double minPha, double maxPha) {

        this.width = width;
        this.height = height;
        this.ampMin = (float) minAmp;
        this.ampMax = (float) maxAmp;
        this.phaMin = (float) minPha;
        this.phaMax = (float) maxPha;

        this.isInputReady = true;
    }

    public void setIllumination(double wavelength, double roughness) {
        this.lambda = wavelength;
        this.roughness = roughness;

        this.isIlluminationReady = true;
    }

    public void setImaging(double magnification, double MONA, double TLfocal) {
        this.magnification = magnification;
        this.moNA = MONA;
        this.tlFocal = TLfocal;

        this.isImagingReady = true;
    }

    public void setInputImages(int inputType, int M, int N, float[][] input1, float[][] input2) {
        if (!isInputReady) {
            JOptionPane.showMessageDialog(parentFrame, "The input parameters must be set before loading the images");
            return;
        }

        this.M = M;
        this.N = N;

        // Optical roughness settings
        double roughStdD = (roughness / 2) * lambda / 3;    // Maximum at 3 sigma
        Random randGen = new Random();

        switch (inputType) {
            case 1:
                // Real & Imaginary
                inputField = ArrayUtils.complexAmplitude2(input1, input2);
                break;
            case 2:
                // Amplitude & Phase
                float input1min = 0,
                 input1max = 0;
                float input2min = 0,
                 input2max = 0;

                if (input1 != null) {
                    input1min = ArrayUtils.min(input1);
                    input1max = ArrayUtils.max(input1);
                }
                if (input2 != null) {
                    input2min = ArrayUtils.min(input2);
                    input2max = ArrayUtils.max(input2);
                }

                float[][] scaledInput1 = new float[M][N];
                float[][] scaledInput2 = new float[M][N];
                float delta1 = input1max - input1min;
                float delta2 = input2max - input2min;

                for (int i = 0; i < M; i++) {
                    for (int j = 0; j < N; j++) {
                        // Scale amplitude
                        if (input1 != null) {
                            scaledInput1[i][j] = input1[i][j] - input1min;
                            if (delta1 != 0) {
                                scaledInput1[i][j] = scaledInput1[i][j] / delta1;
                            }
                            scaledInput1[i][j] = scaledInput1[i][j] * (ampMax - ampMin);
                            scaledInput1[i][j] = scaledInput1[i][j] + ampMin;
                        } else {
                            scaledInput1[i][j] = ampMax;
                        }

                        // Scale phase
                        if (input2 != null) {
                            scaledInput2[i][j] = input2[i][j] - input2min;
                            if (delta2 != 0) {
                                scaledInput2[i][j] = scaledInput2[i][j] / delta2;
                            }
                            scaledInput2[i][j] = scaledInput2[i][j] * (phaMax - phaMin);
                            scaledInput2[i][j] = scaledInput2[i][j] + phaMin;
                        } else {
                            scaledInput2[i][j] = 0;
                        }

                        // Introduce phase roughness
                        double randomNoise = (randGen.nextGaussian() * roughStdD);
                        randomNoise = randomNoise * 2 * PI / lambda;
                        scaledInput2[i][j] = scaledInput2[i][j] + (float) randomNoise;
                    }
                }

                inputField = ArrayUtils.complexAmplitude(scaledInput2, scaledInput1);
                break;
            default:
                return;
        }

        this.isFieldReady = true;
    }

    public void setAngles(boolean isDiffractionLimited, double azimuth, double polar) {
        this.isDiffractionLimited = isDiffractionLimited;
        if (!isDiffractionLimited) {
            modifyInteferenceAngles(polar, azimuth);
        }

        this.isAnglesReady = true;
    }

    public void createImage() {
        isReady = (isFieldReady && isInputReady && isIlluminationReady
                && isImagingReady && isAnglesReady);
        if (!isReady) {
            return;
        }

        calculateParameters();
        firstLens();
        pupilPlane();
        secondLens();
        createHologram();        
        System.out.printf("\nDif Limit: %b; Polar: %f, Azimuthal: %f\n", isDiffractionLimited, polarDir, azimuthalDir);
        isParametersValid();

        createOutputs();

        // Update the used polar value
        String newPolar = String.format("%.3g%n", polarDir);//Double.toString(polarDir);
        String newAzimuth = String.format("%.3g%n", azimuthalDir);//Double.toString(azimuthalDir);
        parentFrame.updateAngleValues(newPolar, newAzimuth);
    }

    private void calculateParameters() {
        objectiveFocal = tlFocal / magnification;
        fft = new FloatFFT_2D(M, N);
    }

    private void isParametersValid() {
        double minVal = moNA / magnification;
        double maxVal = (lambda) / (2 + (Math.sqrt(2) * 3));
        maxVal /= outputPitch;

        System.out.printf("pitch: %f min: %f, max: %f", outputPitch, minVal, maxVal);
        String maxNa = Double.toString(Math.round(maxVal * magnification * 1000.0) / 1000.0);

        if (maxVal < minVal) {
            JOptionPane.showMessageDialog(parentFrame,
                    "The MO selection is inadequate for the system \r\n Maximum admisible NA is " + maxNa);
        }
    }

    private void firstLens() {
        inputPitch = width / M;
        fft.complexForward(inputField);
        ArrayUtils.complexShift(inputField);

        double focalPitchScale = lambda * objectiveFocal;
        focalPitch = focalPitchScale / (M * inputPitch);
    }

    private void pupilPlane() {
        // Pupil parameters
        double pupilRadius = (moNA * tlFocal) / magnification;
        double pupilScaledRadius = (pupilRadius) / focalPitch;
        // Pupil creation
        int[][] pupil = new int[M][N];
        double pupilCenter = (pupil.length - 1) / 2.0;
        for (int col = 0; col < pupil.length; col++) {
            int[] row = new int[pupil.length];
            double yy = col - pupilCenter;
            for (int x = 0; x < row.length; x++) {
                double xx = x - pupilCenter;
                if (Math.sqrt(xx * xx + yy * yy) <= pupilScaledRadius) {
                    row[x] = 1;
                }
            }
            pupil[col] = row;
        }

        // Spatial filtering
        outputField = new float[M][N];
        float[][] procReal = ArrayUtils.real(inputField);
        float[][] procImag = ArrayUtils.imaginary(inputField);
        float[][] outputReal = new float[M][N];
        float[][] outputImag = new float[M][N];
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                outputReal[i][j] = procReal[i][j] * (float) pupil[i][j];
                outputImag[i][j] = procImag[i][j] * (float) pupil[i][j];
            }
        }
        outputField = ArrayUtils.complexAmplitude2(outputReal, outputImag);

    }

    private void secondLens() {
        ArrayUtils.complexShift(outputField);
        fft.complexForward(outputField);

        double outputPitchScale = lambda * tlFocal;
        outputPitch = outputPitchScale / (focalPitch * M);

    }

    private void createOutputs() {
        cali = new Calibration();
        cali.setUnit("um");
        cali.pixelWidth = outputPitch;
        cali.pixelHeight = outputPitch;

        char[] selOut = parentFrame.getSelectedOutputs();
        for (int i = 0; i < selOut.length; i++) {
            switch (selOut[i]) {
                case 'A': // Amplitude
                    createImage(ArrayUtils.modulus(outputField), "Amplitude");
                    break;
                case 'H': // Hologram
                    createImage(hologram, "Hologram");
                    break;
                case 'J': // Imaginary
                    createImage(ArrayUtils.imaginary(outputField), "Imaginary");
                    break;
                case 'I': // Intensity
                    createImage(ArrayUtils.modulusSq(outputField), "Intensity");
                    break;
                case 'P': // Phase
                    createImage(ArrayUtils.phase(outputField), "Phase");
                    break;
                case 'R': // Real
                    createImage(ArrayUtils.real(outputField), "Real");
                    break;
                default:
                    break;
            }
        }
    }

    public void modifyInteferenceAngles(double polarDir, double azimuthalDir) {
        this.polarDir = polarDir;
        this.azimuthalDir = azimuthalDir;
    }

    private void createHologram() {
        // Diffraction limit
        if (isDiffractionLimited) {
            polarDir = (lambda) / (2 * outputPitch);
            azimuthalDir = PI / 4;
        }

        // Reference direction
        double kx = Math.sin(polarDir) * Math.cos(azimuthalDir);
        double ky = Math.sin(polarDir) * Math.sin(azimuthalDir);
        double kz = Math.cos(polarDir);

        float[][] objReal = ArrayUtils.real(outputField);
        float[][] objImag = ArrayUtils.imaginary(outputField);
        float[][] objAmp = ArrayUtils.modulus(outputField);
        float meanValue = 0;
        float dataCount = 0;
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                meanValue += objAmp[i][j];
                dataCount++;
            }
        }
        meanValue /= dataCount;

        // Create reference
        float[][] refAmp = new float[M][N];
        float[][] refPha = new float[M][N];

        double midPoint = (refPha.length - 1) / 2.0;
        double kVec = 2 * PI / (lambda);

        // TODO Instead of calculating phase, Re/Im could be directly computed
        // to latter 
        for (int i = 0; i < M; i++) {
            double phaPtX = kx * (i - midPoint) * outputPitch;
            for (int j = 0; j < N; j++) {
                double phaPtY = phaPtX + ky * (j - midPoint) * outputPitch;
                phaPtY = phaPtY + kz * 100;
                refPha[i][j] = (float) (kVec * phaPtY);
                refAmp[i][j] = meanValue / 4;
            }
        }
        float[][] reference = ArrayUtils.complexAmplitude(refPha, refAmp);
        float[][] refReal = ArrayUtils.real(reference);
        float[][] refImag = ArrayUtils.imaginary(reference);

        float[][] holoReal = new float[M][N];
        float[][] holoImag = new float[M][N];
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                holoReal[i][j] = refReal[i][j] + objReal[i][j];
                holoImag[i][j] = refImag[i][j] + objImag[i][j];
            }
        }
        hologramField = ArrayUtils.complexAmplitude2(holoReal, holoImag);
        hologram = ArrayUtils.modulusSq(hologramField);
    }

    private void createImage(float[][] toShow, String name) {
        ImageProcessor ip = new FloatProcessor(toShow);
        ImagePlus imp = new ImagePlus(name, ip);
        imp.setCalibration(cali);
        imp.show();
    }

}
