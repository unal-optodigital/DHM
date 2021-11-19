# Digital Holographic Microscopy (DHM)
## An ImageJ Plugin

"DHM" is a plugin developed to work on the well-known software for image processing [ImageJ](https://imagej.nih.gov/ij/index.html) or any of its derivate platforms like [Fiji](https://fiji.sc/) or [Micro-Manager](https://micro-manager.org/). This plugin allows both the realistic simulation and real-time reconstruction of digital holograms.

The simulation module implements a numerical telecentric image-plane DHM recording architecture with user-configurable imaging, interference, and scaling parameters. It also includes the possibility of defining an estimate of the average roughness of the sample to produce realistic coherent-noise affections.  
![Simulation GUI](/img/SimulationGUI.png)  

The reconstruction module allows the spatial-filtering reconstruction of digital holograms and subsequent computation of either amplitude, intensity, or phase. The plugin receives as input either single images or video feeds for real-time processing. This module also implements user-defined fine-tuning parameters, allowing sub-pixel linear phase compensations and digital refocusing of the reconstructed fields.  
![Reconstruction GUI](/img/ReconstructionGUI.png)

### Installation

The installation process is the standard procedure for any ImageJ plugin. Just download either the [JAR](https://drive.google.com/file/d/1ARskoFNgAdFyMoVHhxgctC_6oN80dwRV/view?usp=sharing) or [ZIP](https://drive.google.com/file/d/1-E5lRbGQM7V8hH5Z06D8NGE2bldeSMQ9/view?usp=sharing) file and extract its contents under the `imagej/plugins` folder. It is advisable to use the latest release of [ImageJ](https://imagej.net/downloads).

Once installed, you should be able to access the plugin at `OD > DHM`
![Fiji GUI Access](/img/FijiGUI.png)

### Downloads
You may choose any of the following:
- [Download the latest library JAR](https://drive.google.com/file/d/1ARskoFNgAdFyMoVHhxgctC_6oN80dwRV/view?usp=sharing)
- [Download the ZIP containing both the library JAR and the dependencies libraries](https://drive.google.com/file/d/1-E5lRbGQM7V8hH5Z06D8NGE2bldeSMQ9/view?usp=sharing)
- [Check the code on GitHub and rebuild from source](https://github.com/unal-optodigital/DHM)

### References
Further information about this plugin and its functional modules can be found in the following publications. These are also the preferred way of citing this tool if you are implementing it in your works.
- C. Buitrago-Duque and J. Garcia-Sucerquia, "Realistic Simulation and Real-Time Reconstruction of Digital Holographic Microscopy Experiments in ImageJ," Applied Optics, 61(5), B56-B63 (2022).  
DOI: [10.1364/AO.443137](https://doi.org/10.1364/AO.443137)
- C. Buitrago-Duque and J. Garcia-Sucerquia, "Realistic modeling of digital holographic microscopy," Optical Engineering 59, 1 (2020).  
DOI: [10.1117/1.OE.59.10.102418](https://doi.org/10.1117/1.OE.59.10.102418)

### Contact
- Carlos A. Buitrago-Duque ([cabuitragod@unal.edu.co](mailto:cabuitragod@unal.edu.co))
- Jorge I. Garcia-Sucerquia ([jigarcia@unal.edu.co](mailto:jigarcia@unal.edu.co))

![UNAL](/img/UNAL.png){: width="250" }![ODP](/img/OD.png){: width="220" }
