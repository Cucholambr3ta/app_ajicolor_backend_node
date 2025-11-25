const mongoose = require('mongoose');

const auditLogSchema = new mongoose.Schema({
    user: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        required: false
    },
    action: {
        type: String,
        required: true,
        index: true
    },
    ip: {
        type: String,
        required: true
    },
    details: {
        type: mongoose.Schema.Types.Mixed,
        required: false
    },
    timestamp: {
        type: Date,
        default: Date.now,
        index: true
    }
});

// TTL Index: Automatically delete logs older than 30 days
auditLogSchema.index({ timestamp: 1 }, { expireAfterSeconds: 2592000 }); // 30 days in seconds

const AuditLog = mongoose.model('AuditLog', auditLogSchema);

module.exports = AuditLog;
