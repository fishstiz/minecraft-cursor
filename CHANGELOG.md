**For all versions:**
- Cursors can now use **8x8**, **16x16**, **48x48**, or **64x64** textures in addition to **32x32**.
- Added option to defer loading of disabled cursors on the next launch of Minecraft until they’re enabled.  
  Mostly negligible impact; mainly intended for users who disable adaptive cursor.
- Registered vanilla **Multiline Edit Box** with **Text** cursor.
- Applied default cursor when dragging the scrollbar on some scrollable areas.
- Fixed hotspot widget applying when it was the last focused widget and the mouse was dragged from outside to inside its bounds.
- Fixed animation also getting disabled when the cursor is disabled.

**For Minecraft version 1.20.1:**
- Fixed hotspot widget not applying when releasing the mouse outside the widget's bounds.

**For Fabric versions:**
- Fixed rare concurrency issue where the adaptive cursor does not work until restarting if other mods are also creating cursors.
