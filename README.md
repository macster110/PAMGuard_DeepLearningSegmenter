# PAMGuard_DeepLearningSegmenter
The Deep Learning Segment PAMGuard module acquires incoming chunks of raw sound data and sends to a deep learning model for classification in real time. Results become part of the PAMGuard processing chain and so can be further classified, saved as raw wav clips, localised, annotated etc. 

## Introduction 
[PAMGuard](https://www.pamguard.org/) is a bioacoustics toolbox for the detection, classification and localisation of soniferous species both in real time and post processing sound files. It's primarily focused cetaceans (whales, dolphins, river dolphins, porpoises) and bats however can be used with any vocalising animal. The modular structure of PAMGuard allows users to create processing chains for detection, classification and localisation which is combined with a comprehensive data management and visualisation system. This allows users to analyse and then visualise and navigate through months and years of acoustic recordings. 

So far PAMGuard has mainly used more traditional detection and classification algorithms (e.g. energy detectors) and some machine learning approaches (e.g. whistle classifier, ROCCA), however, has yet to fully integrate deep learning. The powerful data visualisation tools and real time capability of PAMGuard mean it is an ideal platform to integrate deep learning classifiers. Such algorithms greatly enhance automated classification performance an, if combined with PAMGuard, could be integrated into an acoustic analysis workflow with a wide variety of conservation applications, for example, improving real time mitigation and enabling more streamlined analysis of large acousitc datasets. This plugin PAMGuard module provides a framework to integrate deep learning classifiers which analyse continuous raw sound or spectrogram data. The first algorithm integrated is OrcaSpot, designed to detect and classify the call type of Orcas.

## Deep Learning Segmenter
The module is fairly straightforward to use. Go to _ _File ->Add Modules -> Classifiers -> Deep Learning Segmener_ _ . This will add the module to the PAMGuard data model. Once the module has been added to the data model go to  _ _Settings -> Deep Learning Segmenter_ _ to open the module settings. Select the channels, window length, hop size and deep leanring model and you are ready to start analysing data. A PAMGuard settings files which simulates and localises simulated dolphin whistles using the OrcaSpot model is located in the resources folder. 

<center><img src="resources/OrcaSpot_help1.png" width="1024"></center>
An example of OrcaSpot working on some simulated data and explanations of the various GUI components. Here the output from the algorithm is being sent to a beam former which provides a bearing to the detected Orca call. 

## Installing the Plugin
Installing the PAMGuard plugin is straightforward and detailed on the [PAMGuard website](https://www.pamguard.org/66_CreatingExternalPlug-ins.html). The jar file is located in the resources folder (currently not available). 

## Deep Learning Models
### OrcaSpot
[ORCA-SPOT](https://github.com/ChristianBergler/ORCA-SPOT) is a deep learning based algorithm which was initially designed for killer whale sound detection in noise heavy underwater recordings. 

Settings up OrcaSpot to work is not trivial and requires some command line coding. 

You will need to. 

 * Install [Python and Anaconda or similar](https://docs.anaconda.com/anaconda/install/windows/).
 * Install [CUDA installed from Nvidea](https://developer.nvidia.com/cuda-downloads) to run the classifier on a graphics card. 
 * Install [Cuda support for Pytorch](https://pytorch.org).
 * Download the OrcaSpot Python code and classifier. 
 
 To set up the python environment
 * Copy the Orcaspot Segmeneter folder to a location on your computer you ar enot going to change. 
 * Open command prompt or Anaconda prompt if is using Anaconda.
 * Type ```python -m venv C:\Your\Enviroment\Path\Here``` for example ```python -m venv C:\Users\Hauec\Desktop\Segmenter\pytorch\my-venv```. This creates a Folder called my-venv in the PyTorch Folder inside of the Segmenter.
 * Next activate your Virtual environment. Inside of my-venv\Scripts should see a windows batch called activate.bat. cd to it and run it in CMD by typing ```activate.bat```. You'll know that it is active via the (my-venv) precommand in the command windows.
 * Once that is done, run setup_pytorch.bat from the PyTorch folder. It should automatically install Pytorch, PyVision, and all of the required ependencies.

## Development Environment
The best way to develop a PAMGuard external plugin is to download the PAMGuard project [(instruction here for Eclipse)](https://www.pamguard.org/15_SourceCode.html) (use the  UID Java 12 branch) and copy and past this repository in as a package in the main src folder. Then, in PamModel.java around line 753 in the classifiers group add

```Java
		mi = PamModuleInfo.registerControlledUnit("rawDeepLearningClassifer.DLControl", "Deep Learning Segmenter");
		mi.addDependency(new PamDependency(RawDataUnit.class, "Acquisition.AcquisitionControl"));
		mi.setToolTipText("Classifies sections of raw acoustic data based on an imported deep learning classifier");
		mi.setModulesMenuGroup(classifierGroup);
```
Adding a new DeepLearning model requires a new class satisfying the interface ```DLClassifierModel``` in the _ deepLearningClassiifcation _ package. This then needs to be added to an array (```ArrayList<DLClassiferModel> dlModels```) in ```DLControl```.

## TODO and Missing Features
* Viewer mode support is limited. 
* The segmenter may not work properly for hop sizes that less than 1/100 the sample rate - this will be fixed soon.  
* There are no help files. 
* Only OrcaSpot classifier so far implemented . 
* Plug module jar file still needs to be created.  


