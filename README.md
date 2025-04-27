# Waiter App

## Project structure

com.example.waiterapp
├── data                  # Data Layer (shared across features)
│   ├── local             # Room DB, DAOs, Entities, TypeConverters
│   │   ├── AppDatabase.kt
│   │   └── dao
│   │       └── UserDao.kt
│   │   └── entity
│   │       └── UserEntity.kt
│   ├── remote            # Retrofit/Ktor API interfaces, DTOs
│   │   ├── ApiService.kt
│   │   └── dto
│   │       └── UserDto.kt
│   ├── model             # Domain models (used by UI and Domain layers)
│   │   └── User.kt
│   └── repository        # Repository implementations
│       ├── UserRepositoryImpl.kt
│       └── ...
├── domain                # Domain Layer (Optional but recommended for complex apps)
│   ├── model             # Can reuse data/model or have specific domain models
│   ├── repository        # Repository interfaces (defined here, implemented in data)
│   │   └── UserRepository.kt
│   └── usecase           # Business logic units / Interactors
│       └── GetUserProfileUseCase.kt
├── di                    # Dependency Injection (Hilt Modules)
│   ├── AppModule.kt
│   ├── DatabaseModule.kt
│   └── NetworkModule.kt
├── ui                    # UI Layer (Compose)
│   ├── components        # Reusable common Composables (Button, LoadingIndicator)
│   ├── navigation        # Navigation setup (Routes, NavHost, NavGraphBuilder extensions)
│   │   ├── AppNavigation.kt
│   │   └── ScreenRoute.kt
│   ├── theme             # Theme.kt, Color.kt, Type.kt, Shape.kt
│   └── feature           # Package per feature <--- KEY PART
│       ├── login
│       │   ├── LoginScreen.kt
│       │   └── LoginViewModel.kt
│       │   └── LoginUiState.kt // Optional state holder
│       │   └── LoginEvent.kt   // Optional event definition
│       ├── profile
│       │   ├── ProfileScreen.kt
│       │   └── ProfileViewModel.kt
│       │   └── ProfileUiState.kt
│       │   └── ProfileEvent.kt
│       ├── settings
│       │   └── ...
│       └── ...
├── util                  # Utility classes, extensions
└── MainActivity.kt       # Entry point, sets up theme and navigation