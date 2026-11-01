# 资产每日平摊（AssetDailyCost）

一个安卓端的小工具：记录物品、购买日期和购买金额，自动算出「截至今天，这件东西每天花了多少钱」。

已构建好的安装包在 [GitHub Releases](https://github.com/Kirara0/asset-daily-cost/releases)（`AssetDailyCost-v1.0.apk`），直接下载安装即可；应用完全离线，数据只保存在手机本地。

## 功能

- **记录物品**：名称 + 购买日期 + 购买金额，用 Material 3 的日期选择器选日期
- **每日平摊**：`每日平摊 = 购买金额 ÷ 已用天数`，已用天数 = 购买当天到今天的天数（含购买当天，最少 1 天）
- **列表汇总**：物品数量、购买总额、每日合计、平均已用天数
- **成本对比**：横向柱状条按每日成本排序，一眼看出哪件东西最贵
- **本地存储**：Room 数据库（`asset_daily.db`），关掉应用数据不丢
- **其它**：单条删除、一键清空（带确认弹窗）、按当天重新计算、深色模式、边到边显示

## 技术栈

| 项目 | 版本 |
| --- | --- |
| 语言 | Kotlin 2.0.21 |
| 界面 | Jetpack Compose + Material 3（Compose BOM 2024.12.01） |
| 数据库 | Room 2.6.1（KSP 2.0.21-1.0.28） |
| 构建 | AGP 8.7.3 / Gradle 8.9 |
| SDK | compileSdk 35、targetSdk 35、minSdk 26 |
| 架构 | 单 Activity + ViewModel + StateFlow + Repository |

minSdk 26 是为了直接使用 `java.time`，不需要额外开核心库脱糖。

## 目录结构

```
asset-daily-cost/
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
├── gradle/
│   ├── libs.versions.toml          # 版本统一管理
│   └── wrapper/gradle-wrapper.properties
└── app/
    ├── build.gradle.kts
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml
        │   ├── java/com/assetcalc/app/
        │   │   ├── MainActivity.kt          # 入口，组装 Repository / ViewModel
        │   │   ├── data/
        │   │   │   ├── AssetItem.kt         # Room 实体
        │   │   │   ├── AssetDao.kt          # 增删查（Flow）
        │   │   │   ├── AppDatabase.kt       # Room 数据库单例
        │   │   │   └── AssetRepository.kt
        │   │   ├── domain/
        │   │   │   └── Amortization.kt      # 纯计算逻辑（可单测）
        │   │   └── ui/
        │   │       ├── AssetViewModel.kt
        │   │       ├── AssetScreen.kt       # 汇总卡片 / 录入 / 明细 / 柱状对比
        │   │       └── theme/               # Color / Type / Theme
        │   └── res/                         # 图标、主题、字符串
        └── test/java/com/assetcalc/app/
            └── AmortizationTest.kt          # 计算规则单元测试
```

## 计算规则

```
已用天数 = (今天 - 购买日期) 的天数 + 1   // 含购买当天，最少 1 天
每日平摊 = 购买金额 / 已用天数
每日合计 = Σ 每件物品的每日平摊
```

以「统计日期 = 2026-11-01」为例（`AmortizationTest` 断言的也是这组数）：

| 物品 | 购买日期 | 金额 | 已用天数 | 每日平摊 |
| --- | --- | --- | --- | --- |
| 笔记本电脑 | 2024-06-15 | ¥8,999.00 | 870 | ¥10.34 |
| 手机 | 2025-01-20 | ¥4,999.00 | 651 | ¥7.68 |
| **合计** | | **¥13,998.00** | 平均 761 | **¥18.02** |

> 应用里用的是设备当天的真实日期（`LocalDate.now()`），所以打开 App 时显示的是「截至今天」的数字，会随天数增长而变小；右上角刷新按钮可以重新取一次日期。

## 下载安装

1. 打开 [GitHub Releases](https://github.com/Kirara0/asset-daily-cost/releases)，下载 `AssetDailyCost-v1.0.apk`。
2. 把 APK 传到安卓手机（Android 8.0 / API 26 及以上），点击安装。
3. 系统提示“未知来源”时，允许当前文件管理器安装即可。

应用不联网、不需要服务器，数据保存在应用私有的 Room 数据库里，卸载应用会一起删除。

## 怎么运行

1. 安装 **Android Studio**（Ladybug 2024.2.1 或更新版本，内置 JDK 17+）。
2. `File → Open`，选择 `asset-daily-cost` 目录（不是里面的 `app`）。
3. 等待 Gradle Sync 完成（首次会自动下载 Gradle 8.9、AGP、Compose、Room 依赖）。
4. 连上安卓手机（开启开发者模式 + USB 调试）或启动模拟器，点 Run ▶。

如果 IDE 提示缺少 `gradle/wrapper/gradle-wrapper.jar`：
- 在 Android Studio 里执行一次 `File → Sync Project with Gradle Files`，它会自动补齐；
- 或者本机装了 Gradle 时，在项目根目录执行 `gradle wrapper --gradle-version 8.9` 生成 wrapper。

## 打包 APK

环境：JDK 17+（本项目用 JDK 21 验证）、Android SDK Platform 35、Build-Tools 35.0.0、Gradle 8.9。

```bash
# 调试包，产物在 app/build/outputs/apk/debug/app-debug.apk
./gradlew assembleDebug

# 单元测试
./gradlew testDebugUnitTest

# 未签名的发布包，产物在 app/build/outputs/apk/release/app-release-unsigned.apk
./gradlew assembleRelease
```

Windows 下把 `./gradlew` 换成 `gradlew.bat`，并用 `set JAVA_HOME=C:\path\to\jdk-21` 指定 JDK。

发布包需要签名（keystore 不进仓库，请使用自己的密钥）：

```bash
BT=$ANDROID_HOME/build-tools/35.0.0

# 1. 对齐
$BT/zipalign -p -f 4 app-release-unsigned.apk aligned.apk

# 2. 签名（v2 + v3）
java -jar $BT/lib/apksigner.jar sign \
  --ks release.keystore --ks-key-alias assetcalc \
  --ks-pass pass:你的密码 --key-pass pass:你的密码 \
  --out AssetDailyCost-v1.0.apk aligned.apk

# 3. 校验
java -jar $BT/lib/apksigner.jar verify --verbose AssetDailyCost-v1.0.apk
```

> 注意：PATH 里默认的 `java` 可能是 JDK 8，读不了新版 PKCS12 keystore，会报 `Invalid keystore format`。请显式使用 JDK 17+ 的 `java` 执行 `apksigner.jar`，或先把 `JAVA_HOME` 指向 JDK 21。

## 测试

```bash
./gradlew testDebugUnitTest
```

`AmortizationTest` 覆盖：含购买当天的天数、当天购买=1 天、购买日期在未来时按 1 天、示例数据的每日平摊与合计、金额格式化。

## 说明

- 首次安装是空列表，需要自己点“添加”录入第一件物品；应用没有内置示例数据。
- 金额按「元」以 `Double` 存储，显示时统一保留两位小数；如果要做严格记账，可以改成以分为单位的 `Long`。
- 数据存在应用私有数据库，卸载应用会一起删除；已开启系统自动备份（`backup_rules.xml` / `data_extraction_rules.xml`）。
- 版本 1.0（`versionCode 1`），minSdk 26、targetSdk 35；后续升级请使用同一签名密钥，否则需要先卸载旧版。

## 构建状态

- 2026-11-01：`assembleDebug`、`testDebugUnitTest`（6/6）、`assembleRelease` 均通过。
- 发布包：`dist/AssetDailyCost-v1.0.apk`（6.33 MB），APK Signature Scheme v2 + v3 校验通过，zipalign 校验通过。
- SHA-256：`CC4423AA84B448A25E89239E7B8CC8E2B6F5308E15C0CA36211CF37ED0370352`
