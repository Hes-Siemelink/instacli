#!/bin/bash

#
# This script prepares the cloud connector execution environment
# - Download k3d (if it doesn't exist)
# - Create directory structure in local host file system (".digitalai" folder)
# - Login to docker image repository (if not using local)
# - Pull digitalai agent image (if not using local)
# - Check whether cloud connector cluster exists
#   - if exists:
#     - start the cluster in case it is stopped
#   - else:
#     - Setup docker network for dai-agent - cluster communication
#     - Create and configure (k3d_config.yml) "digitalai-agent-cluster"
#     - Setup kube config for digitalai agent
# - Run digitalai agent manager container


K3D_VERSION_REQUIRED=v5.4.6

# ensureK3dInstalled ensures the required k3d version is installed and available via command line
#                    returns 0 if k3d is available, 1 otherwise
ensureK3dInstalled() {
  local INSTALL_K3D=0
  k3d version
  exit_code=$?
  if [ $exit_code -ne 0 ]; then
    INSTALL_K3D=1
  else
    local K3D_VERSION_INSTALLED=$(k3d version | grep 'k3d version' | cut -d " " -f3)
    if [ "$K3D_VERSION_INSTALLED" != "$K3D_VERSION_REQUIRED" ]; then
      INSTALL_K3D=1
    fi
  fi
  if [ $INSTALL_K3D -eq 1 ]; then
    echo "k3d ${K3D_VERSION_REQUIRED} needs to be installed. Setting up..."
    curl -s https://raw.githubusercontent.com/rancher/k3d/main/install.sh | TAG=$K3D_VERSION bash
    exit_code=$?
    if [ $exit_code -ne 0 ]; then
      return 1
    else
      k3d version
      return 0
    fi
  else
    return 0
  fi
}

if ! ensureK3dInstalled; then
  echo "k3d ${K3D_VERSION_REQUIRED} is not available. Exiting..."
  exit 1
fi

# create directory structure in host file system (shared by docker containers)
# makes use of bash's brace expansion support
mkdir -p .digitalai/{.kubeconfig,agent-deployments,logs}

# Set env variables (for platform image repository connection info)
set -o allexport; source .dai-agent-env; set +o allexport

IMAGE_REGISTRY_HOST="${DAI_IMAGE_REPOSITORY_URL}"
if [[ -z $IMAGE_REGISTRY_HOST ]]; then
  echo "Working with local docker registry"
else
  # Register platform image repository with docker (to pull dai-agent image)
  echo $DAI_IMAGE_REPOSITORY_PASSWORD | docker login -u $DAI_IMAGE_REPOSITORY_USERNAME --password-stdin $IMAGE_REGISTRY_HOST
  # Pull dai-agent image
  docker pull $IMAGE_REGISTRY_HOST/digitalai-agent:latest
  exit_code=$?
  if [ $exit_code -ne 0 ]; then
    echo "Failed to pull 'digitalai-agent' image from $IMAGE_REGISTRY_HOST!"
    exit $exit_code
  fi
  docker tag $IMAGE_REGISTRY_HOST/digitalai-agent:latest digitalai-agent:latest
fi


# Check if there is already cloud connector cluster
CLUSTER_NAME=digitalai-agent-cluster
k3d cluster list | awk '{print $1}' | grep $CLUSTER_NAME
CLUSTER_DOES_NOT_EXIST=$?

if [ $CLUSTER_DOES_NOT_EXIST -eq 1 ]; then
  # create cluster for running agents in
  k3d cluster create --config k3d_config.yml

  # setup kubeconfig
  KUBE_CONFIG=.digitalai/.kubeconfig/$CLUSTER_NAME-config.yml
  k3d kubeconfig get --all > $KUBE_CONFIG
  sed -i.bak -E 's~0\.0\.0\.0:[0-9]+~k3d-digitalai-agent-cluster-server-0:6443~g' $KUBE_CONFIG && rm $KUBE_CONFIG.bak
else
  # show a message, and try to start the cluster in case it is stopped
  echo "$CLUSTER_NAME cluster already exists. Run cloud-connector-teardown.sh to start from scrath."
  k3d cluster start $CLUSTER_NAME
fi

DIR_ENV_UPDATES=.digitalai/agent-manager-updates
FILE_ENV=.dai-agent-env
FILE_ENV_NEW=$DIR_ENV_UPDATES/$FILE_ENV.new
if [ -f "$FILE_ENV_NEW" ]; then
  FILE_ENV_ARCHIVE=$FILE_ENV-$(date +%Y%m%d_%H%M%S)

  # prepare current env file for archiving
  cp -a "$FILE_ENV" "$FILE_ENV_ARCHIVE"

  # copy updated config file as current
  cp -a "$FILE_ENV_NEW" "$FILE_ENV"

  # delete updated env file
  rm "$FILE_ENV_NEW"

  # archive old env file
  mv "$FILE_ENV_ARCHIVE" "$DIR_ENV_UPDATES"

  echo "Agent manager configuration is updated!"
fi

# run digitalai agent in detached mode
docker-compose up -d

# follow the logs
# related issue: https://github.com/docker/compose/issues/8977
docker-compose logs -f

# stop the agent manager container
# q: should we keep the container running if user CTRL+C from the logs
docker-compose stop
