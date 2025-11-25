const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');

const userSchema = new mongoose.Schema(
  {
    nombre: {
      type: String,
      required: true,
    },
    email: {
      type: String,
      required: true,
      unique: true,
    },
    password: {
      type: String,
      required: true,
    },
    telefono: {
      type: String,
      required: false,
    },
    direccion: {
      type: String,
      required: false,
    },
    rol: {
      type: String,
      enum: ["USER", "ADMIN"],
      default: "USER",
    },
    resetPasswordToken: {
      type: String,
      required: false,
    },
    resetPasswordExpires: {
      type: Date,
      required: false,
    },
  },
  {
    timestamps: true,
  }
);

// Encriptar password antes de guardar
userSchema.pre('save', async function (next) {
    if (!this.isModified("password")) {
      return next();
    }
    // Use work factor 1 in test environment for speed, otherwise use config value
    const rounds =
      process.env.NODE_ENV === "test"
        ? 1
        : parseInt(process.env.BCRYPT_ROUNDS || "10");
    const salt = await bcrypt.genSalt(rounds);
    this.password = await bcrypt.hash(this.password, salt);
    next();
});

// Método para comparar passwords
userSchema.methods.matchPassword = async function (enteredPassword) {
    return await bcrypt.compare(enteredPassword, this.password);
};

const User = mongoose.model('User', userSchema);

module.exports = User;
