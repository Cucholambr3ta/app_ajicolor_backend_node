const request = require("supertest");
const { MongoMemoryServer } = require("mongodb-memory-server");
const mongoose = require("mongoose");
const jwt = require("jsonwebtoken");
const app = require("../src/index");
const User = require("../src/models/User");

jest.mock("../src/config/db", () => jest.fn());
jest.mock("../src/utils/sendEmail", () => jest.fn());

let mongoServer;

describe("Security Tests", () => {
  beforeAll(async () => {
    mongoServer = await MongoMemoryServer.create();
    const uri = mongoServer.getUri();
    await mongoose.connect(uri);
    process.env.JWT_SECRET = "testsecret";
  });

  afterAll(async () => {
    await mongoose.disconnect();
    await mongoServer.stop();
  });

  beforeEach(async () => {
    await User.deleteMany({});
  });

  describe("Role Based Access Control (RBAC)", () => {
    it("Debe rechazar acceso a ruta admin sin token", async () => {
      const res = await request(app).post("/api/v1/productos").send({});
      expect(res.statusCode).toEqual(401);
      expect(res.body.message).toMatch(/Not authorized/);
    });

    it("Debe rechazar acceso a ruta admin con token de usuario normal", async () => {
      // Crear usuario normal
      const user = await User.create({
        nombre: "User",
        email: "user@example.com",
        password: "password",
        rol: "USER",
      });

      const token = jwt.sign({ id: user._id }, process.env.JWT_SECRET, {
        expiresIn: "1d",
      });

      const res = await request(app)
        .post("/api/v1/productos")
        .set("Authorization", `Bearer ${token}`)
        .send({});

      expect(res.statusCode).toEqual(401);
      expect(res.body.message).toEqual("Not authorized as an admin");
    });

    it("Debe permitir acceso a ruta admin con token de ADMIN", async () => {
      // Crear admin
      const admin = await User.create({
        nombre: "Admin",
        email: "admin@example.com",
        password: "password",
        rol: "ADMIN",
      });

      const token = jwt.sign({ id: admin._id }, process.env.JWT_SECRET, {
        expiresIn: "1d",
      });

      // Intentar crear producto (debería pasar auth, aunque falle validación de datos)
      const res = await request(app)
        .post("/api/v1/productos")
        .set("Authorization", `Bearer ${token}`)
        .send({});

      // Esperamos 500 o 400 por validación de datos, pero NO 401
      expect(res.statusCode).not.toEqual(401);
    });
  });
});
