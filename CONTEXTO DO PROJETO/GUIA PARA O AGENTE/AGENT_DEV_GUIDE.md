# 🤖 Guia de Desenvolvimento para Agentes IA
## Finance App - Android Kotlin + Jetpack Compose

> **Objetivo:** Desenvolver app de finanças pessoais Android seguindo Clean Architecture + MVVM.
> **Agentes:** Gemini CLI + Antigravity
> **Metodologia:** Vibe Coding com iteração incremental

---

## 📦 SETUP INICIAL DO PROJETO

### STEP 0: Criar Estrutura Base
```bash
# Criar novo projeto Android no Android Studio
# File > New > New Project > Empty Activity (Compose)
# Nome: FinanceApp
# Package: com.personal.financeapp
# Minimum SDK: API 26 (Android 8.0)
# Build configuration: Kotlin DSL
```

**Checklist:**
- [ ] Projeto criado com Jetpack Compose
- [ ] Gradle sync bem-sucedido
- [ ] App roda no emulador (tela "Hello Android" padrão)

---

## 🏗️ FASE 1: ARQUITETURA & FUNDAÇÃO (Semana 1-2)

### STEP 1.1: Configurar Dependencies (build.gradle.kts)

**Arquivo:** `app/build.gradle.kts`

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") version "1.9.23-1.0.20"
    id("com.google.dagger.hilt.android") version "2.51"
}

android {
    namespace = "com.personal.financeapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.personal.financeapp"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        // Configurar BuildConfig para API Keys
        buildConfigField("String", "GEMINI_API_KEY", "\"${project.findProperty("GEMINI_API_KEY") ?: ""}\"")
        buildConfigField("String", "VISION_API_KEY", "\"${project.findProperty("VISION_API_KEY") ?: ""}\"")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.05.00")
    implementation(composeBom)
    
    // Core Android
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.1")
    implementation("androidx.activity:activity-compose:1.9.0")
    
    // Compose UI
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")
    
    // Room Database
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    
    // Hilt (Dependency Injection)
    implementation("com.google.dagger:hilt-android:2.51")
    ksp("com.google.dagger:hilt-compiler:2.51")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    
    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    
    // DataStore (alternativa ao SharedPreferences)
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    
    // WorkManager (Background tasks)
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    
    // Coil (Image loading)
    implementation("io.coil-kt:coil-compose:2.6.0")
    
    // Charts
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    
    // Date/Time
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
```

**Arquivo:** `gradle.properties`
```properties
# Adicionar (NÃO COMMITAR - adicionar ao .gitignore)
GEMINI_API_KEY=SUA_CHAVE_AQUI
VISION_API_KEY=SUA_CHAVE_AQUI
```

**Action Items:**
- [ ] Copiar dependencies acima
- [ ] Sync Gradle
- [ ] Resolver erros de build

---

### STEP 1.2: Criar Estrutura de Pastas (Clean Architecture)

```
app/src/main/java/com/personal/financeapp/
├── data/
│   ├── local/
│   │   ├── dao/              # Room DAOs
│   │   ├── entity/           # Room Entities
│   │   └── database/         # Database instance
│   ├── remote/
│   │   ├── api/              # Retrofit interfaces
│   │   ├── dto/              # Data Transfer Objects
│   │   └── service/          # API implementations
│   └── repository/           # Repository implementations
│
├── domain/
│   ├── model/                # Business models (Transaction, Goal, etc)
│   ├── repository/           # Repository interfaces
│   └── usecase/              # Use cases (ProcessReceiptUseCase, etc)
│
├── presentation/
│   ├── navigation/           # Navigation setup
│   ├── theme/                # Material Theme
│   ├── components/           # Reusable Composables
│   └── screens/              # Feature screens
│       ├── dashboard/
│       ├── transactions/
│       ├── receipt/
│       ├── assistant/
│       └── goals/
│
├── di/                       # Hilt modules
└── util/                     # Extensions, Constants, Helpers
```

**Comando para criar:**
```bash
# Linux/Mac
mkdir -p app/src/main/java/com/personal/financeapp/{data/{local/{dao,entity,database},remote/{api,dto,service},repository},domain/{model,repository,usecase},presentation/{navigation,theme,components,screens/{dashboard,transactions,receipt,assistant,goals}},di,util}

# Windows (PowerShell)
New-Item -ItemType Directory -Force -Path app/src/main/java/com/personal/financeapp/data/local/dao,entity,database
# ... repetir para outras pastas
```

---

### STEP 1.3: Configurar Room Database

**Arquivo:** `data/local/entity/TransactionEntity.kt`
```kotlin
package com.personal.financeapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Instant

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val date: String, // ISO 8601: "2026-04-25"
    
    val amount: Double,
    
    val type: String, // "income" | "expense"
    
    @ColumnInfo(name = "category_id")
    val categoryId: Long,
    
    @ColumnInfo(name = "subcategory_id")
    val subcategoryId: Long? = null,
    
    val description: String? = null,
    
    val tags: String? = null, // JSON array: ["#jac_j2", "#gasolina"]
    
    @ColumnInfo(name = "receipt_image_path")
    val receiptImagePath: String? = null,
    
    @ColumnInfo(name = "created_at")
    val createdAt: String = Instant.DISTANT_PAST.toString()
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val icon: String,
    val color: String
)

