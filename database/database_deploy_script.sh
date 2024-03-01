#!/bin/sh

if [ ! -d ./assets ]; then
    echo "directory ./assets not found!"
    exit
fi

if [ ! -f postgres_db ]; then
    echo "File postgres_db.txt not found!"
    exit
fi

if [ ! -f postgres_user ]; then
    echo "File postgres_user.txt not found!"
    exit
fi

if [ ! -f postgres_password ]; then
    echo "File postgres_password.txt not found!"
    exit
fi

mkdir ./database
export assets="../assets"
curl https://raw.githubusercontent.com/KYamshanov/Mission-backend/develop/database/docker-compose.yml --output ./database/docker-compose.yml
cp postgres_db ./database/postgres_db
cp postgres_user ./database/postgres_user
cp postgres_password ./database/postgres_password
cd ./database
docker compose up --detach
cd ..
unset assets
rm -r ./database