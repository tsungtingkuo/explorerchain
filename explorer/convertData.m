function [train, test] = convertData(dataName, seeds)
    s = RandStream('mt19937ar','Seed',seeds);
    RandStream.setGlobalStream(s);
    tmp_data = load(dataName);
    id = randperm(size(tmp_data, 1));
    tmp_data = tmp_data(id, :);
    data.x = tmp_data(:, 1:(end-1));
    data.y = tmp_data(:, end);
%% get ten cross id    
    indices = crossvalind('Kfold', size(tmp_data, 1), 10);
    train = cell(10, 1);
    test = cell(10, 1);
    for i = 1:10
        test_id = (indices == i); train_id = ~test_id;
        train{i}.x = data.x(train_id, :);
        train{i}.y = data.y(train_id, :);
        test{i}.x = data.x(test_id, :);
        test{i}.y = data.y(test_id, :);
    end
end