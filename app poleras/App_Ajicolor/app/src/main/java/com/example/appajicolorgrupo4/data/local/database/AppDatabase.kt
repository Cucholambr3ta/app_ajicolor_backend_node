package com.example.appajicolorgrupo4.data.local.database

import android.content.Context                                  // Contexto para construir DB
import androidx.room.Database                                   // Anotación @Database
import androidx.room.Room                                       // Builder de DB
import androidx.room.RoomDatabase                               // Clase base de DB
import androidx.sqlite.db.SupportSQLiteDatabase                 // Tipo del callback onCreate
import com.example.appajicolorgrupo4.data.local.user.UserDao         // Import del DAO de usuario
import com.example.appajicolorgrupo4.data.local.user.UserEntity      // Import de la entidad de usuario
import com.example.appajicolorgrupo4.data.local.pedido.PedidoDao     // Import del DAO de pedido
import com.example.appajicolorgrupo4.data.local.pedido.PedidoEntity  // Import de la entidad de pedido
import com.example.appajicolorgrupo4.data.local.pedido.PedidoItemEntity // Import de los items de pedido
import kotlinx.coroutines.CoroutineScope                        // Para corrutinas en callback
import kotlinx.coroutines.Dispatchers                           // Dispatcher IO
import kotlinx.coroutines.launch                                // Lanzar corrutina

// @Database registra entidades y versión del esquema.
// version = 2: Agregadas las tablas de pedidos y pedido_items
@Database(
    entities = [UserEntity::class, PedidoEntity::class, PedidoItemEntity::class],
    version = 2,
    exportSchema = true // Mantener true para inspección de esquema (útil en educación)
)
abstract class AppDatabase : RoomDatabase() {

    // Exponemos los DAOs
    abstract fun userDao(): UserDao
    abstract fun pedidoDao(): PedidoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null              // Instancia singleton
        private const val DB_NAME = "app_ajicolor.db"          // Nombre del archivo .db

        // Obtiene la instancia única de la base
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // Construimos la DB con callback de precarga
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    // Callback para ejecutar cuando la DB se crea por primera vez
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Lanzamos una corrutina en IO para insertar datos iniciales
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getInstance(context).userDao()

                                // Precarga de usuarios
                                val seed = listOf(
                                    UserEntity(
                                        nombre = "Admin",
                                        correo = "admin@ajicolor.cl",
                                        clave = "Admin123!",
                                        direccion = "Santiago, Chile"
                                    ),
                                    UserEntity(
                                        nombre = "Juan Pérez",
                                        correo = "juan@ajicolor.cl",
                                        clave = "123456",
                                        direccion = "Valparaíso, Chile"
                                    )
                                )

                                // Inserta seed sólo si la tabla está vacía
                                if (dao.countUsers() == 0) {
                                    seed.forEach { dao.insert(it) }
                                }
                            }
                        }
                    })
                    // En entorno educativo, si cambias versión sin migraciones, destruye y recrea.
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance                             // Guarda la instancia
                instance                                        // Devuelve la instancia
            }
        }
    }
}
