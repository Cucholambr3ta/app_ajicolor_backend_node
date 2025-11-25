const request = require('supertest');
const { MongoMemoryServer } = require('mongodb-memory-server');
const mongoose = require('mongoose');

// Mock connectDB BEFORE importing index.js
jest.mock('../src/config/db', () => jest.fn());

const app = require('../src/index');
const User = require('../src/models/User');

let mongoServer;

describe('Auth API', () => {
    beforeAll(async () => {
        mongoServer = await MongoMemoryServer.create();
        const uri = mongoServer.getUri();
        await mongoose.connect(uri);
        process.env.JWT_SECRET = 'testsecret';
    });

    afterAll(async () => {
        await mongoose.disconnect();
        await mongoServer.stop();
    });

    it('POST /api/v1/usuarios/register - debería registrar un usuario', async () => {
        const res = await request(app)
            .post('/api/v1/usuarios/register')
            .send({
                nombre: 'Test User',
                email: `test${Date.now()}@example.com`,
                password: 'password123',
                telefono: '123456789',
                direccion: 'Calle Falsa 123'
            });

        expect(res.statusCode).toEqual(201);
        expect(res.body).toHaveProperty('token');
    });

    it('POST /api/v1/usuarios/login - debería fallar con credenciales incorrectas', async () => {
        const res = await request(app)
            .post('/api/v1/usuarios/login')
            .send({
                email: 'noexiste@example.com',
                password: 'wrongpassword'
            });

        expect(res.statusCode).toEqual(401);
    });
});
