const express = require('express');
const router = express.Router();
const Order = require('../models/Order');

// Middleware simple para proteger rutas (se puede mejorar)
const protect = async (req, res, next) => {
    // Implementar validación de JWT aquí si es necesario
    // Por ahora, permitimos acceso para simplificar, pero en producción debe validarse el token
    next();
};

// @desc    Crear nuevo pedido
// @route   POST /api/v1/pedidos
// @access  Private
router.post('/', protect, async (req, res) => {
    const {
        numeroPedido,
        usuario,
        productos,
        subtotal,
        impuestos,
        costoEnvio,
        total,
        direccionEnvio,
        telefono,
        metodoPago
    } = req.body;

    if (productos && productos.length === 0) {
        return res.status(400).json({ message: 'No hay productos en el pedido' });
    } else {
        try {
            const order = new Order({
                numeroPedido,
                usuario,
                productos,
                subtotal,
                impuestos,
                costoEnvio,
                total,
                direccionEnvio,
                telefono,
                metodoPago
            });

            const createdOrder = await order.save();
            res.status(201).json(createdOrder);
        } catch (error) {
            res.status(500).json({ message: error.message });
        }
    }
});

// @desc    Obtener pedido por ID
// @route   GET /api/v1/pedidos/:id
// @access  Private
router.get('/:id', protect, async (req, res) => {
    try {
        const order = await Order.findById(req.params.id).populate('usuario', 'nombre email');

        if (order) {
            res.json(order);
        } else {
            res.status(404).json({ message: 'Pedido no encontrado' });
        }
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

module.exports = router;
