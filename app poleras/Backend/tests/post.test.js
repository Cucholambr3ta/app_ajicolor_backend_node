const request = require('supertest');
const { MongoMemoryServer } = require('mongodb-memory-server');
const mongoose = require('mongoose');

// Mock connectDB BEFORE importing index.js
jest.mock('../src/config/db', () => jest.fn());

const app = require('../src/index');
const Post = require('../src/models/Post');

let mongoServer;

describe('Post API', () => {
    beforeAll(async () => {
        mongoServer = await MongoMemoryServer.create();
        const uri = mongoServer.getUri();
        await mongoose.connect(uri);
    });

    afterAll(async () => {
        await mongoose.disconnect();
        await mongoServer.stop();
    });

    beforeEach(async () => {
        await Post.deleteMany({});
    });

    it('should get all posts', async () => {
        await Post.create({ titulo: 'Test Post', contenido: 'Contenido test' });
        const res = await request(app).get('/api/v1/posts');
        expect(res.statusCode).toEqual(200);
        expect(res.body.length).toBe(1);
        expect(res.body[0].titulo).toBe('Test Post');
    });
});
