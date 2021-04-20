# demo-ocp

part 1 - demo with terminal
- new project
- deploy with yaml
```
oc login
oc new-project project1
oc apply -f manifests/frontend.yaml -n project1
oc get pods
oc get svc
oc get route
FRONTEND_URL=https://$(oc get route frontend -n project1 -o jsonpath='{.spec.host}')
curl -k $FRONTEND_URL/version
```


part 2 - demo with web console
- new project --> project2
- deploy with image --> quay.io/voravitl/frontend-js:v1
- show pod, log, terminal
- show service
- show route


part 3 - other
- scale manual
- show monitoring
- add database


