# kubernetes installation guide 

For network used Calico

> sudo apt-get update
> sudo apt-get install -y apt-transport-https ca-certificates curl
> curl -fsSL https://pkgs.k8s.io/core:/stable:/v1.29/deb/Release.key | sudo gpg --dearmor -o /etc/apt/keyrings/kubernetes-apt-keyring.gpg
> ls  /etc/apt/keyrings
> echo 'deb [signed-by=/etc/apt/keyrings/kubernetes-apt-keyring.gpg] https://pkgs.k8s.io/core:/stable:/v1.29/deb/ /' | sudo tee /etc/apt/sources.list.d/kubernetes.list
> sudo apt-get update
> sudo apt-get install -y kubectl
> kubectl cluster-info dump
> kubectl cluster-info
> sudo apt-get update
> sudo apt-get install -y kubelet kubeadm kubectl
> sudo apt-mark hold kubelet kubeadm kubectl
> swapon --show
> sudo swapoff -a
> sudo sed -i -e '/swap/d' /etc/fstab

see https://github.com/Mirantis/cri-dockerd
> sudo dpkg -i cri-dockerd_0.3.10.3-0.ubuntu-jammy_amd64.deb
> sudo kubeadm init --pod-network-cidr=192.168.0.0/16 --cri-socket="unix:///var/run/cri-dockerd.sock"

from output
> mkdir -p $HOME/.kube
> sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
> sudo chown $(id -u):$(id -g) $HOME/.kube/config
> kubectl create -f https://raw.githubusercontent.com/projectcalico/calico/v3.27.2/manifests/tigera-operator.yaml
> kubectl create -f https://raw.githubusercontent.com/projectcalico/calico/v3.27.2/manifests/custom-resources.yaml

> watch kubectl get pods -n calico-system
> !wait till containers are running!

> kubectl taint nodes --all node-role.kubernetes.io/control-plane-
> kubectl taint nodes --all node-role.kubernetes.io/master-
> kubectl get nodes -o wide
> kubectl taint nodes --all node-role.kubernetes.io/control-plane-
> sudo kubectl taint nodes --all node-role.kubernetes.io/control-plane-
> kubectl taint nodes --all node-role.kubernetes.io/control-plane-
> kubectl label nodes --all node.kubernetes.io/exclude-from-external-load-balancers-

> kubectl get nodes -o wide
