# Adapt

## What is Adapt?
Adapt is an Android Library written in Kotlin with an aim to provide a clean, well-integrated and easy-to-use experience in writing RecyclerView adapters.

It leverages Kotlin to create a typesafe DSL that makes writing Adapters simple.

Documentation: https://vshnv.github.io/adapt/#adapt



## Getting Started

##### Gradle:
```groovy
dependencies {
    implementation("io.github.vshnv:adapt:0.0.6")
}
```

Make sure you have Maven Central added as a repository.
In your settings.gradle file, verify that `mavenCentral()` is added as a repository
```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral() // <---- Make sure this is present
    }
}
```

## Sample Usage

Assume we have some data class `Person` to be shown in a RecyclerView.
```kotlin
data class Person(
    val name: String,
    val age: Int,
    val avatarUrl: String
)
```
Also assume we create a layout file `layout_person.xml` .



Now we can create a simple adapter for this using adapt:
```kotlin

fun createPersonAdapter() = adapt<Person> {
    create {
        // Inflating view for element
        val personBinding = LayoutPersonBinding.inflate(LayoutInflater.from(it.context), it, false)
        ViewSource.BindingViewSource(personBinding, ViewBinding::getRoot)
    }.bind {
        binding.name.text = data.name
        binding.age.text = "Aged ${data.age}"
    }.withLifecycle { lifecycleOwner ->
        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val avatar = fetchAvatar(avatarUrl) // Some long running task required for this specific binding
            binding.avatar.setImageBitmap(avatar)
        }
    }
}

```
> While the above example uses ViewBinding, you can use DataBinding or just views for the same.

You can now submit values to the adapter created using your function.

```kotlin
val myData = listOf(
    Person("Person A", 31),
    Person("Person B", 25),
    Person("Person C", 52),
)

val adapter = createPersonAdapter()
recyclerView.adapter = adapter
adapter.submitData(myData)
```
And thats it!
> `submitData` handles async diffing and publishes only changes to your data. For better performance it is recommended to include `itemEquals` and `contentEquals` in your adapter config. This is further explained a few sections below.
