# ViciousLib
# General Features
## Annotated Persistence
A powerful data serialization and deserialization system that uses annotations to automate logic. Allows making any object persistent without needing to implement any interfaces or super classes.
[Wiki Page](https://github.com/Vicious-Development/ViciousLib/wiki/Persistence)
## Command Handler
A basic command handler implementation. Takes a command and arguments. Executes an action. Uses the Builder pattern for setup.
(Wiki Page not yet added)
## Event Broadcasting
Yes, this has been done before. This system allows calling methods on event objects. Uses a Reflective implementation to do so.
[Wiki Page](https://github.com/Vicious-Development/ViciousLib/wiki/Event-Broadcasting)

# Using this Library:
1. Put this in your build script
```gradle
repositories {
    maven {
        name = "ViciousLib"
        url = uri("https://maven.pkg.github.com/Vicious-MCModding/ViciousLib")
        credentials {
            username = project.findProperty("gpr.user") ?: System.getenv("GPR_USER")
            password = project.findProperty("gpr.key") ?: System.getenv("GPR_API_KEY")
        }
    }
}
dependencies {
    compile "com.vicious:viciouslib:VERSION"
}
```
2. If you don't have a GPR Key you need to make one. To do so, go here: [https://github.com/settings/tokens](https://github.com/settings/tokens)
* Click generate new token
* Click the read:packages checkbox
* Scroll down and generate that boyo. Copy the key, and in gradle.properties write
```
gpr.user=YOUR GITHUB USERNAME
gpr.key=THE KEY YOU JUST MADE.
```
* You should be good now BUT be warned. This key grants anyone with it special privileges (the ones you gave it in that checkbox section). Make sure that this key is kept private, you'll want your git system to ignore the gradle.properties file in this case. You could also just use the System Environment variables as well.
