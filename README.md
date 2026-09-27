# Automated Java Game Deployment on AWS (IaC, CI/CD & Kubernetes)

[![CI/CD Pipeline](https://github.com/ibrahimkh-cmd/jeu-allumettes-devops/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/ibrahimkh-cmd/jeu-allumettes-devops/actions/workflows/ci-cd.yml)
[![Docker Hub](https://img.shields.io/badge/docker%20hub-ikl0934%2Fjeu__allumettes-blue.svg?logo=docker)](https://hub.docker.com/r/ikl0934/jeu_allumettes)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> *Le code applicatif (jeu des allumettes en Java) est issu d'un TP de l'INP-ENSEEIHT. Ce projet porte sur l'industrialisation de son cycle de vie : IaC, CI/CD, conteneurisation et orchestration.*

Ce projet automatise de bout en bout le cycle de vie d'une application console Java (**Jeu des Allumettes**). Il combine l'Infrastructure as Code (**Terraform**), la gestion de configuration (**Ansible**), l'intégration et la livraison continues (**GitHub Actions**), un build industrialisé (**Maven**), la conteneurisation optimisée (**Docker multi-stage**) et l'orchestration de conteneurs (**Kubernetes / Minikube**).

---

## Architecture globale

```text
[ Pull Request / Push main ] ──> [ GitHub Actions ] ──────────────> [ Docker Hub Registry ]
                                  Tests Maven · hadolint             ikl0934/jeu_allumettes
                                  Build · smoke test · Trivy         tags : <sha> + latest
                                              │
          ┌───────────────────────────────────┴────────────────────────┐
          ▼                                                            ▼
[ Terraform ] ──(Provisioning)──> [ AWS EC2 Ubuntu ]        [ Kubernetes / Minikube ]
          │                              │                             │
          ▼ (Génération dynamique)       ▼ (Configuration)             ▼
[ inventory.yaml ] ─────────────> [ Ansible ]                 [ Pod interactif ]
                                         │                     (stdin/tty, limits)
                                         ▼ (Docker pull & run)
                                  [ Conteneur standalone ]
```

### Points clés de la conception DevOps

- **IaC sécurisée (Terraform)** : provisioning d'une instance EC2 avec restriction dynamique du Security Group SSH (`port 22`) à l'adresse IP publique de l'opérateur, via une data source HTTP.
- **Inventaire dynamique découplé** : Terraform génère automatiquement le fichier d'inventaire `inventory.yaml` utilisé par Ansible (`local_file`), sans intervention manuelle.
- **Build industrialisé (Maven)** : compilation et exécution des tests unitaires JUnit à chaque build ; l'image Docker n'est pas produite si un test échoue.
- **Conteneurisation optimisée (multi-stage build)** : build Maven (`maven:3.9-eclipse-temurin-21-alpine`) puis image d'exécution minimale (`eclipse-temurin:21-jre-alpine`) avec **utilisateur non-root**. Les dépendances Maven sont mises en cache dans une couche dédiée.
- **Qualité et sécurité en CI** : lint du Dockerfile (hadolint), smoke test de l'image, scan de vulnérabilités (Trivy).
- **Images versionnées** : chaque image est taguée avec le SHA du commit (traçabilité et rollback), en plus de `latest`.
- **Idempotence & automatisation (Ansible)** : installation du runtime Docker, gestion des droits utilisateurs (`docker group`) et récupération de l'image applicative.

---

## Structure du projet

```text
jeu-allumettes-devops/
├── README.md                 # Documentation technique du projet
├── LICENSE                   # Licence open source MIT
├── .gitignore                # Exclusion des secrets et des états Terraform
├── .github/
│   └── workflows/
│       └── ci-cd.yml         # Pipeline CI/CD GitHub Actions
├── app/                      # Code source et conteneurisation applicative
│   ├── pom.xml               # Build Maven (Java 21, JUnit, jar exécutable)
│   ├── src/main/java/        # Code source Java (package allumettes)
│   ├── src/test/java/        # Tests unitaires JUnit
│   ├── .dockerignore         # Exclusions du contexte de build Docker
│   └── Dockerfile            # Multi-stage : build Maven + tests -> JRE non-root
├── k8s/
│   └── pod.yml               # Manifeste du Pod interactif avec gestion des ressources
├── terraform/                # Infrastructure as Code (AWS)
│   ├── provider.tf           # Providers AWS, HTTP et Local
│   ├── vpc_sg.tf             # Security Group avec restriction IP dynamique
│   ├── keypair.tf            # Déclaration de la clé SSH AWS
│   ├── instance.tf           # Déploiement de l'EC2 Ubuntu
│   └── outputs.tf            # Extraction IP et génération d'inventory.yaml
└── ansible/                  # Gestion de configuration
    ├── ansible.cfg           # Paramétrage global
    ├── inventory.yaml        # Inventaire auto-généré par Terraform (non versionné)
    └── playbook.yml          # Installation du runtime Docker et pull de l'image
```

---

## Pipeline CI/CD (GitHub Actions)

Le workflow `.github/workflows/ci-cd.yml` s'exécute à chaque pull request et à chaque push sur `main` :

1. **Tests** : `mvn verify` (compilation + tests unitaires JUnit, avec cache Maven).
2. **Lint** : analyse du Dockerfile avec hadolint.
3. **Build & smoke test** : construction de l'image puis partie ordinateur contre ordinateur pour valider son exécution.
4. **Scan de sécurité** : analyse des vulnérabilités de l'image avec Trivy (CRITICAL / HIGH).
5. **Publication** (sur `main` uniquement) : push vers Docker Hub avec deux tags, le SHA du commit et `latest`.

Secrets GitHub requis : `DOCKERHUB_USERNAME`, `DOCKERHUB_TOKEN`.

---

## Exécution locale avec Docker

```bash
# Build (compile + lance les tests)
docker build -t jeu-allumettes:dev ./app

# Partie ordinateur contre ordinateur
docker run --rm jeu-allumettes:dev ordi1@rapide ordi2@expert

# Partie interactive (-it obligatoire pour jouer au clavier)
docker run --rm -it jeu-allumettes:dev Ibrahim@humain Ordinateur@naif
```

Stratégies disponibles : `humain | naif | rapide | expert | tricheur`.

---

## Orchestration Kubernetes (Minikube)

L'application console requiert une session interactive (`stdin`/`tty`) et s'arrête proprement à la fin de la partie.

**1. Démarrage du cluster local**

```bash
minikube start --driver=docker
```

**2. Déploiement déclaratif (manifeste)**

Déployer le Pod avec ses requests/limits CPU et mémoire définis dans `k8s/pod.yml` :

```bash
# Appliquer le manifeste
kubectl apply -f k8s/pod.yml

# S'attacher à la session interactive du jeu
kubectl attach jeu-allumettes -c game -i -t

# Nettoyer la ressource une fois la partie terminée
kubectl delete -f k8s/pod.yml
```

**3. Exécution avec stratégies personnalisées (à la volée)**

```bash
kubectl run jeu-custom -it --rm \
  --image=ikl0934/jeu_allumettes:latest \
  --restart=Never \
  -- Ibrahim@humain Ordinateur@rapide
```

---

## Déploiement Cloud sur AWS (Terraform & Ansible)

### 1. Prérequis

- Compte AWS configuré (`aws configure`) avec permissions EC2.
- Terraform (>= 1.5).
- Paire de clés SSH enregistrée localement (`~/.ssh/dove_key`).

### 2. Provisioning de l'infrastructure (Terraform)

```bash
cd terraform
terraform init
terraform plan
terraform apply
```

Terraform instancie l'EC2, configure le pare-feu SSH filtré sur l'IP active et écrit l'inventaire `../ansible/inventory.yaml`.

### 3. Configuration et déploiement applicatif (Ansible)

Ansible est exécuté depuis un conteneur de contrôle pour garantir la portabilité :

```bash
MSYS_NO_PATHCONV=1 docker run --rm -it \
  -v "$(pwd)/../ansible":/ansible \
  -v "$HOME/.ssh/dove_key":/root/.ssh/dove_key:ro \
  -w /ansible cytopia/ansible:latest sh
```

À l'intérieur du conteneur :

```bash
# 1. Dépendances et configuration
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

### 4. Lancement sur l'instance AWS

```bash
ssh -i ~/.ssh/dove_key ubuntu@<IP_PUBLIQUE_EC2>
docker run --rm -it ikl0934/jeu_allumettes
```

---

## Nettoyage des ressources

Pour libérer les ressources et éviter tout coût AWS résiduel :

```bash
cd terraform
terraform destroy
```

---

## Stack technique

| Domaine          | Outil / Technologie              | Rôle                                                        |
| ---------------- | -------------------------------- | ----------------------------------------------------------- |
| Langage & build  | Java 21 LTS (Temurin), Maven     | Logique du jeu, compilation et tests JUnit                  |
| Conteneurisation | Docker (multi-stage, non-root)   | Image légère Alpine JRE                                     |
| CI / CD          | GitHub Actions                   | Tests, lint, smoke test, scan Trivy, push d'images versionnées |
| Qualité / sécu   | hadolint, Trivy                  | Lint du Dockerfile, scan de vulnérabilités de l'image       |
| Orchestration    | Kubernetes / Minikube            | Gestion déclarative des Pods et allocation de ressources    |
| IaC              | Terraform (>= 1.5)               | Provisioning de l'instance AWS EC2 et des Security Groups   |
| Configuration    | Ansible                          | Installation du runtime Docker sur l'hôte                   |
| Registry         | Docker Hub                       | Hébergement de l'image applicative                          |

---

## Licence

Ce projet est sous licence MIT — voir le fichier [LICENSE](LICENSE).
