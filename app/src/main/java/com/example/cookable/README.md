# Cookable Kotlin App

Cookable is an Android app that helps the user manage cupboard items, create a shopping list, and find recipes they can cook based on the ingredients they already have. It is targeted towards students, busy homes, new cooks, or anyone wanting to save time, money, and reduce food waste.

# Features

Cupboard:
- Add and remove ingredients you own. Items are stored locally using Room Database.

Shopping List:
- Add items to buy, remove them, or mark them as bought. Bought items are moved automatically into the cupboard. Also stored using Room.

Recipes (API Search):
- Recipes are now fetched entirely from TheMealDB API using Retrofit. Each recipe includes a title, image, ingredients, and instructions.

Cookability:
- The app checks which recipes can be cooked with the user’s cupboard items and highlights which ones are missing.

Recipe Detail:
- Shows full ingredients, cookability status, and instructions. Missing ingredients can be added individually or all at once to the shopping list.

# Tech

Kotlin, Jetpack Compose, ViewModel + StateFlow, Room Database, Retrofit, Coroutines, Coil (for image loading), Navigation Compose.

# Demo

A short screen recording is included showing all app features here: `com/example/cookable/Cookable_Demo.mp4`