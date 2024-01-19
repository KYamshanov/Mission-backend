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

mkdir ./database
export assets="../assets"
curl https://raw.githubusercontent.com/KYamshanov/Mission-backend/develop/database/docker-compose.yml --output ./database/docker-compose.yml
cp postgres_db.txt ./database/postgres_db.txt
cp postgres_user.txt ./database/postgres_user.txt
cp postgres_password.txt ./database/postgres_password.txt
cd ./database
docker compose up --detach
cd ..
unset assets
rm -r ./database