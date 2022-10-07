#!/bin/bash

# Directories
BASE_PATH=$HOME/.config/platea
CONFIGS_PATH=$BASE_PATH/configs
TMP_PATH=$BASE_PATH/tmp
RC_PATH=$HOME/.platearc

# Project structure
mkdir -p "$BASE_PATH" "$CONFIGS_PATH"
cp "$PWD"/init.sh "$BASE_PATH"/
cp "$PWD"/schema.sql "$BASE_PATH"/

# Create database
sudo rm -rf "$BASE_PATH"/.dbdata
cd "$BASE_PATH" || exit

docker rm plateadb
docker run \
    --name plateadb \
    --env-file .env \
    -P -p 5432:5432 \
    -v "$PWD"/.dbdata:/var/lib/postgresql/data:Z \
    -v "$PWD"/schema.sql:/docker-entrypoint-initdb.d/schema.sql:Z \
    -d postgres