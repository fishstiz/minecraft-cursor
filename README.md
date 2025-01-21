# Minecraft Cursor

A Fabric mod that replaces the boring old default system cursor with a Minecraft-themed cursor.

## Requirements
- Fabric for Minecraft 1.21.4
- Compatible with [Mod Menu](https://modrinth.com/mod/modmenu) (optional)

## Multiple Cursors
![default](https://github.com/user-attachments/assets/6c632b54-e284-47a0-8634-f4ba1ef03f29)
![pointer](https://github.com/user-attachments/assets/83a41d81-5a0b-4399-8d70-61ca421117c0)
![text](https://github.com/user-attachments/assets/049fc447-6f3f-4c7a-a0a2-b87d0348c593)

- Context-aware cursors when hovering over buttons and text fields (limited to common GUI elements only)
- If you think an element should or shouldn't change the cursor's state then open an issue and I might look into it.
- Mods that shift the positions of the in-game GUIs may lead to unexpected state changes with the cursor. 

  Unfortunately, most elements from in-game GUIs are not actually elements and are drawn directly on the screen, with their sizes and positions obfuscated and/or hardcoded

## Configure Cursors
- Configure from Mod Menu (if installed)
- Configure from Options > Controls > Mouse Settings... > Cursor Settings...

<img alt="options-menu" src="https://github.com/user-attachments/assets/c5626cec-f332-40d2-bcd1-6103f7fca745" style="width:600px;"/>

## Resource Pack Support
### Image Format
- 32x32 pixels
- png format

### File Structure
```
└── minecraft-cursor/
    ├── atlases/
    │   └── cursors.json
    └── textures/
        └── cursors/
            ├── default.png
            ├── pointer.png
            └── text.png
```
### Custom Configuration 
- Define a custom configuration for your resource pack in `atlases/cursors.json`. 
- Can be overridden by users through the Cursor Settings menu

**Example**:
```json
{
  "settings": {
    "default": {
      "yhot": 3
    },
    "pointer": {
      "xhot": 7,
      "yhot": 3,
      "scale": 1.25
    },
    "text": {
      "xhot": 12,
      "yhot": 3
    }
  }
}
```
**Settings**:
- `enabled`: `true`/`false`
- `scale`: `0.50` - `3.00` (incrementing in 0.05)
- `xhot`: `0` - `31`
- `yhot`: `0` - `31`

<h2>Mod Compatibility</h2>
  <h4>
    Widgets are automatically registered by this mod with the following conditions:&nbsp;
  </h4>
  <ul>
    <li>Pointer elements must be an instance of <code>PressableWidget</code>
        or <code>SliderWidget</code>
    </li>
      <ul>
        <li>
          <code>active</code> and <code>visible</code> must be <code>true</code>
        </li>
      </ul>
    <li>Text elements must be an instance of <code>TextFieldWidget</code>
      <ul>
        <li><code>visible</code> must be <code>true</code></li>
      </ul>
    </li>
    <li>They must be a child of <code>ParentElement</code> (e.g. <code>Screen</code>), 
      accessible through <code>children()</code> method
    </li>
    <li>Containers must be an instance of <code>ParentElement</code>
      and nested containers must be an instance and child of <code>ParentElement</code>
    </li>
   </ul>
  <a href="https://github.com/fishstiz/minecraft-cursor/blob/master/src/client/java/io/github/fishstiz/minecraftcursor/registry/CursorTypeRegistry.java" target="_blank">See More</a>

    
