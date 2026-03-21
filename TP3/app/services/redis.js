const Redis = require('ioredis');

// Création du client Redis pointant vers le conteneur 'redis'
const redis = new Redis({ host: 'redis', port: 6379 });

redis.on('connect', () => console.log('✅ Redis connecté'));

module.exports = redis;