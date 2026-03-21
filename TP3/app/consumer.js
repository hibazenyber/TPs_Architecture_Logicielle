const amqp = require('amqplib');

async function demarrerConsommateur() {
    try {
        const conn = await amqp.connect('amqp://admin:admin@rabbitmq');
        const channel = await conn.createChannel();
        const QUEUE = 'commandes';

        await channel.assertQueue(QUEUE, { durable: true });
        console.log('👷 Consommateur en attente de messages...');

        channel.consume(QUEUE, (msg) => {
            if (msg !== null) {
                const commande = JSON.parse(msg.content.toString());
                console.log('------------------------------------');
                console.log('🔔 NOUVELLE COMMANDE REÇUE !');
                console.log(`📦 Produit : ${commande.produit}`);
                console.log(`👤 Client  : ${commande.client}`);
                console.log(`🆔 ID      : ${commande.id}`);
                console.log('📧 Email de confirmation envoyé (simulé)');
                console.log('------------------------------------');
                channel.ack(msg); // Accuse réception
            }
        });
    } catch (err) {
        console.log('Attente de RabbitMQ...');
        setTimeout(demarrerConsommateur, 5000); // Réessaie si RabbitMQ n'est pas prêt
    }
}

demarrerConsommateur();