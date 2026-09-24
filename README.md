# Automated Java Game Deployment on AWS (IaC, CI/CD & Kubernetes Pipeline)

[![CI/CD Pipeline](https://github.com/ibrahimkh-cmd/jeu-allumettes-devops/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/ibrahimkh-cmd/jeu-allumettes-devops/actions/workflows/ci-cd.yml)
[![Docker Hub](https://img.shields.io/badge/docker%20hub-ikl0934%2Fjeu__allumettes-blue.svg?logo=docker)](https://hub.docker.com/r/ikl0934/jeu_allumettes)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Ce projet implémente l'automatisation de bout en bout du cycle de vie d'une application console Java (**Jeu des Allumettes**). Il combine l'Infrastructure as Code (**Terraform**), la gestion de configuration (**Ansible**), l'intégration et le déploiement continus (**GitHub Actions**), la conteneurisation optimisée (**Docker Multi-stage build**) et l'orchestration de conteneurs (**Kubernetes / Minikube**).

---
---

##  Architecture globale

```text
[ Git Push (main) ] ──> [ GitHub Actions CI/CD ] ──> [ Docker Hub Registry ]
                          (Build & Test)                (ikl0934/jeu_allumettes)
                                │
     ┌──────────────────────────┴──────────────────────────┐
     ▼                                                      ▼
[ Terraform ] ──(Provisioning)──> [ AWS EC2 Ubuntu ]   [ Kubernetes / Minikube ]
     │                                   │                       │
     ▼ (Génération dynamique)            ▼ (Configuration)       ▼
[ inventory.yaml ] ──────────────> [ Ansible Control Node ]  [ Interactive Pod / Job ]
                                          │                  (stdin/tty, limits)
                                          ▼ (Docker pull & run)
                                   [ Conteneur Standalone ]
```

### Points clés de la conception DevOps

- **IaC sécurisée (Terraform)** : provisioning d'une instance EC2 avec restriction dynamique du Security Group SSH (`port 22`) à l'adresse IP publique de l'opérateur, via une data source HTTP.
- **Inventaire dynamique découplé** : Terraform génère automatiquement le fichier d'inventaire `inventory.yaml` utilisé par Ansible (`local_file`), éliminant toute intervention manuelle.
- **Conteneurisation optimisée (multi-stage build)** : séparation stricte entre l'environnement de compilation (`eclipse-temurin:23-jdk-alpine`) et l'environnement d'exécution minimal (`eclipse-temurin:23-jre-alpine`). Les tests unitaires sont exclus de la compilation finale.
- **Idempotence & automatisation (Ansible)** : installation automatisée du runtime Docker, gestion des droits utilisateurs (`docker group`) et synchronisation de l'image applicative.

---

## 📁 Structure du projet

```text
jeu-allumettes-devops/
├── README.md                 # Documentation technique du projet
├── LICENSE                   # Licence open source MIT
├── .gitignore                # Exclusion des secrets et des états Terraform
├── .github/
│   └── workflows/
│       └── ci-cd.yml         # Pipeline CI/CD automatisé GitHub Actions
├── app/                      # Code source et conteneurisation applicative
│   ├── src/                  # Code source Java (package allumettes)
│   └── Dockerfile            # Multi-stage build optimisé (JDK -> JRE)
├── k8s/                      # Orchestration de conteneurs Kubernetes
│   └── pod.yaml              # Manifeste du Pod interactif avec gestion des ressources
├── terraform/                # Infrastructure as Code (AWS)
│   ├── provider.tf           # Configuration des providers AWS, HTTP et Local
│   ├── vpc_sg.tf              # Security Group avec restriction IP dynamique
│   ├── keypair.tf             # Déclaration de la clé SSH AWS
│   ├── instance.tf            # Déploiement de l'EC2 Ubuntu
│   └── outputs.tf             # Extraction IP et génération d'inventory.yaml
└── ansible/                  # Configuration Management
    ├── ansible.cfg            # Paramétrage global (désactivation host checking)
    ├── inventory.yaml         # Fichier d'inventaire auto-généré par Terraform
    └── playbook.yml           # Déploiement du runtime Docker et pull de l'image
```

---

##  Pipeline CI/CD (GitHub Actions)

Le workflow `.github/workflows/ci-cd.yml` s'exécute à chaque commit sur `main` modifiant `app/**` :

1. **Checkout & Setup JDK 23** : vérification de l'environnement de compilation Java.
2. **Compilation de contrôle** : validation de l'absence d'erreurs syntaxiques.
3. **Docker Build & Push** : authentification via secrets GitHub (`DOCKERHUB_USERNAME`, `DOCKERHUB_TOKEN`) et publication de la nouvelle image multi-stage vers Docker Hub sous le tag `latest`.

---

## ☸️ Orchestration Kubernetes (Minikube / K8s)

L'application console requiert une session interactive (`stdin`/`tty`) et un arrêt propre à la fin de la partie.

**1. Démarrage du cluster local**

```bash
minikube start --driver=docker
```

**2. Déploiement déclaratif (manifeste)**

Déployer le Pod avec ses quotas de mémoire et de CPU définis dans `k8s/pod.yaml` :

```bash
# Appliquer le manifeste
kubectl apply -f k8s/pod.yaml

# S'attacher à la session interactive du jeu
kubectl attach jeu-allumettes -c game -i -t

# Nettoyer la ressource une fois la partie terminée
kubectl delete -f k8s/pod.yaml
```

**3. Exécution avec stratégies personnalisées (à la volée)**

Pour injecter des arguments spécifiques à l'application sans modifier le fichier YAML :

```bash
kubectl run jeu-custom -it --rm \
  --image=ikl0934/jeu_allumettes:latest \
  --restart=Never \
  -- Ibrahim@humain Ordinateur@rapide
```

---

##  Déploiement Cloud sur AWS (Terraform & Ansible)

### 1. Prérequis

- Compte AWS configuré (`aws configure`) avec permissions EC2.
- Terraform (>= 1.5).
- Paire de clés SSH enregistrée localement (`~/.ssh/dove_key`).

### 2. Provisioning de l'infrastructure (Terraform)

```bash
cd terraform
terraform init
terraform apply -auto-approve
```

Automatisation : Terraform instancie l'EC2, configure le pare-feu SSH filtré sur l'IP active et écrit l'inventaire `../ansible/inventory.yaml`.

### 3. Configuration et déploiement applicatif (Ansible)

Ansible est exécuté depuis un conteneur de contrôle pour garantir une portabilité stricte :

```bash
# Lancement du conteneur de contrôle
MSYS_NO_PATHCONV=1 docker run --rm -it \
  -v "$(pwd)/../ansible":/ansible \
  -v "$HOME/.ssh/dove_key":/root/.ssh/dove_key:ro \
  -w /ansible cytopia/ansible:latest sh
```

À l'intérieur du conteneur :

```bash
# 1. Dépendances et configuration de l'environnement
apk add --no-cache openssh-client
export ANSIBLE_CONFIG=/ansible/ansible.cfg

# 2. Isolation de la clé SSH et droits stricts
mkdir -p /tmp/.ssh && cp /root/.ssh/dove_key /tmp/.ssh/dove_key && chmod 600 /tmp/.ssh/dove_key

# 3. Test de connectivité
ansible all -m ping --private-key /tmp/.ssh/dove_key

# 4. Exécution du playbook
ansible-playbook playbook.yml --private-key /tmp/.ssh/dove_key

# 5. Quitter le conteneur
exit
```

### 4. Lancement direct sur l'instance AWS

```bash
ssh -i ~/.ssh/dove_key ubuntu@<IP_PUBLIQUE_EC2>
docker run --rm -it ikl0934/jeu_allumettes
```

---

##  Nettoyage des ressources

Pour libérer les ressources et éviter tout coût AWS résiduel :

```bash
cd terraform
terraform destroy -auto-approve
```

---

##  Stack technique

| Domaine          | Outil / Technologie          | Rôle                                                       |
|-------------------|-------------------------------|--------------------------------------------------------------|
| Langage           | Java 23 (Eclipse Temurin)      | Logique métier du jeu des allumettes                         |
| Conteneurisation  | Docker (Multi-stage build)     | Image légère Alpine JRE isolée                                |
| CI / CD           | GitHub Actions                 | Compilation automatisée et push Docker Hub                    |
| Orchestration     | Kubernetes / Minikube          | Gestion déclarative des Pods et allocation de ressources      |
| IaC               | Terraform (>= 1.5)             | Provisioning de l'instance AWS EC2 et des Security Groups     |
| Configuration     | Ansible                        | Déploiement automatisé du runtime Docker sur l'hôte           |
| Registry          | Docker Hub                     | Hébergement public de l'image applicative                     |

---

##  Licence

Ce projet est sous licence MIT - voir le fichier [LICENSE](LICENSE) pour plus de détails.