@Entity(
    tableName = "subcategories",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SubcategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "category_id")
    val categoryId: Long,
    
    val name: String
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val name: String,
    
    @ColumnInfo(name = "target_amount")
    val targetAmount: Double,
    
    @ColumnInfo(name = "target_currency")
    val targetCurrency: String,
    
    @ColumnInfo(name = "target_date")
    val targetDate: String,
    
    @ColumnInfo(name = "current_amount")
    val currentAmount: Double = 0.0,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true
)

@Entity(tableName = "exchange_rates")
data class ExchangeRateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "currency_pair")
    val currencyPair: String,
    
    val rate: Double,
    
    @ColumnInfo(name = "fetched_at")
    val fetchedAt: String
)

@Entity(tableName = "vehicle_alerts")
data class VehicleAlertEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "alert_type")
    val alertType: String,
    
    @ColumnInfo(name = "last_km")
    val lastKm: Int? = null,
    
    @ColumnInfo(name = "last_date")
    val lastDate: String? = null,
    
    @ColumnInfo(name = "next_km")
    val nextKm: Int? = null,
    
    @ColumnInfo(name = "next_date")
    val nextDate: String? = null,
    
    @ColumnInfo(name = "is_dismissed")
    val isDismissed: Boolean = false
)
```

---

**Arquivo:** `data/local/dao/TransactionDao.kt`
```kotlin
package com.personal.financeapp.data.local.dao

import androidx.room.*
import com.personal.financeapp.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>
    
    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): TransactionEntity?
    
    @Query("""
        SELECT * FROM transactions 
        WHERE date BETWEEN :startDate AND :endDate 
        ORDER BY date DESC
    """)
    fun getTransactionsByDateRange(startDate: String, endDate: String): Flow<List<TransactionEntity>>
    
    @Query("SELECT * FROM transactions WHERE category_id = :categoryId ORDER BY date DESC")
    fun getTransactionsByCategory(categoryId: Long): Flow<List<TransactionEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)
    
    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)
    
    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)
    
    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()
    
    // Query customizada para Text-to-SQL
    @RawQuery
    suspend fun executeCustomQuery(query: androidx.sqlite.db.SupportSQLiteQuery): List<TransactionEntity>
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY name")
    fun getAllCategories(): Flow<List<CategoryEntity>>
    
    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Long): CategoryEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)
    
    @Query("SELECT * FROM subcategories WHERE category_id = :categoryId")
    fun getSubcategoriesByCategory(categoryId: Long): Flow<List<SubcategoryEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubcategories(subcategories: List<SubcategoryEntity>)
}

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals WHERE is_active = 1 ORDER BY target_date")
    fun getActiveGoals(): Flow<List<GoalEntity>>
    
    @Query("SELECT * FROM goals WHERE id = :id")
    suspend fun getGoalById(id: Long): GoalEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity): Long
    
    @Update
    suspend fun updateGoal(goal: GoalEntity)
    
    @Delete
    suspend fun deleteGoal(goal: GoalEntity)
}

@Dao
interface ExchangeRateDao {
    @Query("""
        SELECT * FROM exchange_rates 
        WHERE currency_pair = :pair 
        ORDER BY fetched_at DESC 
        LIMIT 1
    """)
    suspend fun getLatestRate(pair: String): ExchangeRateEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRate(rate: ExchangeRateEntity)
    
