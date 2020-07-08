function saveModelVariance(ep_LR, modelVarianceFileName)
    fileID = fopen(modelVarianceFileName,'w');
    for ii = 1:size(ep_LR.vw,1)
        fprintf(fileID,'%f\t',ep_LR.vw(ii,:));
        fprintf(fileID,'\n');
    end
    fseek(fileID, 0, 'eof');
    fclose(fileID);
    fclose('all');
end
