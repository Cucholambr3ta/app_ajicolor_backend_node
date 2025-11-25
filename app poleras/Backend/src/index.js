const express = require('express');
const path = require("path");
const dotenv = require("dotenv");
const connectDB = require("./config/db");
const configureSecurity = require("./middleware/security");

// Rutas
const productRoutes = require("./routes/productRoutes");
const authRoutes = require("./routes/authRoutes");
const orderRoutes = require("./routes/orderRoutes");
const postRoutes = require("./routes/postRoutes");

dotenv.config();

// Conectar a Base de Datos (Skip in test mode for mongodb-memory-server)
if (process.env.NODE_ENV !== "test") {
  connectDB();
}

const app = express();

// Body Parser with limit to prevent DoS
app.use(express.json({ limit: "10kb" }));
app.use(express.static(path.join(__dirname, "../public")));

// Apply Security Middleware (Helmet, CORS, RateLimit, MongoSanitize)
configureSecurity(app);

// Rutas
app.use("/api/v1/productos", productRoutes);
app.use("/api/v1/usuarios", authRoutes);
app.use("/api/v1/pedidos", orderRoutes);
app.use("/api/v1/posts", postRoutes);

// Ruta base
app.get("/", (req, res) => {
  res.send("API de App Poleras funcionando...");
});

// Health Check
app.get("/health", (req, res) => res.status(200).json({ status: "OK" }));

const PORT = process.env.PORT || 3000;

if (require.main === module) {
    app.listen(PORT, () => {
        console.log(`Servidor corriendo en puerto ${PORT}`);
    });
}

module.exports = app; // Para tests
