# Introduction to Container Technology
<!-- TOC -->

- [Introduction to Container Technology](#introduction-to-container-technology)
  - [Presentation](#presentation)
  - [Setup](#setup)
- [Basic Docker Command](#basic-docker-command)
- [Create Database Container](#create-database-container)
- [Connect postgres container from local development](#connect-postgres-container-from-local-development)
  - [Run More Than 1 Container with Docker Compose](#run-more-than-1-container-with-docker-compose)

<!-- /TOC -->

## Presentation

Presentation ([container.pptx](presentation/container.pptx))

## Setup

- install docker desktop
  - for for mac/windows10 (except home edition) --> https://www.docker.com/products/docker-desktop
  - for windows10 home edition --> https://docs.docker.com/docker-for-windows/install-windows-home/
- Source Code --> folder container

# Basic Docker Command

- start docker desktop
- get docker info
```bash
$ docker info
Client:
 Context:    default
 Debug Mode: false
 Plugins:
  app: Docker App (Docker Inc., v0.9.1-beta3)
  buildx: Build with BuildKit (Docker Inc., v0.5.1-docker)
  scan: Docker Scan (Docker Inc., v0.6.0)

Server:
 Containers: 0
  Running: 0
  Paused: 0
  Stopped: 0
 Images: 16
 Server Version: 20.10.5
 ...
```
- get docker image
```bash
$ docker image list -a
REPOSITORY                                                                          TAG                  IMAGE ID       CREATED         SIZE
redis                                                                               latest               a617c1c92774   5 weeks ago     105MB
quay.io/quarkus/ubi-quarkus-native-image                                            21.0.0-java11        31ccea2b17ae   2 months ago    1.39GB
postgres                                                                            latest               4ea2949e4cb8   2 months ago    314MB
openzipkin/zipkin                                                                   latest               9b4acc3eb019   3 months ago    150MB
```
- get docker container
```bash
$ docker ps
```

# Create Database Container
- start docker with below command for start postgres database with persistence storage (local path)
  - expose port 5432
  - name of container is 'postgres'
  - db password = docker
  - db user = docker
  - db name = docker
  - persistence path = /User/ckongman/work/postgresql/data, create folder /User/ckongman/work/postgresql, don't create /data
  - -d for background process
```bash
$ docker run -p 5432:5432 --name postgres -e POSTGRES_PASSWORD=docker -e POSTGRES_USER=docker -e POSTGRES_DB=docker -d -v /Users/ckongman/work/postgresql/data:/var/lib/postgresql/data postgres
```

- check container process
```bash
$ docker ps
CONTAINER ID   IMAGE      COMMAND                  CREATED         STATUS         PORTS                    NAMES
675021e2cef7   postgres   "docker-entrypoint.s…"   7 seconds ago   Up 5 seconds   0.0.0.0:5432->5432/tcp   postgres
```
- remote to container and test database
```bash
$ docker exec -it postgres bash
root@f83e262793db:/# psql -U docker
psql (13.1 (Debian 13.1-1.pgdg100+1))
Type "help" for help.

docker=# \l
                              List of databases
   Name    | Owner  | Encoding |  Collate   |   Ctype    | Access privileges
-----------+--------+----------+------------+------------+-------------------
 docker    | docker | UTF8     | en_US.utf8 | en_US.utf8 |
 postgres  | docker | UTF8     | en_US.utf8 | en_US.utf8 |
 template0 | docker | UTF8     | en_US.utf8 | en_US.utf8 | =c/docker        +
           |        |          |            |            | docker=CTc/docker
 template1 | docker | UTF8     | en_US.utf8 | en_US.utf8 | =c/docker        +
           |        |          |            |            | docker=CTc/docker
(4 rows)

docker=# \dt
Did not find any relations.
docker=# \z
                            Access privileges
 Schema | Name | Type | Access privileges | Column privileges | Policies
--------+------+------+-------------------+-------------------+----------
(0 rows)

docker=# \q
root@f83e262793db:/# exit
exit
```

# Connect postgres container from local development

- source code in folder container
- review application.properties in src/main/resources, url is localhost
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/docker
```
- run application
```bash
$ mvn spring-boot:run
...
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.3.4.RELEASE)

2021-04-20 11:05:36.003  INFO 30792 --- [  restartedMain] com.example.demo.DemoApplication         : Starting DemoApplication on ckongman-mac with PID 30792 (/Users/ckongman/work/workspace/app-modern/container/target/classes started by ckongman in /Users/ckongman/work/workspace/app-modern/container)
...
2021-04-20 11:05:38.671  INFO 30792 --- [  restartedMain] com.example.demo.DemoApplication         : Started DemoApplication in 7.995 seconds (JVM running for 8.448)
```
- Test with PostMan
  - insert data to database with post command
  - call http://localhost:8080/api/v1/employees
  - POST : { "name": "tiger", "age":12 }
  ```bash
  $ curl -d '{"name":"tiger", "age":12}' -H "Content-Type: application/json" -X POST http://localhost:8080/api/v1/employees
  {"id":1,"name":"tiger","age":12}%
  ```
  - get all data with http://localhost:8080/api/v1/employees
  ```bash
  $ curl http://localhost:8080/api/v1/employees
  [{"id":1,"name":"tiger","age":12}]%
  ```
  - stop mvn, stop postgres container database (docker desktop dashboard is a good choice!)
  - don't delete data in local path, we will used it again in next step.

## Run More Than 1 Container with Docker Compose
- change application.properties to postgres
```properties
spring.datasource.url=jdbc:postgresql://postgres:5432/docker
```
- build jar file
```bash
$ mvn clean package -DskipTests
```
- review Dockerfile in container folder, build docker 
```bash
docker build ./ -t myapp
```
- review new image
```bash
$ docker images
REPOSITORY                                                                          TAG                  IMAGE ID       CREATED         SIZE
myapp                                                                               latest               b16111d01abc   5 minutes ago   142MB
redis                                                                               latest               a617c1c92774   5 weeks ago     105MB
...
```
- run application and database with docker-compose
  - review docker-compose.yml
  - run docker-compose
  ```bash
  $ docker-compose up
  Docker Compose is now in the Docker CLI, try `docker compose up`

  Creating postgres ... done
  Creating springbootapp ... done
  Attaching to postgres, springbootapp
  springbootapp    | 04:33:33.064 [main] INFO com.example.demo.DemoApplication - Init the application...
  ...
  postgres         |
  postgres         |
  postgres         | PostgreSQL Database directory appears to contain a database; Skipping initialization
  postgres         |
  postgres         | 2021-04-20 04:33:34.219 UTC [1] LOG:  starting PostgreSQL 13.1 (Debian 13.1-1.pgdg100+1) on x86_64-pc-linux-gnu, compiled by gcc (Debian 8.3.0-6) 8.3.0, 64-bit
  postgres         | 2021-04-20 04:33:34.219 UTC [1] LOG:  listening on IPv4 address "0.0.0.0", port 5432
  postgres         | 2021-04-20 04:33:34.219 UTC [1] LOG:  listening on IPv6 address "::", port 5432
  postgres         | 2021-04-20 04:33:34.223 UTC [1] LOG:  listening on Unix socket "/var/run/postgresql/.s.PGSQL.5432"
  postgres         | 2021-04-20 04:33:34.236 UTC [27] LOG:  database system was shut down at 2021-04-20 04:21:44 UTC
  postgres         | 2021-04-20 04:33:34.261 UTC [1] LOG:  database system is ready to accept connections
  springbootapp    |
  springbootapp    |   .   ____          _            __ _ _
  springbootapp    |  /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
  springbootapp    | ( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
  springbootapp    |  \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  springbootapp    |   '  |____| .__|_| |_|_| |_\__, | / / / /
  springbootapp    |  =========|_|==============|___/=/_/_/_/
  springbootapp    |  :: Spring Boot ::        (v2.3.4.RELEASE)
  springbootapp    |
  springbootapp    | 2021-04-20 04:33:34.643  INFO 1 --- [           main] com.example.demo.DemoApplication
  ...
  springbootapp    | 2021-04-20 04:33:42.131  INFO 1 --- [           main] com.example.demo.DemoApplication         : Started DemoApplication in 8.586 seconds (JVM running for 9.913)
  ```
- test get all data with http://localhost:8080/api/v1/employees
  ```bash
  $ curl http://localhost:8080/api/v1/employees
  [{"id":1,"name":"tiger","age":12}]%
  ```




