# Lumiere server

Lumiere is a backend server which can be used by the 
[Lumiere app](https://github.com/nicomahery/lumiere)
in order to send its pictures to a storage. These pictures
can then be used to train an AI or any other usage.
The only role of this server is to receive pictures from the
app and then store them in the requested location.

## Usage
Here are some example snippets to help you started creating a container.

### docker-compose
```
---
version: "3.7"
services:
  lumiere:
    image: nicomahery/lumiere_server:latest
    container_name: lumiere-server
    environment:
      - ACCESS_KEY=accesskey
    volumes:
      - ./data:/downloadedFiles #Location where files will be downloaded first
    ports:
      - 8080:8080
    restart: unless-stopped
```

### docker cli

```
docker run -d \
  --name=lumiere-server \
  -e ACCESS_KEY=accesskey \
  -p 8080:8080 \
  -v ./data:/downloadedFiles \
  --restart unless-stopped \
  nicomahery/lumiere_server:latest
```