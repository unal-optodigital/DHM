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

/**
 *
 * @author: Carlos Buitrago-Duque <cabuitragod@unal.edu.co>
 * @author: Jorge Garcia-Sucerquia <jisucerquia@unal.edu.co>
 */
public interface PreferencesKeys {

    // Frame parameters
    final static String FRAME_LOC_X = "FRAME_LOC_X";
    final static String FRAME_LOC_Y = "FRAME_LOC_Y";
    
    // SIMULATION PREFERENCES
    // Imaging parameters
    final static String IMG_LAMBDA = "IMG_LAMBDA";
    final static String IMG_ROUGHNESS = "IMG_ROUGHNESS";
    final static String IMG_MO_MAGNIFICATION = "IMG_MO_MAGNIFICATION";
    final static String IMG_MO_NA = "IMG_MO_NA";
    final static String IMG_TL_FOCAL = "IMG_TL_FOCAL";
    final static String IMG_INPUT_WIDTH = "IMG_INPUT_WIDTH";
    final static String IMG_INPUT_HEIGHT = "IMG_INPUT_HEIGHT";
    
    // Interference parameters
    final static String INTF_DIFFLIMITED = "INTF_DIFFLIMITED";
    final static String INTF_AZIMUTH = "INTF_AZIMUTH";
    final static String INTF_POLAR = "INTF_POLAR";
    
    // Input parameters
    final static String IN_AMPLI_MIN = "IN_AMPLI_MIN";
    final static String IN_AMPLI_MAX = "IN_AMPLI_MAX";
    final static String IN_PHASE_MIN = "IN_PHASE_MIN";
    final static String IN_PHASE_MAX = "IN_PHASE_MAX";
    
    // Output parameters
    final static String OUT_PHASE_CHECKED = "OUT_PHASE_CHECKED";
    final static String OUT_AMPLITUDE_CHECKED = "OUT_AMPLITUDE_CHECKED";
    final static String OUT_INTENSITY_CHECKED = "OUT_INTENSITY_CHECKED";
    final static String OUT_REAL_CHECKED = "OUT_REAL_CHECKED";
    final static String OUT_IMAGINARY_CHECKED = "OUT_IMAGINARY_CHECKED";
    final static String OUT_HOLOGRAM_CHECKED = "OUT_HOLOGRAM_CHECKED";

    // Units
    final static String IMG_LAMBDA_UNITS = "IMG_LAMBDA_UNITS";
    final static String IMG_TL_FOCAL_UNITS = "IMG_TL_FOCAL_UNITS";
    final static String IMG_INPUT_UNITS = "IMG_INPUT_UNITS";
    
    // RECONSTRUCTION PREFERENCES
    // Imaging parameters
    final static String REC_LAMBDA = "REC_LAMBDA";
    final static String REC_MO_MAGNIFICATION = "REC_MO_MAGNIFICATION";
    final static String REC_MO_NA = "REC_MO_NA";
    final static String REC_TL_FOCAL = "REC_TL_FOCAL";
    final static String REC_INPUT_WIDTH = "REC_INPUT_WIDTH";
    final static String REC_INPUT_HEIGHT = "REC_INPUT_HEIGHT";
}
