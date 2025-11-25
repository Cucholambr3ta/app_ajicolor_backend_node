const request = require('supertest');
const { MongoMemoryServer } = require('mongodb-memory-server');
const mongoose = require('mongoose');

// Mock connectDB BEFORE importing index.js
jest.mock('../src/config/db', () => jest.fn());

const app = require('../src/index');
const Order = require('../src/models/Order');
const User = require('../src/models/User');
const Product = require('../src/models/Product');

let mongoServer;
let userId;
let productId;

describe('Order API', () => {
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

    beforeEach(async () => {
        await Order.deleteMany({});
        await User.deleteMany({});
        await Product.deleteMany({});

        // Create dummy user and product
        const user = await User.create({
            nombre: 'Test User',
            email: 'test@example.com',
            password: 'password123',
            telefono: '123456789',
            direccion: 'Test Address'
        });
        userId = user._id;

        const product = await Product.create({
            id: 'prod1',
            nombre: 'Test Product',
            descripcion: 'Desc',
            precio: 1000,
            categoria: 'SERIGRAFIA',
            stock: 10,
            imagenUrl: 'http://img.com'
        });
        productId = product._id;
    });

    it('POST /api/v1/pedidos - debería crear un pedido', async () => {
        const res = await request(app)
            .post('/api/v1/pedidos')
            .send({
                numeroPedido: 'ORD-001',
                usuario: userId,
                productos: [{
                    producto: productId,
                    cantidad: 2,
                    precioUnitario: 1000
                }],
                subtotal: 2000,
                impuestos: 0,
                costoEnvio: 0,
                total: 2000,
                direccionEnvio: 'Test Address',
                telefono: '123456789',
                metodoPago: 'EFECTIVO'
            });

        expect(res.statusCode).toEqual(201);
        expect(res.body.numeroPedido).toEqual('ORD-001');
    });

    it('GET /api/v1/pedidos/usuario/:userId - debería retornar pedidos del usuario', async () => {
        // Create an order first
        await Order.create({
            numeroPedido: 'ORD-002',
            usuario: userId,
            productos: [{
                producto: productId,
                cantidad: 1,
                precioUnitario: 1000
            }],
            subtotal: 1000,
            impuestos: 0,
            costoEnvio: 0,
            total: 1000,
            direccionEnvio: 'Test Address',
            telefono: '123456789',
            metodoPago: 'EFECTIVO'
        });

        const res = await request(app).get(`/api/v1/pedidos/usuario/${userId}`);

        expect(res.statusCode).toEqual(200);
        expect(Array.isArray(res.body)).toBeTruthy();
        expect(res.body.length).toEqual(1);
        expect(res.body[0].numeroPedido).toEqual('ORD-002');
    });
});
