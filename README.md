# NewsRoom ![Kotlin](https://img.shields.io/badge/Kotlin-1.9.24-orange) ![Android Gradle Plugin](https://img.shields.io/badge/AGP-8.12.0-blue)
## Demo:
<table style="width:100%;"> 
  <tr> 
    <td align="center" style="width:50%;"> 
      <strong>App Tour
        <br>
        <em>3 min 08 sec
        </em>
      </strong>
      <br> 
      <img src="https://github.com/MrHAM17/NewsRoom_App/blob/main/Rough%20Work%20%26%20Data/Output%20Data/Screen%20Recordings/v1.0.0%20GIFs/1%5D%20App%20Tour.gif" height="510" width="240"> 
  </td> 
    <td align="center" style="width:50%;"> 
      <strong>Theme-switch & Widget
        <br>
        <em>1 min 30 sec
        </em>
      </strong>
      <br> 
      <img src="https://github.com/MrHAM17/NewsRoom_App/blob/main/Rough%20Work%20%26%20Data/Output%20Data/Screen%20Recordings/v1.0.0%20GIFs/2%5D%20Theme-switch%20%26%20Widget.gif" height="510" width="240"> </td> </tr> </table>

## Screenshots:
**Light Theme:**

![](https://github.com/MrHAM17/NewsRoom_App/blob/main/Rough%20Work%20%26%20Data/Output%20Data/Screenshots/v1.0.0%20Slides/Slide%2001.png)
![](https://github.com/MrHAM17/NewsRoom_App/blob/main/Rough%20Work%20%26%20Data/Output%20Data/Screenshots/v1.0.0%20Slides/Slide%2002.png)
**Dark Theme:**

![](https://github.com/MrHAM17/NewsRoom_App/blob/main/Rough%20Work%20%26%20Data/Output%20Data/Screenshots/v1.0.0%20Slides/Slide%2003.png)
![](https://github.com/MrHAM17/NewsRoom_App/blob/main/Rough%20Work%20%26%20Data/Output%20Data/Screenshots/v1.0.0%20Slides/Slide%2004.png)
**Notification & Widget Support:**

![](https://github.com/MrHAM17/NewsRoom_App/blob/main/Rough%20Work%20%26%20Data/Output%20Data/Screenshots/v1.0.0%20Slides/Slide%2005.png)

## Table of contents

1. [Project Summary](#1-project-summary)
2. [Tech Stack](#2-tech-stack)
3. [Quick Start](#3-quick-start)
4. [Tests & Reports](#4-tests--reports)
5. [Macrobenchmark & Baseline Profile](#5-macrobenchmark--baseline-profile)
6. [Baseline Profile Results](#6-baseline-profile-results)
7. [CI Workflow](#7-ci-workflow)

---

## 1. Project Summary

NewsRoom is a single-activity Android news application built with Kotlin & the MVVM architecture.
It serves as a learning + portfolio project, demonstrating production-grade practices—Hilt dependency injection, Room persistence, Retrofit/OkHttp networking, WorkManager background sync, notification handling, widget support, & performance optimization through macrobenchmarks & Baseline Profiles.

**Key Features:**

* **Bottom Navigation:** With 5 sections — Home, Saved, Search, Sources, Settings.
* **Home:** ViewPager2 with 7 news tabs, each showing article cards; detail screen with image, metadata, and actions (open · bookmark · share).
* **Saved:** Displays bookmarked news with identical UI.
* **Search:** Real-time search results, offline support using Room FTS4—allowing instant results even when offline; opens the same detail layout.
* **Sources:** Simple list of text sources.
* **Settings:** Theme selection (System / Light / Dark) + Import / Export bookmarks (CSV + JSON).
* **WorkManager:** Performs periodic sync (on startup + every 12 h) and sends success notifications.
* **Caching policy:** Avoids re-fetch for 2 min; cached data marked stale after 7 days; images cached by Glide.
* **Home-Screen Widget:** Displays top 5 headlines (each with an image & title). Selecting a headline opens the app & shows its detailed view.
* **Performance Coverage:** 71 unit tests · 61 instrumented tests · 6 macrobenchmarks · Baseline Profile

---

## 2. Tech Stack


* **Language:** Kotlin
* **Database:** Room DB
* **Architecture:** MVVM + Retrofit + Room + DI · Single-Activity (MainActivity) + Fragments · Navigation Component
* **Dependency Injection:** Hilt (@HiltAndroidApp) · HiltWorkerFactory (for WorkManager injection)
* **External API:** News from NewsAPI.org
* **Networking:** Retrofit + OkHttp (interceptor injects API key from local.properties) · Logging + Caching enabled
* **Images:** Glide for memory + disk caching
* **Background & Notifications:** WorkManager (startup + 12 hr sync) · Local notification on successful sync
* **Performance & Testing:** 71 Unit tests · 61 Instrumented tests · 6 Macrobenchmarks tests + Baseline Profile
* **Continuous Integration:** GitHub Actions

---

## 3. Quick Start

**3.1 Clone the repo:**

```bash
git clone https://github.com/MrHAM17/NewsRoom_App
cd newsroom
```
Or download the ZIP file and extract it manually.

#
**3.2 API Key Integration:**

  * The app uses the **News API** for fetching news data. It reads your API key from `local.properties` and injects it into requests via an OkHttp interceptor.
  * Get your key from 👉 [https://newsapi.org](https://newsapi.org)
  * Add it to your local properties file (see example below under signing setup).
  * This key is automatically applied to every request at runtime — no manual setup needed.

#
**3.3 Signing & Release Notes (Keystore) — IMPORTANT**

* Release & benchmark builds must be signed correctly to work as intended.

* **Signing Setup:**
  In `app/build.gradle`, both release and benchmark build types reference a release signing config.
  If the keystore file is missing, these build tasks **will fail**.

* **Options for Local Runs:**
  When running locally (for release, benchmark, or baseline profile generation), **you have two choices:**

#
**3.3.1️⃣ Recommended — Create & Use a Local Release Keystore**

For accurate, production-like results:

1. **Generate a Release Keystore**
   Run in terminal:

   ```bash
   keytool -genkeypair -v -keystore release-keystore.jks \
     -alias myappkey -keyalg RSA -keysize 2048 -validity 10000
   ```

2. **Add Credentials to `local.properties`**
   Populate the file at project root (never commit this file).
   Example:

   ```
   # News API credentials (get at https://newsapi.org)
   news.api.key=123456789                            # Replace with your real API key

   # Release keystore (example values)
   RELEASE_STORE_FILE=keystore/release-keystore.jks  # Keep as is
   RELEASE_STORE_PASSWORD=123456789                  # Your keystore password
   RELEASE_KEY_ALIAS=myappkey                        # Keep as is
   RELEASE_KEY_PASSWORD=123456789                    # Your key password
   ```

3. **Place the Keystore File**

   * Create a directory named `keystore/` in the **project root**.
   * Place the generated `release-keystore.jks` file from project-root folder to inside that directory.
   * Keep it **private**, **do not commit**, and **store a backup copy** (preferably encrypted for CI/CD injection).

#
**3.3.2️⃣ Temporary (Dev Only) — Use Debug Signing**

If you don’t have a keystore yet and just need to run locally:

1. **Comment out** these below lines in both release & benchmark build types in `app/build.gradle`:

  ```gradle
  signingConfigs { ... }                                // For both release & benchmark build types: Inside android block
  signingConfig = signingConfigs.getByName("release")   // For both release & benchmark build types: Inside android/buildTypes block
  ```
2. **Gradle will** sign with the default debug key instead.
  (Use only for local testing — never for Play Store or CI/CD releases.)

#
**3.4 Open Project in Android Studio**

Use the Android Gradle Plugin (AGP) version expected by the project,
then **build and run** the app on your device or emulator.

---

## 4. Tests & Reports

**4.1 Unit Tests**

Run JVM-based (non-device) unit tests:

```bash
./gradlew app:testDebugUnitTest
./gradlew app:testReleaseUnitTest

   OR

./gradlew app:test
```

**Results & Reports:**

* **Android Studio:** View via *Build > Test Results* or the **Run panel**.
* **HTML report:** Open this file in your browser to inspect class-wise results and stack traces.

  ```
  app/build/reports/tests/testDebugUnitTest/index.html
  ```

#
**4.2 Instrumented Tests**

Run Android instrumented tests on a connected device or emulator & keep screen visible while running — you can observe real-time interactions:

```bash
./gradlew app:connectedDebugAndroidTest

OR

./gradlew app:connectedAndroidTest
```

**Results & Reports:**

* **Android Studio:** View results in *Run > Tests in ‘connectedDebugAndroidTest’*.
* **HTML report:**

  ```
  app/build/reports/androidTests/connected/index.html
  ```
* **Logcat Output:** Detailed logs visible under the *Logcat* tab.

#
**4.3 Macrobenchmark Tests**

Handled via the **benchmark** module.

```bash
# Install benchmark build first
./gradlew :app:installBenchmark

# Run all connected benchmark tests
./gradlew :benchmark:connectedAndroidTest  OR   ./gradlew :benchmark:connectedCheck   OR   ./gradlew :benchmark:cC
```

**Results & Reports:**

* **Benchmark summary (JSON):**

  ```
  benchmark/build/outputs/connected_android_test_additional_output/
  ```
* **Per-run timing details:** Accessible under the same directory for each device and flavor.
* **Android Studio Profiler:** You can also analyze performance traces if configured with `androidx.benchmark` trace support.

---

## 5. Macrobenchmark & Baseline Profile

**5.1 Minimal Recommended Flow:**

1. **Prepare Generator Test:**
   Implement a macrobenchmark baseline generator (e.g., `BaselineProfileGenerator`) inside the **benchmark** module.

2. **Connect a Device:**
   Use a physical device (API ≥ 33 recommended) for consistent performance metrics.

3. **Generate Baseline Profile:**

   ```bash
   # Install target benchmark build
   ./gradlew :app:installBenchmark

   # Run baseline generator instrumentation test
   ./gradlew :benchmark:connectedAndroidTest \
     "-Pandroid.testInstrumentationRunnerArguments.androidx.benchmark.enabledRules=BaselineProfile"
   ```

4. **Locate the Generated File:**

   ```
   benchmark/build/outputs/managed_device_android_test_additional_output/flavors/<flavor>/<deviceName>/BaselineProfileGenerator_generate-baseline-prof.txt
   ```

5. **Copy the Generated File:**
   This project expects the packaged baseline profile (Folder & file name must match exactly) at:

   ```
   app/src/main/baselineProfiles/baseline-prof.txt
   ```

6. **Rebuild & Compare:**
   Build a **non-debuggable** variant (release or benchmark) so AGP packages the profile, then **re-run benchmarks** (check section 4.3) to compare before/after results.

#
**5.2 Packaging Notes:**

If your AGP version doesn’t auto-merge baseline profiles, manually set:

```gradle
release {
    baselineProfileFiles = files("app/src/main/baselineProfiles/baseline-prof.txt")
    ...
}
create("benchmark") {
    baselineProfileFiles = files("app/src/main/baselineProfiles/baseline-prof.txt")
    ...
}
```

#
**5.3 Verify Packaging by Checking:**

* `app/build/intermediates/merged_art_profile/<variant>/baseline-prof.txt` (check increased size & line count)
* Inside built APK/AAB under `assets/` for `dexopt / baseline.prof / baseline.profm`(check increased size).

#
**5.4 Verification & Debugging Commands:**

For accurate performance benchmarking, the target app must always be non-debuggable.

1. **Prepare Generator Test:**   
   While running benchmarks (both before & after applying the baseline profile), Logcat may display the warning: `Targeted app is debuggable`.
   This indicates the app is built in debug mode, which can produce inaccurate benchmark results.

3. **Check Installed Packages & Build Type:**

```bash
adb shell pm list packages | grep newsroom
adb shell dumpsys package com.example.newsroom | grep debuggable
adb shell dumpsys package com.example.newsroom.benchmark | grep debuggable
```

* `debuggable=true` → Debuggable build (not suitable for benchmarking)
* Blank/no output → Not debuggable build (release or benchmark variant, suitable for benchmarking)

---

## 6. Baseline Profile Results

> **Note**: negative % = improvement (lower times / lower variability), positive % = regression (higher times / more variability).

**Summary Table (Key Metrics)**

| **Benchmark**                      | **Metric**                       | **Before BP** | **After BP** | **Change**                          |
| ---------------------------------- | -------------------------------- | ------------- | ------------ | ----------------------------------- |
| **Feed Scroll** (`scrollNewsList`) | frameCount (median)              | 333.0         | 326.5        | **-1.95%**                          |
|                                    | frameCount (CoV)                 | 0.0524        | 0.0438       | **-16.3% (Improved consistency)**   |
|                                    | Total run time (s)               | 301.36 s      | 287.95 s     | **-4.45%**                          |
|                                    | FrameDurationCpuMs P50 (ms)      | 12.43         | 12.37        | **-0.48%**                          |
|                                    | FrameDurationCpuMs P90 (ms)      | 15.69         | 15.41        | **-1.78%**                          |
|                                    | FrameDurationCpuMs P95 (ms)      | 17.05         | 16.68        | **-2.17%**                          |
|                                    | FrameDurationCpuMs P99 (ms)      | 24.72         | 24.29        | **-1.74%**                          |
| **Theme Switch** (`switchThemes`)  | frameCount (median)              | 106.0         | 108.0        | **+1.89%**                          |
|                                    | frameCount (CoV)                 | 0.0903        | 0.0744       | **-17.6% (Improved consistency)**   |
|                                    | Total run time (s)               | 217.71 s      | 207.93 s     | **-4.49%**                          |
|                                    | FrameDurationCpuMs P50 (ms)      | 13.02         | 13.57        | **+4.22% (Regression)**             |
|                                    | FrameDurationCpuMs P90 (ms)      | 27.52         | 22.44        | **-18.46% (Big improvement)**       |
|                                    | FrameDurationCpuMs P95 (ms)      | 33.74         | 30.50        | **-9.59%**                          |
|                                    | FrameDurationCpuMs P99 (ms)      | 103.65        | 98.59        | **-4.88%**                          |
| **Startup — Hot** (`startupHot`)   | timeToInitialDisplay (min ms)    | 105.75        | 109.13       | **+3.20%**                          |
|                                    | timeToInitialDisplay (median ms) | 116.49        | 116.63       | **+0.12% (Negligible)**             |
|                                    | timeToInitialDisplay (max ms)    | 129.81        | 126.97       | **-2.19%**                          |
|                                    | CoV                              | 0.1174        | 0.0682       | **-41.91% (Much more consistent)**  |
|                                    | Total run time (s)               | 105.09 s      | 99.87 s      | **-4.97%**                          |
| **Startup — Cold** (`startupCold`) | timeToInitialDisplay (min ms)    | 964.95        | 912.00       | **-5.49%**                          |
|                                    | timeToInitialDisplay (median ms) | 1079.63       | 1088.35      | **+0.81% (Small regression)**       |
|                                    | timeToInitialDisplay (max ms)    | 1407.43       | 1561.41      | **+10.94%**                         |
|                                    | CoV                              | 0.0881        | 0.2401       | **+172.53% (Much less consistent)** |
|                                    | Total run time (s)               | 85.92 s       | 115.22 s     | **+34.11% (Regression)**            |
| **Startup — Warm** (`startupWarm`) | timeToInitialDisplay (min ms)    | 358.97        | 344.09       | **-4.14%**                          |
|                                    | timeToInitialDisplay (median ms) | 403.80        | 470.87       | **+16.61% (Regression)**            |
|                                    | timeToInitialDisplay (max ms)    | 436.96        | 664.85       | **+52.17%**                         |
|                                    | CoV                              | 0.0871        | 0.1451       | **+66.59% (Less consistent)**       |
|                                    | Total run time (s)               | 86.39 s       | 110.06 s     | **+27.40% (Regression)**            |


**Interpretation:**

* **Scroll & Theme Switch (Frame Metrics):** There are notable improvements in the consistency (lower CoV) & the top-end frame times (P90, P95, P99). Specifically, the P90 for Theme Switch saw an -18.46% improvement, suggesting Baseline Profiles successfully reduced jank in that critical path.

* **Startup — Cold (Time Metrics):** While the median saw a slight regression (+0.81%), the minimum startup time saw a good -5.49% improvement, indicating the potential for a faster start under ideal conditions. However, the max time & CoV both increased significantly.

* **Startup — Hot (Time Metrics):** The median startup time remained nearly identical (+0.12%), but the metric became much more stable, with a -41.91% decrease in the Coefficient of Variation (CoV). This is a strong positive result, showing consistent, predictable performance.

* **Startup — Warm & Total Run Time (for Cold/Warm):** The Total Run Time and Warm Startup performance appear to have regressed significantly, which could be due to increased overhead during the benchmark process itself, or environmental factors (thermal throttling, noise, etc.) that affected these specific runs.

---

## 7. CI Workflow
* The current CI configuration `.github/workflows/android-ci.yml` runs only unit tests by default.
* Instrumented UI tests are included but commented out to keep builds lightweight.
* You can uncomment those sections anytime to enable full instrumentation testing as well on the CI runner.

* If you want CI (GitHub Actions) to run instrumented tests, benchmarks, or assemble signed release/benchmark APKs, add these **five** secrets to your GitHub repo (Settings → Secrets & variables → Actions):

1. `NEWS_API_KEY` — your `news.api.key` (e.g. `123456789`) used to call [https://newsapi.org](https://newsapi.org).
2. `RELEASE_KEYSTORE_BASE64` — base64-encoded `release-keystore.jks` (decode in workflow to a file).
3. `RELEASE_STORE_PASSWORD` — keystore password (e.g. `123456789`).
4. `RELEASE_KEY_ALIAS` — key alias (e.g. `myappkey`).
5. `RELEASE_KEY_PASSWORD` — key password (can match store password if desired).

---
