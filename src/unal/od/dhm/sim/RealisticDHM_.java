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

import ij.ImageJ;
import ij.plugin.PlugIn;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import unal.od.dhm.AboutFrame;

/**
 *
 * @author: Carlos Buitrago-Duque <cabuitragod@unal.edu.co>
 * @author: Jorge Garcia-Sucerquia <jisucerquia@unal.edu.co>
 */
public class RealisticDHM_ implements PlugIn {

    private static ImagingFrame MAIN_FRAME;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new ImageJ();
        new RealisticDHM_().run("");
    }

    @Override
    public void run(String string) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
        }

        if (string.equalsIgnoreCase("about")) {
            new AboutFrame().setVisible(true);
            return;
        }
        
        if (MAIN_FRAME == null || !MAIN_FRAME.isDisplayable()) {
            MAIN_FRAME = new ImagingFrame();
            MAIN_FRAME.setVisible(true);
        } else {
            MAIN_FRAME.setVisible(true);
            MAIN_FRAME.toFront();
        }
    }

}
