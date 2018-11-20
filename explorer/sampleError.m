% Sample Error: Calculates specified error measure for supplied observations
% by Will Dwinnell
%
% Last modified by Xiaoqian Jiang: Jan-17-2012
%
% Error = SampleError(Predicted,Actual,ErrorType)
%
% Error      = Calculated error measure
% Predicted  = Predicted values (column vector)
% Actual     = Target values (column vector)
% ErrorType:
%   'L-1'
%   'L-2'
%   'L-4'
%   'L-16'
%   'L-Infinity'
%   'RMS'
%   'AUC' (requires tiedrank() from Statistics Toolbox)
%   'Bias'
%   'Conditional Entropy'
%   'Cross-Entropy' (assumes 0/1 actuals)
%   'F-Measure'
%   'Informational Loss'
%   'MAPE'
%   'Median Squared Error'
%   'Worst 10%'
%   'Worst 20%'

function Error = sampleError(Predicted,Actual,ErrorType)

    if numel(Predicted) == 1, Predicted = Predicted * ones(size(Actual)); end;

    Error = [];

    if iscell(ErrorType)
        for i = 1:length(ErrorType)
            Error.(ErrorType{i}) = mySampleError(Predicted,Actual,ErrorType{i});
        end
    else
        Error = mySampleError(Predicted,Actual,ErrorType);
    end            


function Error = mySampleError(Predicted,Actual,ErrorType)

% Make sure we deal with a column vector for predictions/truth
Predicted = reshape(Predicted, [numel(Predicted) 1]);
Actual = reshape(Actual, [numel(Actual) 1]);

switch upper(ErrorType)
    case {'L-1', 'L1', 'LAD', 'LAE', 'MAE', 'ABSOLUTE'}
        Error = mean(abs(Predicted - Actual));
 
    case {'L-2', 'L2', 'MSE', 'LSE'}
        Error = mean((Predicted - Actual) .^ 2);

    case {'L-4', 'L4'}
        Error = mean((Predicted - Actual) .^ 4);
 
    case {'L-16', 'L16'}
        Error = mean((Predicted - Actual) .^ 16);

    case {'L-INFINITY', 'LINFINITY', 'MAXIMUM', 'CITYBLOCK', ...
            'MANHATTAN', 'TAXICAB', 'CHEBYSHEV', 'MINIMAX'}
        Error = max(abs(Predicted - Actual));

    case {'RMS', 'RMSE'}
        Error = sqrt(mean((Predicted - Actual) .^ 2));

    case {'AUC', 'AUROC'}
        
        Actual = Actual - min(Actual);
        
%        Error = myauc(Predicted,Actual);
        
        % Count observations by class
        nTarget     = sum(double(Actual == 1)) + 1e-200;
        nBackground = sum(double(Actual == 0)) + 1e-200;

        % Rank data
        R = tiedrank(Predicted);  % 'tiedrank' from Statistics Toolbox

        % Calculate AUC
        Error = (sum(R(Actual == 1)) - (nTarget^2 + nTarget)/2) / (nTarget * nBackground);

    case {'AUPRC'}
        
        Actual = Actual - min(Actual); % Now Actual = { 0, 1 }

        % Sort the true scores in order of predictions
        [v,i] = sort(Predicted, 'descend');
        
%         if max(i) > numel(Actual) || min(size(Predicted,1), size(Predicted,2)) ~= 1 || min(i) ~= round(min(i))
%             disp(Predicted)
%             disp(Actual)
%         end
        
        Actual = Actual(i); % Sort true labels in predicted order
        %Actual = sort(Actual); % Worst possible case is when all zeros are at the front
        
        precisionAtK = cumsum(Actual) ./ [1:length(Actual)]';
        relevance = Actual;
        Error = sum(precisionAtK .* relevance)/sum(Actual);
        
    case {'RECALL'}
        
        Actual = Actual - min(Actual);
        Error = sum(Predicted > 0.5)/sum(Actual); % Default threshold is 0.5
        
    case {'PRECATK'}
        
        Actual = Actual - min(Actual); % Now Actual = { 0, 1 }
        
        % Sort the true scores in order of predictions
        [v,i] = sort(Predicted, 'descend');
        
        if max(i) > numel(Actual) || min(size(Predicted,1), size(Predicted,2)) ~= 1 || min(i) ~= round(min(i))
            disp(Predicted)
            disp(Actual)
        end
        
        Actual = Actual(i); % Sort true labels in predicted order        
        Error = cumsum(Actual) ./ [1:length(Actual)]';
        
        
    case {'BIAS'}
        Error = mean(Predicted - Actual);

    case {'CONDITIONAL ENTROPY', 'RESIDUAL ENTROPY'}
        Error = ConditionalEntropy(Actual,Predicted);

    % Note: errors of 1.0 blow up        
    case {'CROSS-ENTROPY', 'CROSSENTROPY', 'INFORMATIONALLOSS', 'INFORMATIONAL LOSS', 'MXE'}
        Error = mean(-log2([Predicted(Actual == 1); 1 - Predicted(Actual == 0)]));
        
    case {'FMEASURE', 'F MEASURE'}
        TwoTP = 2 * sum(double( (Predicted == 1) & (Actual == 1) ));
        FP = sum(double( (Predicted == 1) & (Actual == 0) ));
        FN = sum(double( (Predicted == 0) & (Actual == 1) ));
        Error = TwoTP / (TwoTP + FP + FN);
        clear TwoTP FP FN;

    % Watch out for actuals equal to zero!
    case {'MAPE', 'RAE', 'RELATIVE'}
        Error = mean(abs((Predicted - Actual) ./ Actual));
        
    case {'MEDIAN SQUARED ERROR', 'MEDIAN SQUARE ERROR'}
        Error = median((Predicted - Actual) .^ 2);

    case {'WORST 10%'}
        [PredictedSorted I] = sort(Predicted);
        Error = sum(Actual(I(round(0.9 * length(Predicted)):end))) / sum(Actual);

    case {'WORST 20%'}
        [PredictedSorted I] = sort(Predicted);
        Error = sum(Actual(I(round(0.8 * length(Predicted)):end))) / sum(Actual);
end


% EOF

function A = myauc(t,y)

    ntp = size(y,1);

    % sort by classeifier output

    [y,idx] = sort(y, 'descend');
    t       = t(idx) > 0;

    % generate ROC

    P     = sum(t);
    N     = ntp - P;
    fp    = zeros(ntp+2,1);
    tp    = zeros(ntp+2,1);
    FP    = 0;
    TP    = 0;
    n     = 1;
    yprev = -realmax;

    for i=1:ntp
       if y(i) ~= yprev
          tp(n) = TP/P;
          fp(n) = FP/N; 
          yprev = y(i);
          n     = n + 1;
       end

       if t(i) == 1
          TP = TP + 1;
       else
          FP = FP + 1;
       end
    end

    tp(n) = 1;
    fp(n) = 1;
    fp    = fp(1:n);
    tp    = tp(1:n);

    n = size(tp, 1);
    A = sum((fp(2:n) - fp(1:n-1)).*(tp(2:n)+tp(1:n-1)))/2;    

    % bye bye...
