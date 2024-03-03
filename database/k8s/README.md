Configuration use k8s kustomization

Useful commands:
`kubectl port-forward deployment/pg-d 5432:5432`
`kubectl delete -f ./k8s/database_deploy.yaml`
`kubectl expose deployment pg-d --port=5432 --type=NodePort`
