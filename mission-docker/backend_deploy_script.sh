#!/bin/sh

if [ ! -d ./secrets ]; then
    echo "directory ./secrets not found!"
    exit
fi

mkdir ./backend
curl https://raw.githubusercontent.com/KYamshanov/Mission-backend/develop/mission-docker/docker-stack.yml --output ./backend/docker-stack.yml
mkdir ./backend/secrets
cp -r ./secrets ./backend/
cd ./backend
docker stack deploy -c ./docker-stack.yml backend
cd ..
rm -r ./backend