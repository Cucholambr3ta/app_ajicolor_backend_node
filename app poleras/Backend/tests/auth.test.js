const request = require('supertest');
const app = require('../src/index');
const mongoose = require('mongoose');
const User = require('../src/models/User');

describe('Auth API', () => {
    beforeAll(async () => {
        // Limpiar usuarios de prueba si es necesario
    });

    afterAll(async () => {
        await mongoose.connection.close();
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
