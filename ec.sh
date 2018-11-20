
# $1 = dataset name (e.g., edin, ca)
# $2 = test site (e.g., 1, 2)
# $3 = mode (i.e., test, init, new)

java -Xms2048m -Xmx8192m -cp .:bin:lib/* ExplorerChain $1 $2 $3

