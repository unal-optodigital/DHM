# DHM in ImageJ
"DHM" is a plugin developed to work on the well-known software for image processing [ImageJ](https://imagej.nih.gov/ij/index.html) or any of its derivate platforms like [Fiji](https://fiji.sc/) or [Micro-Manager](https://micro-manager.org/). This plugin allows both the realistic simulation and real-time reconstruction of digital holograms.

The simulation module implements a numerical telecentric image-plane DHM recording architecture with user-configurable imaging, interference, and scaling parameters. It also includes the possibility of defining an estimate of the average roughness of the sample to produce realistic coherent-noise affections. The reconstruction module allows the spatial-filtering reconstruction of digital holograms and subsequent computation of either amplitude, intensity, or phase. The plugin receives as input either single images or video feeds for real-time processing. This module also implements user-defined fine-tuning parameters, allowing sub-pixel linear phase compensations and digital refocusing of the reconstructed fields.

Detailed and updated information about this project can be found in the [project page](http://unal-optodigital.github.io/DHM/).

## Downloads
The installation process is the standard procedure for any ImageJ plugin. Just download either the [JAR](https://drive.google.com/file/d/1ARskoFNgAdFyMoVHhxgctC_6oN80dwRV/view?usp=sharing) or [ZIP](https://drive.google.com/file/d/1-E5lRbGQM7V8hH5Z06D8NGE2bldeSMQ9/view?usp=sharing) file and extract its contents under the `imagej/plugins` folder. Once installed, you should be able to access the plugin at `OD > DHM`

## Reference
Further information about this plugin and its functional modules can be found in the following publications. These are also the preferred way of citing this tool if you are implementing it in your own works.
- C. Buitrago-Duque and J. Garcia-Sucerquia, "Realistic Simulation and Real-Time Reconstruction of Digital Holographic Microscopy Experiments in ImageJ," Applied Optics, 61(5), B56-B63 (2022).  
DOI: [10.1364/AO.443137](https://doi.org/10.1364/AO.443137)
- C. Buitrago-Duque and J. Garcia-Sucerquia, "Realistic modeling of digital holographic microscopy," Optical Engineering 59, 1 (2020).  
DOI: [10.1117/1.OE.59.10.102418](https://doi.org/10.1117/1.OE.59.10.102418)

## Credits
DHM uses [JDiffraction](https://unal-optodigital.github.io/JDiffraction/) numerical propagation libraries for the digital refocusing and [JTransforms](https://sites.google.com/site/piotrwendykier/software/jtransforms) FFT routines.

## Contact
- Carlos A. Buitrago-Duque ([cabuitragod@unal.edu.co](mailto:cabuitragod@unal.edu.co))
- Jorge I. Garcia-Sucerquia ([jigarcia@unal.edu.co](mailto:jigarcia@unal.edu.co))