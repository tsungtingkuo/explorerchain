function res_mean = getAverageResult(res)    
    for i = 1: numel(res)
        res_mean.AUC(i) = res{i}.AUC;
        res_mean.AUPRC(i) = res{i}.AUPRC;
        res_mean.MSE(i) = res{i}.MSE;
        res_mean.time(i) = res{i}.time;        
        res_mean.hl_h(i) = res{i}.hl_h;
        res_mean.hl_Stat(i) = res{i}.hl_Stat;
        res_mean.hl_p(i) = res{i}.hl_p;
        res_mean.beta(i, :) = res{i}.beta;
        res_mean.covb(:, :, i) = res{i}.covb;       
%        res_mean.wh(i, :) = res{i}.wh;
        res_mean.wp(i, :) = res{i}.wp;
        res_mean.wstat(i, :) = res{i}.wstat;        
    end
    
end