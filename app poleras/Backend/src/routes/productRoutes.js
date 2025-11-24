const express = require('express');
const router = express.Router();
const Product = require('../models/Product');

// @desc    Obtener todos los productos
// @route   GET /api/v1/productos
// @access  Public
router.get('/', async (req, res) => {
    try {
        const products = await Product.find({});
        res.json(products);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

// @desc    Obtener un producto por ID
// @route   GET /api/v1/productos/:id
// @access  Public
router.get('/:id', async (req, res) => {
    try {
        // Intentar buscar por _id de Mongo o por id personalizado
        let product;
        if (req.params.id.match(/^[0-9a-fA-F]{24}$/)) {
            product = await Product.findById(req.params.id);
        } else {
            product = await Product.findOne({ id: req.params.id });
        }

        if (product) {
            res.json(product);
        } else {
            res.status(404).json({ message: 'Producto no encontrado' });
        }
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

module.exports = router;
