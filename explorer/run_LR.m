function [model, dev, PYTE_LR] = run_LR(XTR, YTR, XTE)
[~, dev, model] = glmfit(XTR, YTR, 'binomial', 'link', 'logit');
[PYTE_LR, ~, ~] = glmval(model.beta, XTE, 'logit', model);
% YTE_LR = PYTE_LR;
% YTE_LR(PYTE_LR > 0.5) = 1;
% YTE_LR(PYTE_LR <= 0.5) = -1;
end