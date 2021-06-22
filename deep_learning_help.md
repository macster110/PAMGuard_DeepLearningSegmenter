
# PAMGuard's deep learning module

## Overview

PAMGuard's deep learning module allows users to deploy a large variety of deep learning models natively in PAMGuard. It is core module, fully integrated into PAMGuard's display and data management system and can be used in real time or for post processing data. It cna therfore be used as a classifier for almost anything and can integrate into multiple types of acoustic analysis workflows, for example post analysis analysis of recorder data or used as part of real time localisation workflow. 

## How it works

The deep learning module accepts raw data from different types of data sources, e.g. from the Sound Acquisition module, clicks and clips. It segments data into equal sized chunks with a specified overlap. Each chunk is passed through a set of transforms which convert the data into a format which is accepted by the specified deep learning model. These transforms are either manually set up by the user or, if a specific type of framework has been used to train a deep leanring model, then can be automatically set up by PAMGuard. Currently there are three implemented frameworks

<p align="center">
  <img width="900" height="380" src = "resources/deep_learning_module_process.png">
</p>
_A diagram of how the deep learning module works in PAMGuard. An input waveform is segmented into chunks. A series of transforms are applied to each chunk creating the input for the deep learning model. The transformed chunks are sent to the model. The results from the model are saved and can be viewed in real time (e.g. mitigation) or in post processing (e.g. data from SoundTraps). _

### Generic framework
This framework allows users to load any Keras or Pytorch model. The user then has to manually set up the required transforms and specify output classes. The frameowrk has the ability to import and export transforms as a json file, making it easier for other users to set up the module. This framework is only recommneded for experienced users. 

### AnimalSpot 



## Creating an instance of the module
The module can be added from the _File>  Add modules > Classifier > Raw deep learninn classiifer_ menu or by right clicking in the data model. More than one instance of the module can be added if multiple deep leanring models are required. 

## Module settings

<p align="center">
  <img width="700" height="630" src = "resources/deep_leanring_module_help.png">
</p>

### Data source

The deep leanring module accepts any raw data source i.e. any data source that contains raw wavefo

### Deep learning framework 
