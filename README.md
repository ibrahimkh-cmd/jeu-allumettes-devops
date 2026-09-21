# Automated Java Game Deployment on AWS (IaC & DevOps Pipeline)
Ce projet implémente l'automatisation de bout en bout du déploiement d'une application console Java (**Jeu des Allumettes**) sur le cloud **AWS**, en combinant l'Infrastructure as Code (**Terraform**), la gestion de configuration (**Ansible**) et la conteneurisation (**Docker Multi-stage build**).

---

##  Architecture globale

```
[ Terraform ] ──(Provisioning)──> [ AWS EC2 Ubuntu ]
       │                                    │
       ▼ (Génération dynamique)             ▼ (Configuration)
[ inventory.yaml ] ─────────────> [ Ansible Control Node ]
                                             │
                                             ▼ (Docker pull & run)
                                   [ Docker Hub Registry ]
                                   (ikl0934/jeu_allumettes)
```

### Points clés de la conception DevOps

- **IaC sécurisée (Terraform)** : provisioning d'une instance EC2 avec restriction dynamique du Security Group SSH (`port 22`) à l'adresse IP publique de l'opérateur, via une data source HTTP.
- **Inventaire dynamique découplé** : Terraform génère automatiquement le fichier d'inventaire `inventory.yaml` utilisé par Ansible (`local_file`), éliminant toute intervention manuelle.
- **Conteneurisation optimisée (multi-stage build)** : séparation stricte entre l'environnement de compilation (`eclipse-temurin:23-jdk-alpine`) et l'environnement d'exécution minimal (`eclipse-temurin:23-jre-alpine`). Les tests unitaires sont exclus de la compilation finale.
- **Idempotence & automatisation (Ansible)** : installation automatisée du runtime Docker, gestion des droits utilisateurs (`docker group`) et synchronisation de l'image applicative.

---

## 📁 Structure du projet

```
jeu-allumettes-devops/
├── README.md                 # Documentation technique du projet
├── LICENSE                   # Licence open source MIT
├── .gitignore                # Exclusion des secrets et des états Terraform
├── app/                      # Code source et conteneurisation applicative
│   ├── src/                  # Code source Java (package allumettes)
│   └── Dockerfile            # Multi-stage build optimisé (JDK -> JRE)
├── terraform/                # Infrastructure as Code (AWS)
│   ├── provider.tf           # Configuration des providers AWS, HTTP et Local
│   ├── vpc_sg.tf             # Security Group avec restriction IP dynamique
│   ├── keypair.tf            # Déclaration de la clé SSH AWS
│   ├── instance.tf           # Déploiement de l'EC2 Ubuntu
│   └── outputs.tf            # Extraction IP et génération d'inventory.yaml
└── ansible/                  # Configuration Management
    ├── ansible.cfg           # Paramétrage global (désactivation host checking)
    ├── inventory.yaml        # Fichier d'inventaire auto-généré par Terraform
    └── playbook.yml          # Déploiement du runtime Docker et pull de l'image
```

---

##  Guide de déploiement pas à pas

### 1. Prérequis

- Un compte **AWS** configuré (`aws configure`) avec les permissions nécessaires.
- **Terraform** (>= 1.5).
- **Docker** installé sur le poste local.
- Une paire de clés SSH (`dove_key` / `dove_key.pub`).

---

### 2. Provisioning de l'infrastructure (Terraform)

Initialiser et déployer les composants cloud :

```bash
cd terraform
terraform init
terraform apply -auto-approve
```

> **Automatisation :** Terraform applique les règles réseau, instancie la machine EC2 et écrit immédiatement le fichier `../ansible/inventory.yaml` avec l'adresse IP publique allouée.

---

### 3. Configuration et déploiement applicatif (Ansible)

Pour garantir la portabilité et éviter toute dépendance d'OS hôte, Ansible est exécuté via un conteneur de contrôle :

```bash
# Lancement du conteneur Ansible avec montage des volumes
MSYS_NO_PATHCONV=1 docker run --rm -it \
  -v "$(pwd)/../ansible":/ansible \
  -v "$HOME/.ssh/dove_key":/root/.ssh/dove_key:ro \
  -w /ansible cytopia/ansible:latest sh
```

À l'intérieur du conteneur de contrôle :

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

---

### 4. Lancement du jeu sur l'instance distante

Connectez-vous sur l'instance AWS déployée pour lancer la partie interactivement :

```bash
ssh -i ~/.ssh/dove_key ubuntu@<IP_PUBLIQUE_EC2>
docker run --rm -it ikl0934/jeu_allumettes
```

---

## 🧹 Nettoyage des ressources

Pour éviter des coûts AWS inutiles, détruisez l'infrastructure une fois les tests terminés :

```bash
cd terraform
terraform destroy -auto-approve
```

---

## 🛠️ Stack technique

| Composant       | Technologie                          |
|-----------------|---------------------------------------|
| Langage         | Java (Eclipse Temurin 23)             |
| Conteneurisation| Docker (multi-stage build)            |
| IaC             | Terraform (>= 1.5)                    |
| Configuration   | Ansible                               |
| Cloud provider  | AWS (EC2, Security Groups)            |
| Registry        | Docker Hub (`ikl0934/jeu_allumettes`) |

---

## 📄 Licence
Ce projet est sous licence MIT - voir le fichier [LICENSE](LICENSE) pour plus de détails.
