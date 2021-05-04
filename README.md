# kin-core

This is just a version of the project `kin-android`
that is not dependent on Android:

- No Gradle plugin `kotlin-android` or `com.android.library`.
- No Android dependencies (`com.android.support:*`).
- Upgraded to Gradle `6.8.2`.

Note that `conscrypt` is being imported by the `base` sub-project,
and injected with:

```kotlin
import org.conscrypt.OpenSSLProvider
//...
Security.addProvider( OpenSSLProvider() );
```

# Example

See the example at:

- `/base/src/main/java/example/KinExample.kts`

# Original project:

- https://github.com/kinecosystem/kin-android/

