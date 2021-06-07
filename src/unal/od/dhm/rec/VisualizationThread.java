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

/**
 *
 * @author Carlos Buitrago <cabuitragod@unal.edu.co>
 * @author: Jorge Garcia-Sucerquia <jisucerquia@unal.edu.co>
 */
public class VisualizationThread extends Thread {

    private final LiveReconstruction_ live;
    volatile boolean stop = false;
    volatile boolean running = false;

    public VisualizationThread(LiveReconstruction_ frame) {
        this.live = frame;
    }

    @Override
    public void run() {
        this.stop = false;
        this.running = true;
        long timeAccum = 0;
        int i = 0;

        while (!this.stop) {
            // Get the time of init
            long time = System.nanoTime();
            // Update the image information
            boolean ret = this.live.updateImage();
            // Stop if something goes wrong
            if (!ret) {
                //this.running = false;
                //return;
                live.stopLive();
                continue;
            }
            // Delay the thread execution
            try {
                VisualizationThread.sleep(10L);
            } catch (InterruptedException ex) {
                System.out.println("Couldn't sleep: " + ex.getMessage());
            }
            // Get the time of end
            time = System.nanoTime() - time;
            timeAccum += time;
            // Update the rate after 5 frames
            if (++i % 5 != 0) {
                continue;
            }
            // Calculate the FPS rate
            double fps = 5.0 / ((double) timeAccum * 1.0E-9);
            this.live.getImageWindow().setFPS(fps);
            timeAccum = 0;
            i = 0;
        }
    }

    /**
     * Asks the thread to friendly stop the live display
     */
    public void stopDisplay() {
        this.stop = true;
    }

    /**
     * Checks whether the live display is running
     *
     * @return running status
     */
    public boolean isRunning() {
        return this.running;
    }

}