    @Query("DELETE FROM exchange_rates WHERE currency_pair = :pair AND id NOT IN (SELECT id FROM exchange_rates WHERE currency_pair = :pair ORDER BY fetched_at DESC LIMIT 10)")
    suspend fun cleanOldRates(pair: String)
}

@Dao
interface VehicleAlertDao {
    @Query("SELECT * FROM vehicle_alerts WHERE is_dismissed = 0 ORDER BY next_date")
    fun getActiveAlerts(): Flow<List<VehicleAlertEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: VehicleAlertEntity): Long
    
    @Update
    suspend fun updateAlert(alert: VehicleAlertEntity)
    
    @Query("UPDATE vehicle_alerts SET is_dismissed = 1 WHERE id = :alertId")
    suspend fun dismissAlert(alertId: Long)
}
```

---

**Arquivo:** `data/local/database/AppDatabase.kt`
```kotlin
package com.personal.financeapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.personal.financeapp.data.local.dao.*
import com.personal.financeapp.data.local.entity.*

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        SubcategoryEntity::class,
        GoalEntity::class,
        ExchangeRateEntity::class,
        VehicleAlertEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun goalDao(): GoalDao
    abstract fun exchangeRateDao(): ExchangeRateDao
    abstract fun vehicleAlertDao(): VehicleAlertDao
    
    companion object {
        const val DATABASE_NAME = "finance_app.db"
    }
}
```

---

### STEP 1.4: Configurar Hilt (Dependency Injection)

**Arquivo:** `FinanceApp.kt` (Application class)
```kotlin
package com.personal.financeapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FinanceApp : Application()
```

**Arquivo:** `AndroidManifest.xml` (adicionar)
```xml
<application
    android:name=".FinanceApp"
    ...>
```

**Arquivo:** `di/DatabaseModule.kt`
```kotlin
package com.personal.financeapp.di

import android.content.Context
import androidx.room.Room
import com.personal.financeapp.data.local.dao.*
import com.personal.financeapp.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration() // REMOVER em produção
            .build()
    }
    
    @Provides
    fun provideTransactionDao(database: AppDatabase): TransactionDao {
        return database.transactionDao()
    }
    
    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }
    
    @Provides
    fun provideGoalDao(database: AppDatabase): GoalDao {
        return database.goalDao()
    }
    
    @Provides
    fun provideExchangeRateDao(database: AppDatabase): ExchangeRateDao {
        return database.exchangeRateDao()
    }
    
    @Provides
    fun provideVehicleAlertDao(database: AppDatabase): VehicleAlertDao {
        return database.vehicleAlertDao()
    }
}
```

---

## 🎨 FASE 2: UI BÁSICA (Semana 2-3)

### STEP 2.1: Configurar Navigation

**Arquivo:** `presentation/navigation/NavGraph.kt`
```kotlin
package com.personal.financeapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.personal.financeapp.presentation.screens.dashboard.DashboardScreen
import com.personal.financeapp.presentation.screens.transactions.TransactionsScreen
import com.personal.financeapp.presentation.screens.receipt.ReceiptScanScreen
import com.personal.financeapp.presentation.screens.assistant.AssistantScreen
import com.personal.financeapp.presentation.screens.goals.GoalsScreen

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Transactions : Screen("transactions")
    object Receipt : Screen("receipt")
    object Assistant : Screen("assistant")
    object Goals : Screen("goals")
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController)
        }
        composable(Screen.Transactions.route) {
            TransactionsScreen(navController)
        }
        composable(Screen.Receipt.route) {
            ReceiptScanScreen(navController)
        }
        composable(Screen.Assistant.route) {
            AssistantScreen(navController)
        }
        composable(Screen.Goals.route) {
            GoalsScreen(navController)
        }
    }
}
```

---

### STEP 2.2: Criar Tela Principal (Dashboard)

**Arquivo:** `presentation/screens/dashboard/DashboardScreen.kt`
```kotlin
package com.personal.financeapp.presentation.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.personal.financeapp.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Finance App") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Goals.route) }) {
                        Icon(Icons.Default.EmojiEvents, "Metas")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Dashboard, "Dashboard") },
                    label = { Text("Início") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Transactions.route) },
                    icon = { Icon(Icons.Default.List, "Transações") },
                    label = { Text("Transações") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Receipt.route) },
                    icon = { Icon(Icons.Default.CameraAlt, "Cupom") },
                    label = { Text("Cupom") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Assistant.route) },
                    icon = { Icon(Icons.Default.SmartToy, "Assistente") },
                    label = { Text("Assistente") }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Add transaction */ }
            ) {
                Icon(Icons.Default.Add, "Nova transação")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "💰 Resumo Financeiro",
                style = MaterialTheme.typography.headlineMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // TODO: Cards de resumo, gráficos, etc.
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Saldo do Mês", style = MaterialTheme.typography.titleMedium)
                    Text("R$ 0,00", style = MaterialTheme.typography.displaySmall)
                }
            }
        }
    }
}
```

**Action Items:**
- [ ] Criar todas as telas básicas (copy/paste estrutura acima)
- [ ] Testar navegação entre telas
- [ ] Verificar Material Theme aplicado

---

## 🤖 FASE 3: INTEGRAÇÃO COM APIs (Semana 3-4)

### STEP 3.1: Configurar Retrofit

**Arquivo:** `data/remote/api/GeminiApi.kt`
```kotlin
package com.personal.financeapp.data.remote.api

