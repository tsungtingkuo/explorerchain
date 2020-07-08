##################### COPYRIGHT NOTICE - BSD License #########################
#       Copyright (c) 2018, Regents of the University of California and
#       the BECKON Project
#       All rights reserved.
#
#       Redistribution and use in source and binary forms, with or without
#       modification, are permitted provided that the following conditions are
#       met:
#
#        * Redistributions of source code must retain the above copyright
#       notice, this list of conditions and the following disclaimer.
#        * Redistributions in binary form must reproduce the above copyright
#       notice, this list of conditions and the following disclaimer in the
#       documentation and/or other materials provided with the distribution.
#
#       THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
#       IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
#       TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
#       PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
#       OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
#       EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
#       PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
#       PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
#       LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
#       NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
#       SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
##############################################################################

# Written by Tsung-Ting Kuo

rm -rf ~/.multichain/

mkdir lib
mkdir temp

cd lib
rm -rf *

wget https://storage.googleapis.com/google-code-archive-downloads/v2/code.google.com/json-simple/json-simple-1.1.1.jar

wget http://www.csie.ntu.edu.tw/~d97944007/utility/utility.jar

wget http://archive.apache.org/dist/commons/math/binaries/commons-math-1.2.zip
unzip commons-math-1.2.zip
cp -f commons-math-1.2/commons-math-1.2.jar .
rm commons-math-1.2.zip

wget https://www.multichain.com/download/multichain-1.0-alpha-27.tar.gz
tar -xvzf multichain-1.0-alpha-27.tar.gz
cp multichain-1.0-alpha-27/multichain* /usr/local/bin
rm multichain-1.0-alpha-27.tar.gz

cd ..

