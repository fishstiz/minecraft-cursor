# Minecraft Cursor

A Fabric mod that replaces the boring old default system cursor with a Minecraft-themed cursor.

## Requirements
- Fabric for Minecraft 1.21.2-1.21.4
- Compatible with [Mod Menu](https://modrinth.com/mod/modmenu) (optional)

## Multiple Cursors
![default](https://github.com/user-attachments/assets/6c632b54-e284-47a0-8634-f4ba1ef03f29)
![pointer](https://github.com/user-attachments/assets/83a41d81-5a0b-4399-8d70-61ca421117c0)
![text](https://github.com/user-attachments/assets/049fc447-6f3f-4c7a-a0a2-b87d0348c593)

- Context-aware cursors when hovering over buttons and text fields (limited to common GUI elements only)

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
    