import com.personal.financeapp.data.remote.dto.GeminiRequest
import com.personal.financeapp.data.remote.dto.GeminiResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApi {
    @POST("v1beta/models/gemini-1.5-pro:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}
```

**Arquivo:** `data/remote/dto/GeminiDto.kt`
```kotlin
package com.personal.financeapp.data.remote.dto

data class GeminiRequest(
    val contents: List<Content>
) {
    data class Content(
        val parts: List<Part>
    ) {
        data class Part(val text: String)
    }
}

data class GeminiResponse(
    val candidates: List<Candidate>
) {
    data class Candidate(
        val content: Content
    ) {
        data class Content(
            val parts: List<Part>
        ) {
            data class Part(val text: String)
        }
    }
}
```

**Arquivo:** `di/NetworkModule.kt`
```kotlin
package com.personal.financeapp.di

import com.personal.financeapp.BuildConfig
import com.personal.financeapp.data.remote.api.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GeminiRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ExchangeRateRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            })
            .build()
    }
    
    @Provides
    @Singleton
    @GeminiRetrofit
    fun provideGeminiRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideGeminiApi(@GeminiRetrofit retrofit: Retrofit): GeminiApi {
        return retrofit.create(GeminiApi::class.java)
    }
    
    @Provides
    @Singleton
    @ExchangeRateRetrofit
    fun provideExchangeRateRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://economia.awesomeapi.com.br/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    // TODO: Adicionar APIs de Vision e ExchangeRate
}
```

---

## ✅ CHECKLIST DE VALIDAÇÃO POR FASE

### Fase 1 - Fundação
- [ ] App builda sem erros
- [ ] Database criado (verificar em Device File Explorer)
- [ ] Hilt injeta DAOs corretamente
- [ ] Navegação entre telas funciona

### Fase 2 - UI
- [ ] 5 telas principais criadas
- [ ] Bottom navigation funcional
- [ ] Material Theme aplicado
- [ ] FAB adiciona transação (mock)

### Fase 3 - APIs
- [ ] Retrofit configurado
- [ ] Teste de chamada Gemini (hardcoded)
- [ ] Response parsing funcional

---

## 🚨 COMANDOS ÚTEIS PARA DEBUG

```bash
# Ver logs do app em tempo real
adb logcat | grep "FinanceApp"

# Listar databases do app
adb shell run-as com.personal.financeapp ls /data/data/com.personal.financeapp/databases

# Pull do database para análise local
adb exec-out run-as com.personal.financeapp cat /data/data/com.personal.financeapp/databases/finance_app.db > local_db.db

# Abrir database no SQLite
sqlite3 local_db.db
.tables
SELECT * FROM transactions;
```

---

## 📝 PRÓXIMOS PASSOS

Após completar Fase 3:
1. Implementar Use Cases (domain layer)
2. Criar ViewModels para cada tela
3. Conectar UI com ViewModels
4. Implementar OCR de cupons
5. Implementar Text-to-SQL
6. Criar sistema de metas

**Arquivo complementar:** Veja `UI_SPECS_STITCH.md` para detalhes de design das telas.
