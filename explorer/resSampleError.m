function res = resSampleError(PYTE_LR,test_y, model)
    res.AUC = sampleError(PYTE_LR,test_y,'AUC');
    res.AUPRC = sampleError(PYTE_LR,test_y,'AUPRC');
    res.MSE = sampleError(PYTE_LR,test_y,'L-2');
    res.model = model;
end