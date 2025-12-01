# Cookable Kotlin App
Cookable is an Android app that helps the user manage cupboard items, create a shopping list, and find recipes they can cook based on what ingredients they already have. Its targeted towards students, busy homes, new cooks anyone wanting to save time, money, & reduce food waste.

# Features
`Cupboard:` Add/remove ingredients you own (saved with Room DB)

`Shopping List:` Add items to buy, remove them, or mark them as bought (moves them to cupboard)

`Recipes:`
- 3 built-in local recipes stored using Room (loaded on first run).
- API recipe search using Retrofit + TheMealDB.

`Cookability:` The app checks which recipes can be cooked with the user’s cupboard items and highlights missing ingredients

`Recipe Detail:` Shows ingredients, cookability, and instructions, with buttons to add missing items to shopping

# Technologies
Kotlin, Jetpack Compose, ViewModel + StateFlow, Room Database, Retrofit, Coroutines, Navigation

# Demo
A short screen recording is included showing all app features here: `com/example/cookable/Cookable Demo.mp4`