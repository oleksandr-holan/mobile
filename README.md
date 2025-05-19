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
│       │   └── LoginUiState.kt 
│       │   └── LoginEvent.kt   
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

## Overview

### MVVM

У патерні MVVM (Model-View-ViewModel) `ViewModel` відіграє ключову роль як посередник між `Model` (дані та бізнес-логіка) та `View` (UI, у нашому випадку Jetpack Compose). Його основні завдання та взаємодія з UI в Jetpack Compose виглядають так:

**Роль ViewModel:**

1.  **Зберігання та управління станом UI (UI State):**
    *   `ViewModel` містить дані, які потрібні для відображення в UI (наприклад, список елементів, текст для поля вводу, стан завантаження).
    *   Важливо, що `ViewModel` переживає зміни конфігурації (наприклад, поворот екрану), тому стан UI не втрачається.

2.  **Підготовка даних для UI:**
    *   `ViewModel` отримує дані з `Model` (репозиторіїв, UseCases) і трансформує їх у формат, зручний для відображення в UI. Наприклад, форматує дати, об'єднує дані з різних джерел.

3.  **Обробка логіки UI та взаємодії з користувачем:**
    *   `ViewModel` містить логіку, пов'язану з подіями від UI. Наприклад, коли користувач натискає кнопку, UI повідомляє про це `ViewModel`, а той, у свою чергу, може оновити дані, викликати бізнес-логіку, здійснити навігацію тощо.

4.  **Ізоляція UI від бізнес-логіки:**
    *   `ViewModel` не має жодних прямих посилань на Android Framework класи, пов'язані з UI (Activity, Fragment, Composable функції). Це робить його легко тестуємим (unit-тести).

5.  **Надання даних UI через спостережувані об'єкти (Observables):**
    *   Щоб UI міг реагувати на зміни даних, `ViewModel` надає їх у вигляді спостережуваних типів, таких як `StateFlow`, `SharedFlow`, `LiveData` або `State` (від Jetpack Compose).

**Взаємодія ViewModel з UI (Jetpack Compose):**

У Jetpack Compose взаємодія відбувається за принципом **односпрямованого потоку даних (Unidirectional Data Flow - UDF)**:

1.  **Надання стану (State flows down):**
    *   `ViewModel` експонує стан UI (наприклад, `StateFlow<MyUiState>` або `State<MyUiState>`).
    *   Composable функції підписуються на цей стан. У Jetpack Compose це зазвичай робиться за допомогою `collectAsState()` для `Flow` або простого доступу до `.value` для `State`.

2.  **Обробка подій (Events flow up):**
    *   Composable функції викликають публічні методи `ViewModel`, коли відбуваються події користувача (натискання кнопки, введення тексту тощо).
    *   `ViewModel` обробляє ці події, оновлює свій внутрішній стан, що, в свою чергу, призводить до оновлення експонованого `StateFlow` / `State`.

3.  **Рекомпозиція (Recomposition):**
    *   Коли значення `StateFlow` (або `State`) у `ViewModel` змінюється, Jetpack Compose автоматично визначає, які Composable функції читали цей стан, і **рекомпонує (перемальовує)** тільки їх. Це забезпечує ефективне оновлення UI.

**Переваги такого підходу:**

*   **Розділення відповідальностей:** UI (Compose) відповідає тільки за відображення, а `ViewModel` — за логіку та дані.
*   **Тестованість:** `ViewModel` легко тестувати без UI.
*   **Життєвий цикл:** `ViewModel` переживає зміни конфігурації, зберігаючи стан.
*   **Керованість стану:** Чіткий потік даних робить стан програми більш передбачуваним.

### Navigation

#### Core Components

Для реалізації перемикання між основними екранами в мобільних застосунках на Android з використанням Jetpack Compose, основним інструментом є **Jetpack Navigation Component**.

Ключові компоненти та концепції:

1.  **`NavController`**:
    *   **Роль**: Це центральний API для керування навігацією в вашому застосунку. Він відстежує стек навігації (back stack), керує переходами між екранами (destinations) та обробляє системну кнопку "Назад".
    *   **Створення в Compose**: `val navController = rememberNavController()`

2.  **`NavHost`**:
    *   **Роль**: Це Composable функція, яка виступає контейнером для відображення поточного екрану (destination) відповідно до стану `NavController`. Коли ви викликаєте `navController.navigate(...)`, `NavHost` автоматично замінює свій вміст на відповідний новий екран.
    *   **Використання**:
        ```kotlin
        NavHost(navController = navController, startDestination = "profile") {
            composable("profile") { ProfileScreen(navController) }
            composable("friends") { FriendsScreen(navController) }
            // ... інші екрани
        }
        ```

