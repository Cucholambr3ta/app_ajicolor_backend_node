const express = require('express');
const dotenv = require('dotenv');
const cors = require('cors');
const helmet = require('helmet');
const connectDB = require('./config/db');

// Rutas
const productRoutes = require('./routes/productRoutes');
const authRoutes = require('./routes/authRoutes');
const orderRoutes = require('./routes/orderRoutes');

dotenv.config();

// Conectar a Base de Datos
connectDB();

const app = express();

// Middleware
app.use(cors());
app.use(helmet());
app.use(express.json());

// Rutas
app.use('/api/v1/productos', productRoutes);
app.use('/api/v1/usuarios', authRoutes);
app.use('/api/v1/pedidos', orderRoutes);

// Ruta base
app.get('/', (req, res) => {
    res.send('API de App Poleras funcionando...');
});

const PORT = process.env.PORT || 3000;

app.listen(PORT, () => {
    console.log(`Servidor corriendo en puerto ${PORT}`);
});

module.exports = app; // Para tests
