resource "aws_key_pair" "dove_key" {
  key_name   = "dove_key"
  public_key = file("~/.ssh/dove_key.pub")
}