output "ec2_public_ip" {
  description = "IP publique de l'EC2 pour l'inventaire Ansible"
  value       = aws_instance.app_server.public_ip
}

# Genere automatiquement le fichier d'inventaire pour Ansible
resource "local_file" "ansible_inventory" {
  content = <<-EOT
all:
  hosts:
    app_ec2:
      ansible_host: ${aws_instance.app_server.public_ip}
      ansible_user: ubuntu
      # Chemin de la cle montee a l'interieur du conteneur Docker Ansible
      ansible_ssh_private_key_file: /root/.ssh/dove_key   
EOT

  filename = "${path.module}/../ansible/inventory.yaml"
}