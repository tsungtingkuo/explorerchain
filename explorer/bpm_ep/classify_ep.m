function [Y,z] = classify_ep(obj, X)
X = [ones(size(X, 1), 1), X];
z_tmp = X*obj.mw;
Y = sign(z_tmp);
v = zeros(size(X, 1), 1);
for i = 1: size(X, 1)
    v(i) = X(i, :) * obj.vw * X(i,:)';
end
z = 1./(1 + exp(- (1 + pi * v / 8).^0.5 .* z_tmp));

% z = normcdf(z);
% z = 1./(1 + exp(-z_tmp));