Бузанов Никита Вячеславович ПИКД Б9123-09.03.03 6 подгруппа

Сколько сделано:
7 юнит-тестов
3 интеграционных тестов
2 нетривиальных тестов

Какие сценарии покрыты:
ЮНИТ ТЕСТЫ:
Во всех тестах проверяю только WeatherViewModel

1) initial_state_is_correct
Проверяю, что сразу после создания ViewModel экран в пустом состоянии:
не грузится ничего
списка нет
ошибок нет
строка поиска пустая
detail нет

2) search_success_updates_locations
Проверяю, что при успешном ответе репозитория:
Fake-репозиторий возвращает listOf(vladivostok)
searchQuery сохраняется
список локаций заполняется
ошибки нет
состояние не Empty
в uiState.locations появился vladivostok

3) search_error_sets_error_state
Проверяем, что при ошибке:
uiState.error заполняется
список очищается
загрузка заканчивается

4) retry_after_error_makes_new_request_and_recovers
Сначала fake-репозиторий отдаёт ошибку, потом успех:
первая попытка дала ошибку
retry делает вторую попытку
после retry ошибка исчезла и данные появились

5) empty_result_sets_empty_state
Корректная обработка пустого результата
Fake-репозиторий возвращает пустой список.
Проверяем, что если поиск успешный, но список пустой:
ошибок нет
locations пустой
включается isEmpty == true

6) open_detail_success_sets_weather_detail
Проверяет, что:
по id находится нужная локация
загружается детальная погода
selectedLocation устанавливается
weatherDetail заполняется
errorDetail остаётся null

7) short_query_does_not_call_repository
если запрос короче 2 символов
сетевой вызов не выполняется
список остаётся пустым
загрузка не начинается

НЕТРИВИАЛЬНЫЕ ТЕСТЫ:
1) retry_really_starts_new_request()
Проверяю, что retry делает новый запрос:
после retry число вызовов репозитория увеличивается
значит, пошла новая попытка запроса

2) toggle_favorite_twice_returns_to_empty_without_duplicates()
Проверяет контракт избранного:
первый toggle добавляет элемент
второй toggle убирает его
не появляется дублей
состояние избранного корректно возвращается в пустое

ИНТЕГРАЦИОННЫЕ ТЕСТЫ:
1) FavoriteLocationDaoTest
Проверяет связку:
Room Database + DAO

Что происходит в тесте:
создаётся in-memory база данных Room
берётся FavoriteLocationDao
в таблицу сохраняется FavoriteLocationEntity
потом эта запись читается обратно через DAO

2) WeatherRepositoryIntegrationTest
Проверяет связку:
WeatherRepository + FavoriteLocationDao + AppDatabase

Что происходит в тесте
создаётся in-memory Room database
создаётся WeatherRepository, в который подсовываем fake geocodin(weather) API + DAO из тестовой Room
добавляем владивосток в избранное
читаем избранное обратно
проверяем, что город сохранен

3) WeatherListLoadedStateIntegrationTest (API 34)
Проверяю, если экрану передать уже готовое состояние с загруженными данными, он должен правильно показать их пользователю

создаёт состояние экрана с одной локацией
передаёт его в WeatherListScreen
проверяет, что на экране отображаются Vladivostok и Primorsky Krai