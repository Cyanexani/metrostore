# Metro Store

Metro Store is the Metro OS app-store client: an Android application for browsing,
downloading, and updating apps available through Google Play. It brings a
privacy-focused app-store experience to Metro OS with a clean Metro-inspired UI.

> [!IMPORTANT]
> Metro Store is an independent project. It is not affiliated with, endorsed by,
> or sponsored by Google, Google Play, or the developers of apps shown
> in the client. App metadata and downloads are retrieved from Google Play; Metro
> Store does not own or distribute that content.

## Project status

Metro Store is under active development. The Metro visual system is being applied
incrementally. Use development builds for testing rather than as a production app
store.

## Features

- Browse, search, download, install, and update Google Play apps
- Anonymous or personal Google account sign-in
- Device and locale spoofing for device- or region-limited listings
- Download management and manual version-code downloads
- Update blacklisting
- Exodus Privacy tracker information
- Plexus compatibility information for devices without Google Play Services or
  devices using microG
- Native, session, root, Shizuku, privileged-service, and device-owner installer
  paths, depending on device configuration
- Metro-inspired Android UI for Metro OS

## Requirements

- Android 6.0 (API 23) or newer
- JDK 21
- Android SDK 36 for local builds
- Git (the build uses the current commit hash for nightly version names)

## Build from source

Clone the repository and build the default vanilla debug variant:

```bash
git clone https://github.com/Cyanexani/metrostore.git
cd metrostore
./gradlew :app:assembleVanillaDebug
```

The APK is written beneath `app/build/outputs/apk/vanilla/debug/`.

Useful verification tasks:

```bash
./gradlew :app:testVanillaDebugUnitTest
./gradlew :app:lintVanillaDebug
./gradlew ktlintCheck
```

Debug builds use the public AOSP test key included in the repository. Never treat
that key as a production signing identity. Release signing is configured locally
through an untracked `app/signing.properties` file.

## Variants

The project currently defines three device flavors:

- `vanilla` — default build for standard Android devices
- `huawei` — Huawei-specific integration
- `preload` — intended for preloaded/system-app deployments

The normal build types are `debug`, `release`, and `nightly`. Huawei and preload
nightly variants are disabled by the build configuration.

## Known limitations

- Google Play's private API is reverse engineered and can change without notice.
- Paid apps cannot be purchased or downloaded.
- Apps using Play Asset Delivery cannot currently be installed or updated.
- Anonymous sessions do not support every account-backed Google Play feature.
- Anonymous login depends on external token-dispenser availability.
- The current `com.aurora.store` application ID and package namespace are retained
  for compatibility and will be migrated separately.

## Security and privacy

Metro Store handles account credentials, app packages, and installation requests.
Only install builds from a source you trust and verify the signing certificate for
official releases when release fingerprints are published. Do not report security
issues in a public issue; contact the repository owner privately through GitHub.

No telemetry system is intentionally added by this fork. Network requests needed
for store functionality are made to Google Play and to upstream services used for
authentication and app metadata. Review the source and network behavior before
using a development build with a personal account.

## Contributing

Contributions and reproducible bug reports are welcome. Before submitting a pull
request:

1. Keep changes focused and preserve upstream license headers.
2. Run the relevant unit tests, lint, and `ktlintCheck`.
3. Describe the device, Android version, build variant, and reproduction steps for
   UI or installer issues.
4. Do not commit signing credentials, personal account data, generated APKs, or
   local Android SDK paths.

## Upstream and licensing

Metro Store retains its source history, copyright notices, third-party notices,
and licensing obligations. The primary project license is GNU GPL 3.0 or later;
some files are covered by compatible or separately identified licenses. See
[LICENSES](LICENSES/), [REUSE.toml](REUSE.toml), and the SPDX headers in individual
files for authoritative details.

Major upstream references include
[Yalp Store](https://github.com/yeriomin/YalpStore),
[AppCrawler](https://github.com/Akdeniz/google-play-crawler),
[Raccoon](https://github.com/onyxbits/raccoon4), and
[SAI](https://github.com/Aefyr/SAI).
