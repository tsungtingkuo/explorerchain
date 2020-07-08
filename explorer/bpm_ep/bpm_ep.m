function obj = bpm_ep(X, Y, type, prior_variance)

if nargin < 3
    type = 'step';
end
if strcmp(type, 'step')
    e = 0;
elseif strcmp(type,'probit') || strcmp(type,'logistic')
    e = 1;
else
    error('do not support this type of likelihood function\n');
end
X = [ones(size(X,1),1) X];
data = X .* repmat(Y, 1, size(X,2));

obj = struct('type', type, 'e', e, 'add_bias', 1, ...    
    'mp', [], 'vp', [], ...
    's', [], 'mw', [], 'vw', [], ...
    'alpha', [], 'bias', 0, 'X', [], 'Y', [], ...
    'state', [], 'restrict', 0, 'stepsize', 1, ...
    'train_err', [], 'loo', [], 'loo_count', [], 'stability', [],...
    'data', data, 'prior_variance', prior_variance);
% clear the state
obj.state = [];
