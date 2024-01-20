#!/bin/sh

if [ ! -d ./secrets ]; then
    echo "directory ./secrets not found!"
    exit
fi

mkdir ./backend
curl https://raw.githubusercontent.com/KYamshanov/Mission-backend/develop/mission-docker/docker-compose.yml --output ./backend/docker-compose.yml
mkdir ./backend/secrets
cp -r ./secrets ./backend/
cd ./backend
docker compose up --detach
cd ..
rm -r ./backend