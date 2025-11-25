const request = require('supertest');
const { MongoMemoryServer } = require('mongodb-memory-server');
const mongoose = require('mongoose');

// Mock connectDB BEFORE importing index.js
jest.mock('../src/config/db', () => jest.fn());

const app = require('../src/index');
const Product = require('../src/models/Product');

let mongoServer;

describe('Product API', () => {
    beforeAll(async () => {
        mongoServer = await MongoMemoryServer.create();
        const uri = mongoServer.getUri();
        await mongoose.connect(uri);
    });

    afterAll(async () => {
        await mongoose.disconnect();
        await mongoServer.stop();
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
