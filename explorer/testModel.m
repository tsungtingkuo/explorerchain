function res_ep_LR = testModel(ep_LR, train, test)
    [~, PYTR_ep_LR] = classify_ep(ep_LR, train.x);
    res_ep_LR.train = resSampleError(PYTR_ep_LR, train.y, ep_LR); 
    [~, PYTE_ep_LR] = classify_ep(ep_LR, test.x);
    res_ep_LR.test = resSampleError(PYTE_ep_LR, test.y, ep_LR);
end