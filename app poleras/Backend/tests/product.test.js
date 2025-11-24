const request = require('supertest');
const app = require('../src/index');
const mongoose = require('mongoose');
const Product = require('../src/models/Product');

describe('Product API', () => {
    beforeAll(async () => {
        // Conectar a una base de datos de prueba o mockear
        // Para simplificar este ejemplo, asumimos que la conexión en index.js maneja el entorno de test
        // O idealmente usar mongodb-memory-server
    });

    afterAll(async () => {
        await mongoose.connection.close();
    });

    it('GET /api/v1/productos - debería retornar lista de productos', async () => {
        const res = await request(app).get('/api/v1/productos');
        expect(res.statusCode).toEqual(200);
        expect(Array.isArray(res.body)).toBeTruthy();
    });

    it('GET /api/v1/productos/:id - debería retornar 404 para ID no existente', async () => {
        const res = await request(app).get('/api/v1/productos/999999');
        expect(res.statusCode).toEqual(404);
    });
});
