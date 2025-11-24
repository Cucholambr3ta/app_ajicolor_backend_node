const mongoose = require('mongoose');

const orderSchema = new mongoose.Schema({
    numeroPedido: {
        type: String,
        required: true,
        unique: true
    },
    usuario: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        required: true
    },
    productos: [{
        producto: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'Product',
            required: true
        },
        cantidad: {
            type: Number,
            required: true,
            default: 1
        },
        talla: {
            type: String,
            required: false
        },
        color: {
            type: String,
            required: false
        },
        precioUnitario: {
            type: Number,
            required: true
        }
    }],
    subtotal: {
        type: Number,
        required: true
    },
    impuestos: {
        type: Number,
        required: true,
        default: 0
    },
    costoEnvio: {
        type: Number,
        required: true,
        default: 0
    },
    total: {
        type: Number,
        required: true
    },
    direccionEnvio: {
        type: String,
        required: true
    },
    telefono: {
        type: String,
        required: true
    },
    estado: {
        type: String,
        enum: ['CONFIRMADO', 'PREPARANDO', 'ENVIADO', 'ENTREGADO'],
        default: 'CONFIRMADO'
    },
    metodoPago: {
        type: String,
        required: true
    },
    fechaConfirmacion: Date,
    fechaEnvio: Date,
    fechaEntrega: Date
}, {
    timestamps: true
});

const Order = mongoose.model('Order', orderSchema);

module.exports = Order;
