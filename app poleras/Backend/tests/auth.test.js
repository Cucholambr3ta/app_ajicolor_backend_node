const request = require('supertest');
const mongoose = require("mongoose");
const { MongoMemoryServer } = require("mongodb-memory-server");
const app = require("../src/index");
const User = require("../src/models/User");

let mongoServer;

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
  await User.deleteMany({});
  // Create a test user
  await User.create({
    nombre: "Test User",
    email: "test@example.com",
    password: "password123", // Will be hashed by pre-save hook
  });
});

describe("POST /api/v1/usuarios/login - Security & Authentication Tests", () => {
  test("Should authenticate valid user and return token", async () => {
    const res = await request(app).post("/api/v1/usuarios/login").send({
      email: "test@example.com",
      password: "password123",
    });

    expect(res.status).toBe(200);
    expect(res.body).toHaveProperty("token");
    expect(res.body.email).toBe("test@example.com");
    expect(res.body.nombre).toBe("Test User");
  });

  test("Should reject invalid credentials with 401", async () => {
    const res = await request(app).post("/api/v1/usuarios/login").send({
      email: "test@example.com",
      password: "wrongpassword",
    });

    expect(res.status).toBe(401);
    expect(res.body).not.toHaveProperty("token");
    expect(res.body.message).toBe("Invalid credentials");
  });

  test("Should reject NoSQL injection payloads (Security)", async () => {
    const maliciousPayload = {
      email: "test@example.com",
      password: { $gt: "" }, // Malicious Object
    };

    const res = await request(app)
      .post("/api/v1/usuarios/login")
      .send(maliciousPayload);

    // Expect 400 Bad Request due to Validation or Sanitization
    expect(res.status).toBe(400);
    expect(res.body).not.toHaveProperty("token");

    // Ensure validation error is returned
    expect(res.body).toHaveProperty("errors");
  });

  test("Should reject missing email with 400", async () => {
    const res = await request(app).post("/api/v1/usuarios/login").send({
      password: "password123",
    });

    expect(res.status).toBe(400);
    expect(res.body).not.toHaveProperty("token");
  });

  test("Should reject missing password with 400", async () => {
    const res = await request(app).post("/api/v1/usuarios/login").send({
      email: "test@example.com",
    });

    expect(res.status).toBe(400);
    expect(res.body).not.toHaveProperty("token");
  });

  test("Should reject malformed email with 400", async () => {
    const res = await request(app).post("/api/v1/usuarios/login").send({
      email: "notanemail",
      password: "password123",
    });

    expect(res.status).toBe(400);
    expect(res.body).not.toHaveProperty("token");
  });
});
