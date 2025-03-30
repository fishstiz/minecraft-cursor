**Fabric changes**:
- Fixed some mods resetting the cursor back to system cursor (e.g., owo-lib resetting the cursor when opening the creative inventory [#26](https://github.com/fishstiz/minecraft-cursor/issues/26)).
- Shortened warning when Early Loading Screen mod is installed.

**Note**: Sorry **Neoforge** & **Forge** users, I tried to bring at least some of these compat features over there, 
but seems like I cannot even transform non-Mojang libraries (with Mixins) like **ImGui**.

For context, ImGui is the usual suspect for why the cursor is not changing or is being reset, as it is ran unconditionally by a lot of mods that use it. 
