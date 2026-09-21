# 1. Recuperer ton IP publique locale automatiquement
data "http" "my_ip" {
  url = "https://checkip.amazonaws.com"
}

# 2. Recuperer le VPC par defaut de ta region AWS
data "aws_vpc" "default" {
  default = true
}

# 3. Creer le groupe de securite dans ce VPC
resource "aws_security_group" "allow_ssh" {
  name        = "allow_ssh_allumettes"
  description = "Autoriser SSH uniquement depuis ma machine locale"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    description = "SSH restreint a mon IP publique"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["${chomp(data.http.my_ip.response_body)}/32"]
  }

  egress {
    description = "Acces sortant total pour installer Docker et apt"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "sg-allumettes"
  }
}