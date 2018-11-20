function saveModelMean(ep_LR, modelMeanFileName)
    fileID = fopen(modelMeanFileName,'w');
    fprintf(fileID,'%f\n', ep_LR.mw);
    fseek(fileID, 0, 'eof');
    fclose(fileID);
    fclose('all');
end
