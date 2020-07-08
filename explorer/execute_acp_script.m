function [model, dev, V, PYTE_LR, YTE_LR] = execute_acp_script(dataName)
% simulate 1D data (not linearly separable)
% synthetic data
% n = 300;
% XTR = [-3+randn(n,1); randn(n,1); 3+randn(n,1)];  % predictor matrix of the training data
% YTR = [zeros(n,1); ones(n,1); zeros(n,1)];        % class label of the training data
% XTE = [-3+randn(n,1); randn(n,1); 3+randn(n,1)];  % predictor matrix of the test data
% YTE = [zeros(n,1); ones(n,1); zeros(n,1)];        % class label of the training data
%  construct LR model

if(~exist('dataName', 'var'))
    % load ./data/hospital_discharge;
    load ./data/shef_structured_rm;
    % load ./data/edin_structured_rm;
else
    load(dataName)
end
   
XTR = train.x;
YTR = train.y;
XTE = test.x;
YTE = test.y;
[model, dev, V] = buildLRModel(XTR, YTR);
PYTE_LR = PredictLR(XTE, model);

sampleError(PYTE_LR,YTE,'AUC');
YTE_LR = PYTE_LR;
YTE_LR(PYTE_LR > 0.5) = 1;
YTE_LR(PYTE_LR <= 0.5) = -1;
% %  call ACP calibration procedure.
% P_ACP =acp(PTR,YTR,XTE, model);
end

function P_ACP =acp(PTR,YTR,XTE, model)
%ACP adaptively calibrates the probabilities of a logistic regression predictive model.
%   P_ACP = ACP(PTR,YTR,XTE) adaptively calibrates the probabilities of
%   predictive models using pre-calibration estiamtion PTR, response YTR of the
%   training data, and the predictor matrix XTE of the test data. The
%   result P_ACP is a vector of calibrated predictions for XTE.
%   XTE is a matrix with rows corresponding to observations, and columns to
%   predictor variables.  ACP automatically includes a constant term in the
%   model (do not enter a column of ones directly into X).  PTR is a vector
%   of pre-calibration probability estimates. YTR is a vector of response values.
train.yhat = PTR;
train.y= YTR;
test.x = XTE;
% [w,dev,stats] = glmfit(train.x, train.y, 'binomial', 'link', 'logit');
% [train.yhat,train.dylo,train.dyhi] = PredictLR(train.x, model)
[test.yhat,test.dylo,test.dyhi] = PredictLR(test.x, model)
% confidence intervals of test points
test.ublb = [test.yhat - test.dylo, test.yhat+test.dyhi];
% range of predicted probabilities of the training points, step 1 in algorithm 1
r = max(train.yhat)-min(train.yhat);
% estimated standard errors for the parameters w
se = model.se(:);
% estimated covariance matrix for w
cc = model.coeffcorr;
V = (se * se') .* cc;
R = cholcov(V);% Equation (5)
c = ones(size(test.x,1),1);
vxb = sum((R * [c,test.x]').^2,1);
idxSE = sqrt(vxb(:));
idxLower = [c,test.x]*model.beta - 1.96*idxSE*r;
idxUpper = [c,test.x]*model.beta + 1.96*idxSE*r;
% Step 2 in Algorithm 1
lb = invlogit(idxLower);
ub = invlogit(idxUpper);
P_ACP = zeros(size(test.x,1),1);
% Step 3 & 4 in Algorithm 1
for i=1:length(test.yhat)
    in = find(train.yhat>lb(i) & train.yhat <ub(i));
if isempty(in)
        [v in]= min((train.yhat - test.yhat(i)*ones(size(train.yhat))).^2);
   end
    P_ACP(i)=sum(train.y(in))/length(in);
end
end
function v = invlogit(x)
%The Inverse Logit function.
v = exp(x)./ (1+exp(x));
end


function [model, dev, V] = buildLRModel(XTR, YTR)
%The buildLRModel functinon returns a logistic regression model.
%
[b, dev, model] = glmfit(XTR, YTR, 'binomial', 'link', 'logit');
% fprintf('beta = [');
% fprintf('%f,', model.beta);
% fprintf(']\n');

se = model.se(:);
% estimated covariance matrix for w
cc = model.coeffcorr;
V = (se * se') .* cc;
% R = cholcov(V);% Equation (5)
% c = ones(size(test.x,1),1);
% vxb = sum((R * [c,test.x]').^2,1);
% idxSE = sqrt(vxb(:));
% idxLower = [c,test.x]*model.beta - 1.96*idxSE;
% idxUpper = [c,test.x]*model.beta + 1.96*idxSE;

% fprintf('covariance = [\n');
% % fprintf('%f,', V);
% disp(V);
% fprintf(']\n');
% fprintf('bias = %.2f\n',dev);
end

function [yhat dylo dyhi] = PredictLR(x, model)
% Apply the Logistic regression model to make predictions
[yhat dylo dyhi] = glmval(model.beta, x, 'logit', model);
end