function ep_LR = loadModel(modelMeanFileName, modelVarianceFileName)
    ep_LR.mw = importdata(modelMeanFileName);
    ep_LR.vw = importdata(modelVarianceFileName);
end