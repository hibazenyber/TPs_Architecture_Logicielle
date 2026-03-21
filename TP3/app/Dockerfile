FROM node:18-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
# On lance le serveur et le consommateur en même temps pour le TP
CMD ["sh", "-c", "node server.js & node consumer.js"]