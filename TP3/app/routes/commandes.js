const express = require('express');
const router = express.Router();
const { Commande } = require('../services/mongodb');
const redis = require('../services/redis');
const { publierCommande } = require('../services/rabbitmq');

const INSTANCE = process.env.INSTANCE || 'Instance-?';
const TTL = 30; // Temps de vie du cache : 30 secondes

// --- POST /commandes : Créer une commande ---
router.post('/', async (req, res) => {
    try {
        const { produit, quantite, client } = req.body;

        // 1. Validation
        if (!produit || !quantite || !client) {
            return res.status(400).json({ erreur: 'Champs manquants' });
        }

        // 2. Sauvegarde MongoDB
        const nouvelleCommande = new Commande({ produit, quantite, client });
        await nouvelleCommande.save();

        // 3. Invalider le cache Redis (les données ont changé)
        await redis.del('toutes_commandes');

        // 4. Publier dans RabbitMQ pour notification
        publierCommande({ id: nouvelleCommande._id, produit, client });

        res.status(201).json({
            message: 'Commande créée',
            commande: nouvelleCommande,
            traitee_par: INSTANCE
        });
    } catch (err) {
        res.status(500).json({ erreur: err.message });
    }
});

// --- GET /commandes : Lister les commandes ---
router.get('/', async (req, res) => {
    try {
        // 1. Vérifier le cache Redis
        const cached = await redis.get('toutes_commandes');
        if (cached) {
            return res.json({
                source: '⚡ CACHE Redis',
                traitee_par: INSTANCE,
                commandes: JSON.parse(cached)
            });
        }

        // 2. Si pas en cache -> aller en MongoDB
        const commandes = await Commande.find().sort({ date: -1 });

        // 3. Stocker en cache pour 30 secondes (TTL)
        await redis.setex('toutes_commandes', TTL, JSON.stringify(commandes));

        res.json({
            source: '🗄️ DATABASE MongoDB',
            traitee_par: INSTANCE,
            commandes
        });
    } catch (err) {
        res.status(500).json({ erreur: err.message });
    }
});

module.exports = router;