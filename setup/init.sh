#!/bin/bash

# This script is ran each time Platea is called.
# It sets up a dotenv file.

# Directories
BASE_PATH=$HOME/.config/platea
CONFIGS_PATH=$BASE_PATH/configs
TMP_PATH=$BASE_PATH/tmp
RC_PATH=$HOME/.platearc

# Build dotenv if does not exist
if [ ! -f "$BASE_PATH"/.env ]; then

tee "$BASE_PATH"/.env <<EOF > /dev/null
BASE_PATH=$BASE_PATH
CONFIGS_PATH=$CONFIGS_PATH
TMP_PATH=$TMP_PATH
RC_PATH=$RC_PATH

REMOTE_URL=https://gitlab.com/aloxyz/platea-configs
DOCKER_SOCKET=unix:/var/run/docker.sock
DOCKER_URL=localhost:2375

POSTGRES_PASSWORD=platea
POSTGRES_USER=platea
POSTGRES_DB=platea
POSTGRES_URL=jdbc:postgresql:platea
EOF

fi
