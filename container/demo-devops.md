## Demo DevOps


1. S2I, deploy code from github

- new project 'project3', developer perspective
- add from git, oc new-app dotnet:3.1~https://github.com/redhat-developer/s2i-dotnetcore-ex#dotnetcore-3.1 --context-dir app


2. Pipeline with Jenkins

- https://github.com/chatapazar/demo-1/blob/master/docs/JENKINS.md
- git clone https://github.com/chatapazar/demo-1.git

```
cd bin
#Create projects for dev,stage,uat and production
./setup_projects.sh

# sometime need remove --as-deployment-config=true 3 point in setup_ci_cd_tools.sh
#Create project ci-cd, jenkins, nexus and sonarqube
./setup_ci_cd_tools.sh

#Create Jenkins slave with Maven 3.6 and Skopeo
./setup_maven36_slave_no_podman.sh

oc apply -f manifests/backend-build-pipeline.yaml -n ci-cd

```

start build backend-build-pipeline in openshift dev console, show jenkins, show blue ocean
