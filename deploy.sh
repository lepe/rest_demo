#!/bin/bash
#
# This script will prepare the files and synchronize them
# to another server for deployment.
#
REMOTE=$1
PORT=$2
if [[ ${REMOTE} == "" ]]; then
	echo "Target is required:";
	echo "$0 user@remote.host:project/somedir/ [ssh port]";
	exit;
fi

if [[ ${PORT} == "" ]]; then
    PORT=22
fi

if [[ ! -d deploy/ ]]; then
    echo "[deploy] directory created";
    mkdir deploy;
fi

rm -rf deploy/*.jar
cp build/libs/*.jar deploy/
rsync -a --delete resources/ deploy/resources/
cp config.properties deploy/
cp run deploy/
cp *.sql deploy/

echo "Syncing..."
rsync -a -e "ssh -p$PORT" --info=progress2 deploy/ $REMOTE
