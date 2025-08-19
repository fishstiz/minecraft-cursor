- Added **Refresh Button** to Configuration Screen to quickly reload cursor textures without reloading all resources.
- Added **Virtual Mode** option under **Mod Compatibility** to toggle virtual cursor mode. Support for this will be
  limited.
  It is only intended to work around issues you may have with native cursor mode and could introduce other issues.
  Results may vary.
- Reduced false positives on adaptive cursor computation with overlapping elements.
- Made option **Remap System Cursors** always available to toggle unless disabled by another mod (Fabric) or unavailable
  by default (Forge/NeoForge).

#### NeoForge
- Added compatibility with **owo-lib**. Cursors from owo-lib will be remapped to the Minecraft Cursor equivalents.
  Can be toggled with the **Remap System Cursors** option under **Mod Compatibility**.
  Fixes ([#38](https://github.com/fishstiz/minecraft-cursor/issues/38))