3.  **Навігаційний граф (Navigation Graph)**:
    *   **Роль**: Це сукупність усіх можливих екранів (destinations) у вашому застосунку та шляхів (actions) між ними. У Jetpack Compose граф визначається програмно всередині `NavHost` за допомогою DSL `composable()`.
    *   Кожен `composable("route_name") { YourScreenComposable() }` визначає один екран (destination). `route_name` – це унікальний рядок, що ідентифікує екран.

4.  **Екрани / Пункти призначення (Destinations)**:
    *   **Роль**: Це окремі Composable функції, що представляють собою UI конкретного екрану.
    *   **Приклад**: `ProfileScreen`, `FriendsListScreen` і т.д.

5.  **Маршрути (Routes)**:
    *   **Роль**: Унікальні рядкові ідентифікатори для кожного екрану (destination). Використовуються для навігації: `navController.navigate("friends_list_route")`.
    *   Можуть включати аргументи: `composable("user_details/{userId}") { ... }`.

#### Back Stack

Гаразд, давайте розберемо концепцію **"стеку повернення" (back stack)**, починаючи з самих основ.

Уявіть собі стопку тарілок:
*   Коли ви додаєте нову тарілку, ви кладете її **зверху** стопки.
*   Коли ви берете тарілку, ви берете її **зверху** стопки.
*   Ви не можете легко взяти тарілку з середини або знизу, не знявши спочатку всі, що лежать вище.

Цей принцип називається **LIFO (Last-In, First-Out)** – "останнім прийшов, першим пішов". Стек повернення в мобільних застосунках (і не тільки) працює саме за таким принципом.

**Що таке стек повернення в контексті мобільних застосунків?**

**Стек повернення** – це, по суті, список або історія екранів (або `Composable` функцій в Jetpack Compose, `Activity` в традиційному Android, `ViewController` в iOS), які користувач відвідав у застосунку в певному порядку. Коли користувач переходить з одного екрану на інший, новий екран "кладеться" на вершину цього стеку.

**Основні операції зі стеком повернення:**

1.  **Додавання (Push):**
    *   Коли користувач відкриває новий екран (наприклад, натискає на кнопку, що веде на детальний екран товару з головного екрану), цей новий екран **додається на вершину** стеку повернення.
    *   *Приклад:*
        *   Початковий стан: `[Головний екран]` (Головний екран на вершині)
        *   Користувач переходить на "Налаштування": `[Головний екран, Екран налаштувань]` (Екран налаштувань тепер на вершині)

2.  **Видалення (Pop):**
    *   Коли користувач натискає системну кнопку "Назад" (або жест "Назад"), поточний екран (той, що знаходиться на вершині стеку) **видаляється (знімається)** зі стеку.
    *   Після цього екраном, що відображається, стає той, який тепер опинився на вершині стеку (попередній екран).
    *   *Приклад (продовження):*
        *   Поточний стан: `[Головний екран, Екран налаштувань]`
        *   Користувач натискає "Назад": `[Головний екран]` (Екран налаштувань видалено, Головний екран знову на вершині і відображається)

3.  **Вершина стеку (Top):**
    *   Екран, який зараз бачить користувач, – це завжди екран, що знаходиться на вершині стеку повернення.

4.  **Порожній стек:**
    *   Якщо стек повернення стає порожнім (тобто користувач натиснув "Назад" на найпершому екрані застосунку), застосунок зазвичай закривається або повертає користувача до попереднього застосунку/домашнього екрану пристрою.

**Навіщо потрібен стек повернення?**

*   **Інтуїтивна навігація:** Це фундаментальний механізм, який дозволяє користувачам легко повертатися до попередніх місць у застосунку. Це очікувана поведінка.
*   **Управління потоком користувача:** Дозволяє розробникам контролювати, як користувач рухається по застосунку.
*   **Збереження стану (частково):** Хоча сам стек не завжди зберігає повний стан екрану (це залежить від реалізації та платформи), він зберігає послідовність переходів. Сучасні фреймворки, як Jetpack Compose з `ViewModel`, допомагають зберігати стан UI незалежно від стеку навігації.

**Стек повернення в Jetpack Navigation Component (Android):**

У Jetpack Compose, коли ви використовуєте `NavController` та `NavHost`, `NavController` автоматично керує стеком повернення для ваших `Composable` екранів.

*   `navController.navigate("route_name")`: Додає новий екран (destination) з вказаним `route_name` на вершину стеку.
*   Натискання кнопки "Назад" (або виклик `navController.popBackStack()`): Видаляє поточний екран з вершини стеку.
*   `NavController` надає розширені можливості для маніпулювання стеком, наприклад:
    *   `popUpTo("route_name") { inclusive = true }`: Видаляє всі екрани зі стеку до екрану з `route_name` (включно з ним, якщо `inclusive = true`). Це корисно для сценаріїв, як-от вихід з акаунту, де потрібно очистити історію.
    *   `launchSingleTop = true`: Якщо екран, на який ви переходите, вже є на вершині стеку, новий екземпляр не створюється, а використовується існуючий.

####