const amqp = require('amqplib');

let channel;
const QUEUE = 'commandes';

async function connectRabbitMQ() {
    // Connexion avec les identifiants admin/admin définis dans le docker-compose
    const conn = await amqp.connect('amqp://admin:admin@rabbitmq');
    channel = await conn.createChannel();
    await channel.assertQueue(QUEUE, { durable: true });
    console.log('✅ RabbitMQ connecté');
}

// Fonction pour envoyer une commande dans la file d'attente
function publierCommande(commande) {
    const msg = JSON.stringify(commande);
    channel.sendToQueue(QUEUE, Buffer.from(msg), { persistent: true });
    console.log('📤 Message publié dans RabbitMQ');
}

module.exports = { connectRabbitMQ, publierCommande };