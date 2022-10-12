#!/bin/bash

# Directories
BASE_PATH=$HOME/.config/platea
CONFIGS_PATH=$BASE_PATH/configs
TMP_PATH=$BASE_PATH/tmp
RC_PATH=$HOME/.platearc

# Project structure
mkdir -p "$BASE_PATH" "$CONFIGS_PATH"
cp -p "$PWD"/setup/init.sh "$BASE_PATH"/
cp -p "$PWD"/setup/schema.sql "$BASE_PATH"/

sh "$BASE_PATH"/init.sh

# Create database
sudo rm -rf "$BASE_PATH"/.dbdata
cd "$BASE_PATH" || exit

docker rm -f plateadb
docker run \
    --name plateadb \
    --env-file "$BASE_PATH"/.env \
    -P -p 5432:5432 \
    -v "$BASE_PATH"/.dbdata:/var/lib/postgresql/data:Z \
    -v "$BASE_PATH"/schema.sql:/docker-entrypoint-initdb.d/schema.sql:Z \
    -d postgres