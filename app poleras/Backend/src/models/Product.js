const mongoose = require('mongoose');

const productSchema = new mongoose.Schema({
    id: {
        type: String,
        required: true,
        unique: true
    },
    nombre: {
        type: String,
        required: true
    },
    categoria: {
        type: String,
        required: true,
        enum: ['SERIGRAFIA', 'DTF', 'CORPORATIVA', 'ACCESORIOS']
    },
    imagenResId: {
        type: Number, // Manteniendo compatibilidad con el modelo de Android, aunque idealmente sería una URL
        required: false
    },
    imagenUrl: {
        type: String, // Campo nuevo para URL de imagen real
        required: false
    },
    descripcion: {
        type: String,
        required: true
    },
    precio: {
        type: Number,
        required: true
    },
    tipoTalla: {
        type: String,
        enum: ['ADULTO', 'INFANTIL'],
        required: false
    },
    permiteTipoInfantil: {
        type: Boolean,
        default: false
    },
    coloresDisponibles: [{
        type: String // Códigos HEX
    }],
    stock: {
        type: Number,
        default: 100
    },
    rating: {
        type: Number,
        default: 0
    },
    cantidadReviews: {
        type: Number,
        default: 0
    }
}, {
    timestamps: true
});

const Product = mongoose.model('Product', productSchema);

module.exports = Product;
