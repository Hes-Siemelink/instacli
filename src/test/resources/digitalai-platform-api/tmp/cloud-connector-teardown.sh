#!/bin/bash

#
# This script tears down the cloud connector execution environment
# - Brings down the agent manager
# - Delete existing "digitalai-agent-cluster"
# - Delete "digitalai-agents-net" network (if not deleted when cluster is deleted)
# - Removes runtime artifacts (.kubeconfig, agent deployment files, agent manager config updates)

docker-compose down

k3d cluster delete --all

docker network rm digitalai-agents-net

rm -rf .digitalai/{.kubeconfig,agent-deployments,agent-manager-updates}
