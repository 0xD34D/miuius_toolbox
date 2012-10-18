#!/bin/bash
########################################################################
# Author : Clark Scheff                                                #
# Written: October 18, 2012                                            #
# Purpose: Used to generate the long class names for the toolbox       #
#          helper classes Craftsman and SnapOn which will cause an     #
#          exception when attempting to decompile them using baksmali  #
#          and prevent the classes from decompiling.                   #
#                                                                      #
# Parameters:                                                          #
#          $1 MIUIToolbox.apk including full path                      #
#          $2 Output location (full directory path)                    #
########################################################################

APK=$1
OUT_DIR=$2
RANDOM_EXTENSION=`tr -dc "[:alpha:]" < /dev/urandom | head -c 256`

usage() {
	echo "usage: $0 <Toolbox apk> <output directory>"
	exit 1
}

if [ $# -lt 2 ] ; then
	usage
fi

echo "Decompiling apk..."
baksmali $APK -o tmp
echo "Adding kang-protection"
for f in tmp/us/miui/*.smali
do
    sed -i s/Toolbox\$Craftsman/Toolbox\$Craftsman_${RANDOM_EXTENSION}/g $f
    sed -i s/Toolbox\$SnapOn/Toolbox\$SnapOn_${RANDOM_EXTENSION}/g $f
done
echo "Copying kang-protected classes to $OUT_DIR"
cp tmp/us/miui/*.smali $OUT_DIR
rm -rf tmp
