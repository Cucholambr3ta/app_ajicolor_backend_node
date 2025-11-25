const request = require("supertest");
const { MongoMemoryServer } = require("mongodb-memory-server");
const mongoose = require("mongoose");
const jwt = require("jsonwebtoken");

jest.mock("../src/config/db", () => jest.fn());
jest.mock("../src/utils/sendEmail", () => jest.fn());

const app = require("../src/index");
const User = require("../src/models/User");
const Product = require("../src/models/Product");
const Order = require("../src/models/Order");

let mongoServer;
let testUser;
let adminToken;
let userToken;

describe("Full Workflow Tests", () => {
  beforeAll(async () => {
    mongoServer = await MongoMemoryServer.create();
    const uri = mongoServer.getUri();
    await mongoose.connect(uri);
    process.env.JWT_SECRET = "testsecret";
    process.env.NODE_ENV = "test";
  });

  afterAll(async () => {
    await mongoose.disconnect();
    await mongoServer.stop();
  });

  beforeEach(async () => {
    await User.deleteMany({});
    await Product.deleteMany({});
    await Order.deleteMany({});
  });

  describe("Flujo Completo: Autenticación", () => {
    it("Registro → Login → Acceso a recursos protegidos", async () => {
      // 1. Registro
      const registerRes = await request(app)
        .post("/api/v1/usuarios/register")
        .send({
          nombre: "Usuario Test",
          email: `test${Date.now()}@example.com`,
          password: "password123",
          telefono: "123456789",
          direccion: "Calle Test 123",
        });

      expect(registerRes.statusCode).toEqual(201);
      expect(registerRes.body).toHaveProperty("token");

      const token = registerRes.body.token;
      const userId = registerRes.body._id;

      // 2. Login
      const loginRes = await request(app).post("/api/v1/usuarios/login").send({
        email: registerRes.body.email,
        password: "password123",
      });

      expect(loginRes.statusCode).toEqual(200);
      expect(loginRes.body).toHaveProperty("token");

      // 3. Acceder a recurso protegido (pedidos del usuario)
      const ordersRes = await request(app)
        .get(`/api/v1/pedidos/usuario/${userId}`)
        .set("Authorization", `Bearer ${token}`);

      expect(ordersRes.statusCode).toEqual(200);
    });
  });

  describe("Flujo Completo: Recuperación de Contraseña", () => {
    it("Recuperar contraseña → Resetear → Login", async () => {
      // Crear usuario
      const user = await User.create({
        nombre: "Test User",
        email: "recover@example.com",
        password: "oldpassword",
        telefono: "123456789",
        direccion: "Test Address",
      });

      // 1. Solicitar recuperación
      const recoverRes = await request(app)
        .post("/api/v1/usuarios/recover")
        .send({ email: "recover@example.com" });

      expect(recoverRes.statusCode).toEqual(200);
      expect(recoverRes.body).toHaveProperty("recoveryCode");

      const recoveryCode = recoverRes.body.recoveryCode;

      // 2. Resetear contraseña
      const resetRes = await request(app)
        .post("/api/v1/usuarios/reset-password")
        .send({
          email: "recover@example.com",
          recoveryCode: recoveryCode,
          newPassword: "newpassword123",
        });

      expect(resetRes.statusCode).toEqual(200);
      expect(resetRes.body).toHaveProperty("token");

      // 3. Login con nueva contraseña
      const loginRes = await request(app).post("/api/v1/usuarios/login").send({
        email: "recover@example.com",
        password: "newpassword123",
      });

      expect(loginRes.statusCode).toEqual(200);
    });

    it("Resetear con código expirado debe fallar", async () => {
      const user = await User.create({
        nombre: "Test User",
        email: "expired@example.com",
        password: "password",
        resetPasswordToken: "123456",
        resetPasswordExpires: Date.now() - 1000, // Expirado
      });

      const resetRes = await request(app)
        .post("/api/v1/usuarios/reset-password")
        .send({
          email: "expired@example.com",
          recoveryCode: "123456",
          newPassword: "newpassword",
        });

      expect(resetRes.statusCode).toEqual(400);
    });
  });

  describe("Flujo Completo: Compra", () => {
    it("Login → Obtener productos → Crear pedido → Verificar", async () => {
      // Crear usuario y producto
      const user = await User.create({
        nombre: "Comprador",
        email: "buyer@example.com",
        password: "password123",
        telefono: "123456789",
        direccion: "Address",
      });

      const token = jwt.sign({ id: user._id }, process.env.JWT_SECRET, {
        expiresIn: "30d",
      });

      const product = await Product.create({
        id: "prod1",
        nombre: "Producto Test",
        descripcion: "Descripción",
        precio: 10000,
        categoria: "SERIGRAFIA",
        stock: 10,
        imagenUrl: "http://img.com",
      });

      // 1. Obtener productos
      const productsRes = await request(app).get("/api/v1/productos");
      expect(productsRes.statusCode).toEqual(200);
      expect(productsRes.body.length).toBeGreaterThan(0);

      // 2. Crear pedido
      const orderRes = await request(app)
        .post("/api/v1/pedidos")
        .set("Authorization", `Bearer ${token}`)
        .send({
          numeroPedido: `ORD-${Date.now()}`,
          usuario: user._id,
          productos: [
            {
              producto: product._id,
              cantidad: 2,
              precioUnitario: 10000,
            },
          ],
          subtotal: 20000,
          impuestos: 0,
          costoEnvio: 0,
          total: 20000,
          direccionEnvio: "Address",
          telefono: "123456789",
          metodoPago: "EFECTIVO",
        });

      expect(orderRes.statusCode).toEqual(201);

      // 3. Verificar pedido
      const userOrdersRes = await request(app)
        .get(`/api/v1/pedidos/usuario/${user._id}`)
        .set("Authorization", `Bearer ${token}`);

      expect(userOrdersRes.statusCode).toEqual(200);
      expect(userOrdersRes.body.length).toEqual(1);
    });
  });

  describe("Flujo Admin: Gestión de Productos", () => {
    it("Login Admin → Crear → Actualizar → Eliminar producto", async () => {
      // Crear admin
      const admin = await User.create({
        nombre: "Admin",
        email: "admin@example.com",
        password: "adminpass",
        rol: "ADMIN",
      });

      const adminToken = jwt.sign({ id: admin._id }, process.env.JWT_SECRET, {
        expiresIn: "30d",
      });

      // 1. Crear producto
      const createRes = await request(app)
        .post("/api/v1/productos")
        .set("Authorization", `Bearer ${adminToken}`)

        .send({
          id: "prod-new",
          nombre: "Producto Nuevo",
          descripcion: "Desc",
          precio: 15000,
          categoria: "CORPORATIVA",
          stock: 5,
          imagenUrl: "http://img.com",
        });

      if (createRes.statusCode !== 201) {
        console.log("Create product error:", createRes.body);
      }
      expect(createRes.statusCode).toEqual(201);
      const productId = createRes.body._id;

      // 2. Actualizar producto
      const updateRes = await request(app)
        .put(`/api/v1/productos/${productId}`)
        .set("Authorization", `Bearer ${adminToken}`)
        .send({
          precio: 18000,
          stock: 10,
        });

      expect(updateRes.statusCode).toEqual(200);
      expect(updateRes.body.precio).toEqual(18000);

      // 3. Eliminar producto
      const deleteRes = await request(app)
        .delete(`/api/v1/productos/${productId}`)
        .set("Authorization", `Bearer ${adminToken}`);

      expect(deleteRes.statusCode).toEqual(200);

      // Verificar eliminación
      const getRes = await request(app).get(`/api/v1/productos/${productId}`);
      expect(getRes.statusCode).toEqual(404);
    });
  });

  describe("Flujo Admin: Gestión de Pedidos", () => {
    it("Login Admin → Ver todos los pedidos → Actualizar estado", async () => {
      // Crear admin, usuario y pedido
      const admin = await User.create({
        nombre: "Admin",
        email: "admin@example.com",
        password: "adminpass",
        rol: "ADMIN",
      });

      const user = await User.create({
        nombre: "User",
        email: "user@example.com",
        password: "pass",
      });

      const product = await Product.create({
        id: "prod1",
        nombre: "Producto",
        descripcion: "Descripción del producto",
        precio: 10000,
        categoria: "SERIGRAFIA",
        stock: 10,
      });

      const order = await Order.create({
        numeroPedido: "ORD-001",
        usuario: user._id,
        productos: [
          { producto: product._id, cantidad: 1, precioUnitario: 10000 },
        ],
        subtotal: 10000,
        total: 10000,
        direccionEnvio: "Address",
        telefono: "123",
        metodoPago: "EFECTIVO",
      });

      const adminToken = jwt.sign({ id: admin._id }, process.env.JWT_SECRET, {
        expiresIn: "30d",
      });

      // 1. Ver todos los pedidos
      const allOrdersRes = await request(app)
        .get("/api/v1/pedidos")
        .set("Authorization", `Bearer ${adminToken}`);

      expect(allOrdersRes.statusCode).toEqual(200);
      expect(allOrdersRes.body.length).toBeGreaterThan(0);

      // 2. Actualizar estado
      const updateStatusRes = await request(app)
        .put(`/api/v1/pedidos/ORD-001/estado`)
        .set("Authorization", `Bearer ${adminToken}`)
        .send({ estado: "ENVIADO" });

      if (updateStatusRes.statusCode !== 200) {
        console.log("Update order status error:", updateStatusRes.body);
      }

      expect(updateStatusRes.statusCode).toEqual(200);
      expect(updateStatusRes.body.estado).toEqual("ENVIADO");
    });
  });
});
