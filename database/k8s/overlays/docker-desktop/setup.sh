#script for configuration k8s yaml file which contains host specific variables such as file path to assets

export MSYS_NO_PATHCONV=1
export REALPATH_ASSETS=$(realpath ../../../assets)

envsubst < tmp/pv-database.yaml > private-pv-database.yaml
envsubst < tmp/pv-init-db.yaml > private-pv-init-db.yaml

echo "REALPATH_ASSETS=$REALPATH_ASSETS"
read -p "Press enter to finish"