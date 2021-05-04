# kin-core

This is just a version of the project `kin-android`
that is not dependent on Android:

- No Gradle plugin `kotlin-android` or `com.android.library`.
- No Android dependencies (`com.android.support:*`).

Note that `conscrypt` is being imported by the `base` sub-project,
and injected with:

```kotlin
import org.conscrypt.OpenSSLProvider
//...
Security.addProvider( OpenSSLProvider() );
```

Original project:

- https://github.com/kinecosystem/kin-android/

