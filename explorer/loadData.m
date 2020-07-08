function data = loadData(dataFileName)
    tmp_data = load(dataFileName);
    data.x = tmp_data(:, 1:(end-1));
    data.y = tmp_data(:, end);  
end
