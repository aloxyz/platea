#!/bin/sh

git clone https://github.com/lcarnevale/docker-file-server.git src/docker-file-server
cd src/docker-file-server
docker build -t lcarnevale/fileserver .
docker run -d --name fileserver \
    -e PORT=8085 \
    -v /mnt/fileshare:/mnt/fileshare \
    -v /var/log/lcarnevale:/opt/app/log \
    -p 8085:8085 \
    --restart unless-stopped \
    lcarnevale/fileserver