- Increased maximum texture size to **128x128** in steps of **8**. Minimum size remains **8x8**.
  <br>
  Recommended texture size is 2<sup>n</sup> with custom scale or 16x16 with auto-scale enabled.
- Changed element detection logic (again). Inactive vanilla widgets are now detected.
- Registered disabled vanilla buttons and sliders with the **Not Allowed** cursor. Can be disabled. ([#31](https://github.com/fishstiz/minecraft-cursor/issues/31))
- Overhauled the **Cursor Settings** screen.
  - Added sidebar with sections: **Global**, **Adaptive Cursor**, **Cursors**, **Mod Compatibility**, and **Debug**.
  - Added search functionality.
  - **Adaptive Cursor**: Added option to toggle **Not Allowed** cursor on disabled widgets.
  - **Cursors**: Added **Reset to Defaults** option.
  - **Mod Compatibility**: Added option to aggressively update the cursor.
  - Removed More Cursor Options screen (consolidated to Cursor Settings screen)
- Improved performance of `random_cycle` animation mode.
- Fixed cursor not being destroyed when switching to resource packs with an invalid cursor image.
- Fixed the cursor reverting to **Default** when holding **right shift** on inventory items if the **Shift** cursor was disabled.

For Fabric versions:
- Fixed custom cursors from other mods not being detected unless a standard cursor was also created.
- Fixed cursor reload (via resource reload or changing cursor settings) bypassing the custom cursor check.
  <br>
  The expected behavior is that the Adaptive Cursor is disabled when a custom cursor is present to improve compatibility with mods that use custom cursors like FancyMenu.