
<h1 align="center">FPS Monitor</h1>

<p align="center">
A lightweight Android application to monitor real-time FPS and RAM usage with customizable floating overlay.
</p>

![Image](https://raw.githubusercontent.com/MSI-Sirajul/FPS-Monitor/refs/heads/main/build-upload/20250717_000824.png)
---

<h2 align="center">Features</h2>

- ✅ FPS & RAM Monitoring: `Show real-time FPS and RAM usage as a floating overlay`
  
- ⚙️ Customizable Overlay Settings: `Adjust overlay size and transparency via sliders.`
  
- ✅ Individual Toggles: `Enable/disable FPS and RAM overlays using checkboxes.`
  
- 🎨 Background Toggle: `Show or hide the overlay background with a checkbox.`
  
- 🔐 Overlay Permission Handler: `First-time users are redirected to grant overlay permission automatically.`
  
- 🔄 SharedPreferences Integration: `All user preferences (toggle, size, opacity, background) are saved and restored.`
  
- 🧲 Quick Settings Tile: `Instantly toggle the overlay from Android's Quick Settings panel.`
  
- 🔧 Foreground Service: `Overlay is managed via a foreground service for persistent display.`
  
- 💡 Minimalist UI: `Clean and user-friendly Material-style design.`
  
- 🏁 Boot Ready: `RECEIVE_BOOT_COMPLETED` permission included for future auto-start capabilities.


---

<h2 align="center">Technical Stack</h2>

<div align="justify">

- Language: Java (Android SDK)
- UI Components: `XML Layouts` `CheckBox` `SeekBar` `TextView`
- Storage: `SharedPreferences`
- Services: `Foreground Service`, Runtime Permission Handler
- Quick Settings Integration: TileService with QS Tile support
- OverlayType: `TYPE_APPLICATION_OVERLAY`
- Permissions Used:
  - `SYSTEM_ALERT_WINDOW`
  - `FOREGROUND_SERVICE`
  - `RECEIVE_BOOT_COMPLETED`
  - `BIND_QUICK_SETTINGS_TILE`

</div>

---

<h2 align="center">🆕 Recent Updates</h2>

<div align="justify">

- ➕ Added: `Background` toggle in settings UI
  
- 🔧 Implemented: Logic to show/hide overlay background
  
- 🛡️ Improved: Runtime permission handler via new `StartupService`
  
- ✅ Enhanced: Startup flow to redirect to overlay permission if not granted
  
- 💾 Optimized: Preferences for overlay background saved using `SharedPreferences`

</div>

---

<h2 align="center">Project Structure</h2>

```
📦 com.view.fps
 ┣ 📂 MainActivity.java
 ┣ 📂 SettingsActivity.java
 ┣ 📂 StartupService.java
 ┣ 📂 FpsTileService.java
 ┣ 📜 AndroidManifest.xml
 ┣ 📜 activity_settings.xml
 ┣ 📜 strings.xml
 ┣ 📜 colors.xml (future use)
```

---

<h2 align="center">Optimization Tips</h2>

<div align="justify">

- Remove unused resources (images, layouts, strings)
  
- Use ProGuard/R8 for minifying and shrinking code
  
- Use vector images instead of raster for smaller APK size
  
- Compress PNGs or use WebP format where applicable
  
- Build in release mode with `zipalign` and `apksigner` for final packaging

</div>

---

<h2 align="center">Developer</h2>

<p align="center">
![MD Sirajul Islam]()
</p>

---

<h2 align="center">📜 License</h2>

<p align="center">
This project is for personal and educational use only. All rights reserved.
</p>

