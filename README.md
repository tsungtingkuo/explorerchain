EXpectation Propagation LOgistic REgRession on permissioned blockCHAIN (ExplorerChain): Decentralized privacy-preserving online healthcare/genomics predictive model learning
------------------------------------------------------------------------

### Citation

Please cite as below:

* *Tsung-Ting Kuo, Rodney A. Gabriel, Krishna R. Cidambi, and Lucila Ohno-Machado, "EXpectation Propagation LOgistic REgRession on permissioned blockCHAIN (ExplorerChain): decentralized online healthcare/genomics predictive model learning." Journal of the American Medical Informatics Association (JAMIA). 2020. doi: [10.1093/jamia/ocaa023](https://doi.org/10.1093/jamia/ocaa023).*

* *Tsung-Ting Kuo, "The Anatomy of a Distributed Predictive Modeling Framework: Online Learning, Blockchain Network, and Consensus Algorithm." Journal of the American Medical Informatics Association Open (JAMIA Open). 2020:  doi: [10.1093/jamiaopen/ooaa017](https://doi.org/10.1093/jamiaopen/ooaa017).*

### Introduction

This is the code for the ExplorerChain, which runs privacy-preserving online predictive modeling algorithm on a permissioned blockchain network.

### Predictive Model

ExplorerChain is based on [EXPLORER](https://kr.mathworks.com/matlabcentral/fileexchange/39653-distributed-logistic-regression-using-expectation-propagation) modeling method.

### Example Data

The two sets of example data (Edin and CA) are derived from the two datasets included in [EXPLORER](https://kr.mathworks.com/matlabcentral/fileexchange/39653-distributed-logistic-regression-using-expectation-propagation).

### Installation

Internet connection is needed for downloading required components.

1. Prerequisites
   * Ubuntu (64-bit 14.04 with superuser privilege)
   * Matlab (R2014a for Linux Command Line Interface with a symbolic link of "matlab", as well as Statistics Toolbox 9.0)
   * Java (1.8 or later)
2. Libraries
   * [JSON.simple v1.1.1](https://code.google.com/archive/p/json-simple/)
   * [Apache Common Math v1.2](https://commons.apache.org/proper/commons-math/)
   * [Tsung-Ting's Java Utilities v1.0.0](http://www.csie.ntu.edu.tw/~d97944007/utility/)
3. Blockchain Platform
   * [MultiChain v1.0 alpha 27](https://www.multichain.com/)
4. Setup
   * After preparing the prerequisites, switch to superuser and run "setup.sh" to download the libraries and blockchain platform.

### Running ExplorerChain on the Edin data

To run ExplorerChain, open 3 terminal windows with superuser privilege and switch to the explorerchain folder.

1. Terminal 1: MultiChain 
   * Run the following commands, and do not close this terminal or stop the MultiChain node:

   ```
   multichain-util create dbmi-chain
   multichaind dbmi-chain
   ```

2. Terminal 2: ExplorerChain Site 1 
   * Run the following command, and then copy the returned address (like 1B…):

   ```
   multichain-cli dbmi-chain getnewaddress
   ```

   * Run the following command by replacing the 1B… part with the copied address:

   ```
   multichain-cli dbmi-chain grant 1B… receive
   ```

   * Edit "conf/config_1.txt", and replace the address in the second row with the copied address

   * Run the following command to test ExplorerChain on Edin data at Site 1:

   ```
   ./ec.sh edin 1 test
   ```

3. Terminal 3: ExplorerChain Site 2

   * Edit "conf/config_2.txt", and replace the address in the second row with the copied address

   * Run the following command to test ExplorerChain on Edin data at Site 2:

   ```
   ./ec.sh edin 2 test
   ```

4. After consensus, the resulting average AUC should be 0.963955.


### Running ExplorerChain on the CA data

Please test abovementioned Edin data steps first.

1. Terminal 1: MultiChain

   * Keep the node running.

2. Terminal 2: ExplorerChain Site 1 

   * Run the following command to test ExplorerChain on CA data at Site 1:

   ```
   ./ec.sh ca 1 test
   ```

3. Terminal 3: ExplorerChain Site 2

   * Run the following command to test ExplorerChain on CA data at Site 2:


   ```
   ./ec.sh ca 2 test
   ```

4. After consensus, the resulting average AUC should be 0.935185.


### Running ExplorerChain to test site leaving and re-joining

Please test abovementioned Edin/CA data steps first.

1. Terminal 1: MultiChain

   * Keep the node running.

2. Terminal 2: ExplorerChain Site 1 

   * Run the following command to run ExplorerChain on Edin data at Site 1 in "daemon" mode:

   ```
   ./ec.sh edin 1 init
   ```

3. Terminal 3: ExplorerChain Site 2

   * Run the following command to run ExplorerChain on Edin data at Site 2 in "daemon" mode:


   ```
   ./ec.sh edin 2 init
   ```

   * After consensus, use Ctrl+C to stop ExplorerChain to simulate the leaving of Site 2.

   * Run the following command to re-join the network:


   ```
   ./ec.sh edin 2 new
   ```

4. The similar process can be tested on CA data as well by replacing the "edin" in all commands to "ca".

### Acknowledgement

This work is partly funded by U.S. National Institutes of Health (OT3OD025462, U54HL108460, K99/R00HG009680, R01HL136835, R01GM118609, and U01EB023685), a UCSD Academic Senate Research Grant (RG084150), and U.S. Department of Veterans Affairs (IIR12-068). The content is solely the responsibility of the authors and does not necessarily represent the official views of the NIH. The funders had no role in study design, data collection and analysis, decision to publish, or preparation of the software.

### Contact

Thank you for using our software. If you have any questions or suggestions, please kindly contact Tsung-Ting Kuo (tskuo@ucsd.edu), UCSD Health Department of Biomedical Informatics, University of California San Diego, La Jolla, USA.

### DOI

[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.1492820.svg)](https://doi.org/10.5281/zenodo.1492820)
