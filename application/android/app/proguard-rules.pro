# Retrofit / Gson модели сериализуются по именам полей — сохраняем DTO.
-keep class ru.ncfu.autoshow.data.remote.dto.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
