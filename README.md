# Minecraft Cursor

A Fabric mod that replaces the boring old default system cursor with a Minecraft-themed cursor.

## Requirements
- Fabric for Minecraft 1.21.4
- Compatible with [Mod Menu](https://modrinth.com/mod/modmenu)

## Multiple Cursors
![default](https://github.com/user-attachments/assets/6c632b54-e284-47a0-8634-f4ba1ef03f29)
![pointer](https://github.com/user-attachments/assets/83a41d81-5a0b-4399-8d70-61ca421117c0)
![text](https://github.com/user-attachments/assets/049fc447-6f3f-4c7a-a0a2-b87d0348c593)

## Configure
- Configure from Mod Menu (if installed)
- Configure from Options > Controls > Mouse Settings... > Cursor Settings...

<img src="https://github.com/user-attachments/assets/c5626cec-f332-40d2-bcd1-6103f7fca745" style="width:600px;"/>

## Resource Pack Support
### Image Format
- 32x32 pixels
- png

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
### cursors.json  
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
**Properties**:
- `enabled`: `true`/`false`
- `scale`: `1.00` - `3.00` (incrementing in 0.25)
- `xhot`: `0` - `31`
- `yhot`: `0` - `31`
    
