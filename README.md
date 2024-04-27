
## Project Description:

This project features a **Kubernetes-deployed Spring application** with two routes: `/hello`, which provides a version-specific greeting, and `/db/messages`, which allows messages to be retrieved and posted to a MySQL database. It's accessible from anywhere via an **ingress**. The application is integrated with a **replicated MySQL StatefulSet cluster** consisting of one write node and two read-only nodes, with connection details securely stored in a secret. The application's **Docker image** is obtained from a private repository. Scalability is provided by auto-scaling capabilities that can accommodate up to three replicas at peak usage.


## Repository Components:

This repository is divided into two main sections:

### Application code


The `application\kubernetesHelloWorld` directory contains the source code for the application. This Spring application provides several functionalities that can be accessed via the specified paths:

- **Functionality Description:**  
  The application provides a simple REST API with the following routes:

  - `/hello`: Returns a version-specific greeting.
  - `/db/messages`: 
    - `GET`: Returns all messages in JSON format.
    - `POST`: Creates a new message. Message text needs to be in the request body.
  - `/db/messages/{id}`: 
    - `GET`: Returns the message with the specified ID in JSON format.

This directory also contains a `Dockerfile` to facilitate image creation from the application code.



**Image Usage from Private Repository:**

The application utilizes an image stored in a private repository for deployment and usage within Kubernetes. Below are the commands to create, tag, and push the image to the private repository, using an example Azure registry named *micpn*:

```bash
az acr login --name micpn
docker build -t kuberneteshw:v1 .
docker tag kuberneteshw:v1 micpn.azurecr.io/kuberneteshw:v1
docker push micpn.azurecr.io/kuberneteshw:v1
```
Adjust the registry URL and image name as necessary for your environment.


### Deployment Configurations

Before deploying the application to Kubernetes, ensure the following prerequisites are met:

- **Kubernetes Access:**  
  Ensure you have access to a Kubernetes cluster and `kubectl` installed, configured, and connected to the target Kubernetes environment.

- **Private Registry Image:**  
  The application image must be pushed to a private registry. In this example, we use an Azure registry named *micpn*. Ensure the application image is accessible from the registry.

- **Secret Creation:**  
  Create a Kubernetes secret to allow access to the private registry. You can use the following commands for an Azure registry secret, replacing placeholders with actual values:

```bash
  kubectl create secret docker-registry <secret-name> \
  --docker-server=<container-registry-name>.azurecr.io \
  --docker-username=<username> \
  --docker-password=<password>
```

  - `<secret-name>`: The name of the secret to be created.
  - `<container-registry-name>`: The name of the Azure Container Registry.
  - `<username>`: The username for accessing the registry. This can be found in the Access keys tab on the Azure portal.
  - `<password>`: The password for accessing the registry. This can also be found in the Access keys tab on the Azure portal.

The deploy directory contains the yml files for deploying the application to Kubernetes. The following Kubernetes configuration files are provided:


**Application secrets:** `app_secret.yml`

This YAML file defines a Kubernetes Secret named *app-secrets* containing sensitive configuration values required by the application and database. These values are base64 encoded for security purposes, with the decoded values provided as comments for reference. These credentials are crucial for establishing the connection between the application and the database, ensuring secure and authorized communication between the two components.

Command to apply:
```bash
kubectl apply -f app_secret.yml
```

**Configuration for MySQL:** `mysql_configmap.yml`

This YAML file defines a Kubernetes ConfigMap named *mysql* that contains configuration data for the MySQL database deployment. It contains the following:

- `primary.cnf`: Configuration settings specific to the primary MySQL instance, ensuring that these settings only apply to the primary database node. This configuration includes enabling binary logging and writing.
- `replica.cnf`: Configuration settings specific to MySQL replicas, ensuring that these settings apply only to replica database nodes. This configuration includes setting replicas to super-read-only mode.
- `init-script.sql`: Initialization script to run when the MySQL container is started. This script initializes the database schema by creating a database named *kubernetes* and a table named*`messages* if they do not already exist.

These configurations and the initialization script ensure proper setup and behavior of the MySQL database deployment within the Kubernetes cluster.

Command to apply:
```bash
kubectl apply -f mysql_configmap.yml
```

**Services for MySQL:** `mysql_service.yml`

This YAML file defines two Kubernetes services to manage the MySQL database deployment:

1. **Headless Service (`mysql`):**.
   - This service provides stable DNS records for StatefulSet members.
   - The *clusterIP* is set to *none*, making it a headless service.

2. **Read client service (`mysql-read`):**.
   - This service allows connections to any MySQL instance for read operations.

Both expose port 3306 for MySQL connections, and the selector is set to *app: mysql*.
These services allow communication with the MySQL database deployment, providing both stable DNS records and separate access for read operations.

Command to apply:
```bash
kubectl apply -f mysql_service.yml
```


**MySQL StatefulSet Cluster:** `mysql_stateful_set.yml`

This YAML file defines a Kubernetes StatefulSet named `mysql`, which is responsible for managing the MySQL database deployment. It contains the following configurations:

- **Pod Template:**
  - Defines the pod template for the StatefulSet, including labels and annotations.
  - Specifies init containers for initializing MySQL and cloning data from a previous peer.
  - Defines two containers: `mysql` and `xtrabackup`, responsible for MySQL operations and backup, respectively.
  - Specifies volume mounts for storing MySQL data and configuration files.

- **MySQL Container:**
  - Specifies the MySQL container with version 5.7.
  - Sets environment variables for MySQL configuration, including root password retrieved from a Kubernetes Secret.
  - Defines ports for MySQL connections.
  - Sets resource requests for CPU and memory.
  - Configures liveness and readiness probes for health checks.

- **Xtrabackup Container:**
  - Configures the Xtrabackup container for database backup operations.
  - Defines ports for backup operations.
  - Sets resource requests for CPU and memory.

With replicas set to 3, each MySQL instance is sequentially deployed, facilitating a primary-replica configuration where the first instance serves as the primary node for write operations, while the remaining instances act as replicas for read operations. Additionally, the StatefulSet provisions PersistentVolumes (PV) and PersistentVolumeClaims (PVC) dynamically, ensuring each MySQL instance has dedicated storage for data persistence. Configured with the ReadWriteOnce access mode, each MySQL instance can exclusively read and write to its assigned PersistentVolume.


Command to apply:
```bash
kubectl apply -f mysql_stateful_set.yml
```
Command to acces a container for testing in current configuration:
```bash
kubectl exec -it mysql-0 -- mysql -uroot -pKubernetesPassword123
```


**Application deployment:** `deployment_service.yml`
This YAML configuration defines several Kubernetes resources:

1. **Deployment (`pn-deployment`):**
   - Deploys a single replica of the `kubernetes-hw-container` container, sourced from the image *micpn.azurecr.io/kuberneteshw:v1*.
   - Specifies environment variables for database connection details fetched from the `app-secrets` Secret.
   - Exposes port 8080 for TCP communication.
   - Utilizes the `registry-secret` for pulling the container image from the private registry.

2. **Service (`pn-service`):**
   - Defines a service named `pn-service`.
   - Exposes port 8080 for TCP traffic, routing requests to pods labeled *app: pn-kubernetes-hw*.

3. **Horizontal Pod Autoscaler (`deployment-autoscaler`):**
   - Scales the `pn-deployment` Deployment based on CPU utilization.
   - Configured with a minimum of 1 replica, maximum of 3 replicas, and a target CPU utilization of 40%.

These configurations collectively manage the deployment, service, and autoscaling of the created application within the Kubernetes cluster, ensuring availability, scalability, and efficient routing of incoming traffic.


Command to apply:
```bash
kubectl apply -f deployment_service.yml
```

**Application deployment:** `ingress.yml`

This YAML configuration defines an Ingress resource named `pn-ingress`, which is responsible for managing external access to the Kubernetes cluster. It defines routing rules for HTTP traffic, directing traffic to the `pn-service` service on port 8080.

The command 
```bash
 az aks approuting enable -g <resourcegroupname> -n <kubernetes-name>
```
is used to enable the Azure AKS Application Gateway Ingress Controller for an Azure Kubernetes Service (AKS) cluster. This controller integrates Azure Application Gateway with AKS, providing advanced traffic management and security features for applications running in the Kubernetes cluster.

Command to apply:
```bash
kubectl apply -f ingress.yml
```

## Deleting Deployed Resources

To clean up and delete the deployed resources associated with the project, the following commands can be executed:

1. **Delete resources labeled with owner:**
```bash
kubectl delete secret,svc,deployment,ingress -l owner=pn
```
2. **Delete HorizontalPodAutoscaler:**
```bash
kubectl delete horizontalpodautoscaler.autoscaling/deployment-autoscaler
```
3. **Delete MySQL StatefulSet:**
```bash
kubectl delete statefulset mysql
```
4. **Delete resources labeled with app=mysql:**
```bash
kubectl delete configmap,service,pvc -l app=mysql
```
5. **Delete registry secret:**
```bash
kubectl delete secret registry-secret
```

After running these commands, be sure to **verify that all resources have been successfully deleted** by running the appropriate kubectl get commands. Also note that **deleting resources does not manage the private repository**; any images stored in the private repository are not affected by these commands.

