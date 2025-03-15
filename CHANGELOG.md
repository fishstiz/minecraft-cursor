- Updated German Translations. ([@Lucanoria](https://github.com/fishstiz/minecraft-cursor/pull/18))
- Fixed misaligned scroll in cursor list.
- Fixed soft lock if image size is invalid and logs the error (The texture will still be messed up)  [#19](https://github.com/fishstiz/minecraft-cursor/issues/19).
- API Changes:
    - Fixed being able to register and override existing cursor keys.
    - Throw error if attempting to register cursor type with empty key
- Updated `fabric.mod.json` Metadata.
- Fabric Only Changes: 
    - Minor optimizations to external cursor tracking
    - Added compatibility with other mods that uses custom cursors.
        - If another mod sets a custom cursor, Minecraft Cursor will yield control of the cursor. The adaptive feature won't work while a custom external cursor is in use (it barely worked before anyway, was unpredictable, and often caused flickering).
        - This is to support mods that only used custom cursors on certain contexts without globally overriding the cursor. (e.g., **FancyMenu**).
    - Added logs if a mod is using a standard cursor or if a custom cursor has been detected.
    - **Note**: Standard cursor remapping is also a Fabric only feature as I've just found out.
    <br>  
      Unfortunately NeoForge/Forge just doesn't allow or have proper support for this due to the way they load libraries. 
    <br><br>
      These mod compatbility features technically exist in the Forge & Neoforge versions of Minecraft Cursor but they only lay dormant. If for some reason they change the way libraries are loaded that will support this feature, it should automatically work.

### The Minecraft Cursor Wiki has also been revamped 🎉
### Check it out: https://fishstiz.github.io/minecraft-cursor-wiki/
<br>
Please do not hesitate to report issues or leave feedback, it really helps.
