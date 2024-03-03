# postgresql
By default project use postgresql database. 
Initial files lie in `/assets/init_db` folder
Database data by default lie in `/assets/db_data` folder

# docker compose

Docker-compose для запуска центарлизованной БД

Для подключения к БД : `psql -U postgres -h localhost postgres`

# Kubernetes
Project use k8s and kustomization for declare deployment items (see [k8s readme](k8s/README.md))

### docker-desktop
To configure k8s kustomization for docker-desktop see [k8s docker-desktop readme](k8s/overlays/docker-desktop/README.md)