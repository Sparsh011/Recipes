package com.example.recipes.utils

object Constants {
    const val DISH_TYPE: String = "DishType"
    const val DISH_CATEGORY: String = "DishCategory"
    const val DISH_COOKING_TIME: String = "DishCookingTime"
    const val DISH_IMAGE_SOURCE_LOCAL: String = "Local"
    const val DISH_IMAGE_SOURCE_ONLINE: String = "Online"
    const val EXTRA_DISH_DETAILS: String = "DishDetails"

    fun dishTypes(): ArrayList<String>{
        val list = ArrayList<String>()
        list.add("Breakfast")
        list.add("Lunch")
        list.add("Snack")
        list.add("Dinner")
        list.add("Salad")
        list.add("Side Dish")
        list.add("Dessert")
        list.add("Other")

        return list
    }

    fun dishCategories(): ArrayList<String>{
        val list = ArrayList<String>()
        list.add("Pizza")
        list.add("BBQ")
        list.add("Bakery")
        list.add("Curry")
        list.add("Cafe")
        list.add("Tortilla")
        list.add("Wraps")
        list.add("Hot Dog")
        list.add("Sandwich")
        list.add("Tea/Coffee")
        list.add("Juice")
        list.add("Drink")
        list.add("Other")

        return list
    }

    fun dishCookTime(): ArrayList<String>{
        val list = ArrayList<String>()
        list.add("0 - 10 mins")
        list.add("10 - 20 mins")
        list.add("20 - 30 mins")
        list.add("30 - 40 mins")
        list.add("40 - 50 mins")
        list.add("50 - 60 mins")
        list.add("60 - 70 mins")
        list.add("70 - 80 mins")
        list.add("80 - 90 mins")
        list.add("90 - 100 mins")
        list.add("100 - 120 mins")
        list.add("120 - 150 mins")
        list.add("> 150 mins")
        return list
    }
}