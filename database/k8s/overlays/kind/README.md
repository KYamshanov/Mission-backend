# Quick guide
To set up k8s declarative yaml files, use `setup.sh` script.
For launch at windows use git bash application

> sh setup.sh

create cluster with config
> kind create cluster --config private-cluster-config.yaml

run database use k8s kustomization from this folder (kubeadm)

> kubectl apply -k ./


# Information

_/tmp_ folder contains templates yaml file for create host specific patches file (see _kustomization.yaml_, _patches_ part)