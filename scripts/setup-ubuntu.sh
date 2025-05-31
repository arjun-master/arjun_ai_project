#!/bin/bash

# Update system packages
sudo apt-get update
sudo apt-get upgrade -y

# Install required packages
sudo apt-get install -y \
    apt-transport-https \
    ca-certificates \
    curl \
    software-properties-common \
    openjdk-17-jdk

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Start Docker service
sudo systemctl start docker
sudo systemctl enable docker

# Add ubuntu user to docker group
sudo usermod -aG docker ubuntu

# Create app directory
sudo mkdir -p /opt/arjun-ai-project
sudo chown ubuntu:ubuntu /opt/arjun-ai-project

# Create monitoring script
cat << 'EOF' > /opt/arjun-ai-project/monitor.sh
#!/bin/bash

# Check if container is running
if ! docker ps | grep -q arjun-ai-project; then
    echo "Container not running. Attempting to restart..."
    docker start arjun-ai-project || \
    docker run -d \
        --name arjun-ai-project \
        -p 8080:8080 \
        --restart unless-stopped \
        ${DOCKER_HUB_USERNAME}/arjun-ai-project:latest
fi
EOF

chmod +x /opt/arjun-ai-project/monitor.sh

# Add monitoring to crontab
(crontab -l 2>/dev/null; echo "*/5 * * * * /opt/arjun-ai-project/monitor.sh") | crontab -

# Setup Nginx as reverse proxy
sudo apt-get install -y nginx

# Configure Nginx
sudo tee /etc/nginx/sites-available/arjun-ai-project << 'EOF'
server {
    listen 80;
    server_name your-domain.com;  # Replace with your domain

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /swagger-ui/ {
        proxy_pass http://localhost:8080/swagger-ui/;
    }

    location /v3/api-docs {
        proxy_pass http://localhost:8080/v3/api-docs;
    }
}
EOF

# Enable the site
sudo ln -s /etc/nginx/sites-available/arjun-ai-project /etc/nginx/sites-enabled/
sudo rm -f /etc/nginx/sites-enabled/default

# Test Nginx configuration
sudo nginx -t

# Restart Nginx
sudo systemctl restart nginx

# Install certbot for SSL
sudo snap install --classic certbot
sudo ln -s /snap/bin/certbot /usr/bin/certbot

echo "Setup complete! Next steps:"
echo "1. Update the Nginx configuration with your domain name"
echo "2. Run: sudo certbot --nginx"
echo "3. Set up your GitHub repository secrets" 