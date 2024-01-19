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

mkdir ./deploy
export assets="./assets"
curl https://raw.githubusercontent.com/KYamshanov/Mission-backend/develop/database/docker-compose.yml --output ./deploy/docker-compose.yml
docker compose up --detach
unset assets
rm -r ./deploy