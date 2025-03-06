- Extended compatibility to mods that also change the cursor to the [standard cursors (GLFW)](https://www.glfw.org/docs/latest/group__shapes.html).
  - Mods that use custom cursors (without the Minecraft Cursor API) may still have compatibility issues. 
- Remapped standard cursors to the equivalent Minecraft Cursor.
- Added the following **unused** cursors, which will only be used if other mods exist that utilize the standard GLFW cursors:
  - Crosshair ![crosshair](https://github.com/user-attachments/assets/523bb588-a839-4e82-b09a-6b00d1564362)
  - Resize EW ![resize_ew](https://github.com/user-attachments/assets/c83f1b8c-c50a-46ba-b9ee-5b30aec64dba)
  - Resize NS ![resize_ns](https://github.com/user-attachments/assets/f02bef60-8cdc-4bc1-b193-84cbd8dd3522)
  - Resize NWSE ![resize_nwse](https://github.com/user-attachments/assets/8cdad669-3294-496e-8168-680f10052db7)
  - Resize NESW ![resize_nesw](https://github.com/user-attachments/assets/843b09e1-a4cc-47a9-97ad-b619a252bc4f)
  - Not Allowed ![not_allowed](https://github.com/user-attachments/assets/5c4628ab-67dc-42d2-a25e-ca66014e1a9c)
- Added option to toggle standard cursor remapping (disabling may cause compatibility issues with certain mods).

This update addresses an [issue](https://github.com/fishstiz/minecraft-cursor/issues/15) reported by a user where the [Effective](https://modrinth.com/mod/effective) mod, which should seemingly not have compatibility issues, became incompatible.

The cause is the [Veil](https://github.com/FoundryMC/Veil) library, which **Effective** integrates. **Veil**, in turn, integrates [ImGui](https://github.com/SpaiR/imgui-java), which does have its own cursor management using the standard GLFW cursors. Since **Veil** runs **ImGui** immediately and persistently, it overrides Minecraft Cursor completely, causing the compatibility issue.

**Veil** is a rendering library, other mods that rely on it for rendering may have also experienced similar compatibility issues. With this update, those mods, along with **Effective**, should now be compatible (hopefully).