function saveResult(res_ep_LR, resultFileName_train, resultFileName_test)
fileID = fopen(resultFileName_train,'w');
fprintf(fileID,'%f', 1.0 - res_ep_LR.train.AUC);
fseek(fileID, 0, 'eof');
fclose(fileID);
fclose('all');

fileID = fopen(resultFileName_test,'w');
fprintf(fileID,'%f', 1.0 - res_ep_LR.test.AUC);
fseek(fileID, 0, 'eof');
fclose(fileID);
fclose('all');
end
