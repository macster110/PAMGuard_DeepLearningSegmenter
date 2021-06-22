
# PAMGuard's deep learning module

## Overview

PAMGuard's deep learning module allows users to deploy a large variety of deep learning models natively in PAMGuard. It is core module, fully integrated into PAMGuard's display and data management system and can be used in real time or for post processing data. It cna therfore be used as a classifier for almost anything and can integrate into multiple types of acoustic analysis workflows, for example post analysis analysis of recorder data or used as part of real time localisation workflow. 

## How it works

The deep learning module accepts raw data from different types of data sources, e.g. from the Sound Acquisition module, clicks and clips. It segments data into equal sized chunks with a specified overlap. Each chunk is passed through a set of transforms which convert the data into a format which is accepted by the specified deep learning model. These transforms are either manually set up by the user or, if a specific type of framework has been used to train a deep leanring model, then can be automatically set up by PAMGuard. Currently there are three implemented frameworks

<p align="center">
  <img width="900" height="380" src = "resources/deep_learning_module_process.png">
</p>

_A diagram of how the deep learning module works in PAMGuard. An input waveform is segmented into chunks. A series of transforms are applied to each chunk creating the input for the deep learning model. The transformed chunks are sent to the model. The results from the model are saved and can be viewed in real time (e.g. mitigation) or in post processing (e.g. data from SoundTraps)._

### Generic Model
A generic model allows a user to load any model compatible with the djl (PyTorch (JIT), Tenserflow, ONXX)library and then manually set up a series of transforms using PAMGuard's transform library. It is recomended that users use an existing framework instead of a generic model as these models will automatically generate the required transforms. 

### AnimalSpot
[ANIMAL-SPOT](https://github.com/ChristianBergler/ANIMAL-SPOT) is a deep learning based framework which was initially designed for [killer whale sound detection]((https://github.com/ChristianBergler/ORCA-SPOT)) in noise heavy underwater recordings (see [Bergler et al. 2019](https://www.nature.com/articles/s41598-019-47335-w). It has now been expanded to a be species independent framework for training acoustic deep learning models using pytorch and python. Imported AnimalSpot model will automatically set up their own data transforms and output classes. 

### Ketos
[Ketos](https://meridian.cs.dal.ca/2015/04/12/ketos/) is an acoustic deep learning framework based on Tensorflow and developed by Meridian. It has excellent resources and tutorials and pytorch libraries can be installed easily via pip. Imported Ketos model will automatically set up their own data transforms and output classes. 

## Creating an instance of the module
The module can be added from the _File>  Add modules > Classifier > Raw deep learning classifier_ menu or by right clicking in the data model. More than one instance of the module can be added if multiple deep leanring models are required. 

## Module settings

The module settings are opened by selecting the  _Settings > Raw deep learning classifier_ menu. The d

<p align="center">
  <img width="700" height="630" src = "resources/deep_leanring_module_help.png">
</p>


### Raw Data source

The deep learning module accepts any raw data source i.e. any data source that contains raw waveform data.

If the data is continous, e.g. from the Sound Acquisiiton module then deep leanring data units are saved to PAMGuard's datamanagement system if they pass a user defined prediciton threshold. The raw waveform data for segments whcih pass prediciton threshold is also saved. 

If the data source has already produced data units, e.g. clicks or clips, then the deep learning results are saved as annotation attached the data unit. The data is segmented in exactly the same way as continous data and thus, depending on the lenght of raw data, there can be more than one prediciton per dat unit. 

### Segmentation

The segmentation section defines how the raw data is segmented. The 

### Deep learning model 

The deep leanring model section is used to select the deep leanring model. The user must select the framework the mdoel is from. 

###
