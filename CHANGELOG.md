- Increased scale cap to 8.00
- Explicitly disable anti-aliasing and use nearest-neighbor when scaling which may or may not fix blurriness on some platforms.
- Improved logs

**Fabric changes**:
- Added `config/minecraft-cursor.properties` Launch Properties. Can only be edited through the file itself.
    - `ignore_mod_check_glfw_mixin`: Ignores the mod check to apply the `GlfwMixin`. Attempts to apply `GlfwMixin` even when Early Loading Screen mod is installed, so you can set `window_creation_point` to `preLaunch` to prevent the crash and also apply the mixin.
    - `force_disable_glfw_mixin`: Forcefully disables `GlfwMixin`. Workaround to fix crashes if GLFW is early loaded. Please report an issue still if this happens.
- **Why opt out instead of opt in?** There are more mods that use GLFW cursors, more than it may seem, than there are mods that early load GLFW. For example, Do A Barrel Roll mod, has seemingly nothing to do with cursors, but conflicts with Minecraft Cursor because of its dependency on CICADA. The `GlfwMixin` is able to fix this.
    - The crash will happen on startup if a mod does early loads GLFW, so it will be immediately known.
    - This was also already a thing in v3.5.0, this explanation is just to provide some context for these new properties.
- This is not a Fabric bias, it's just not possible to do this in **Forge** & **NeoForge** 😔

If you have any issues and/or suggestions, head on over to the issues page.