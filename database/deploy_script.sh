#!/bin/sh

if [ ! -d ./assets ]; then
    echo "directory ./assets not found!"
    exit
fi

if [ ! -f postgres_db.txt ]; then
    echo "File postgres_db.txt not found!"
    exit
fi

if [ ! -f postgres_user.txt ]; then
    echo "File postgres_user.txt not found!"
    exit
fi

if [ ! -f postgres_password.txt ]; then
    echo "File postgres_password.txt not found!"
    exit
fi

assets="./assets"
mkdir ./deploy
curl https://raw.githubusercontent.com/KYamshanov/Mission-backend/develop/database/docker-compose.yml --output ./deploy/docker-compose.yml
docker compose up --abort-on-container-exit --detach
rm -r ./deploy