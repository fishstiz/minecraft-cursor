# Minecraft Cursor

A Fabric mod that replaces the boring old default system cursor with Minecraft-themed cursors.

## 📌 Requirements
- Minecraft 1.20.1 or 1.21.1+

## 🔄 Adaptive Cursor
![default](https://github.com/user-attachments/assets/6c632b54-e284-47a0-8634-f4ba1ef03f29)
![pointer](https://github.com/user-attachments/assets/83a41d81-5a0b-4399-8d70-61ca421117c0)
![grabbing](https://github.com/user-attachments/assets/bdcd6392-a8bb-40af-b2fa-10a465363545)
![text](https://github.com/user-attachments/assets/049fc447-6f3f-4c7a-a0a2-b87d0348c593)
![shift](https://github.com/user-attachments/assets/27f97a5c-be91-45c9-ad5d-91a5e162fb50)
![busy](https://github.com/user-attachments/assets/2b4e338a-7068-4998-8f79-e7ccfc3a97fa)

- The cursor adapts to certain actions and elements (limited to Minecraft's native elements).
- Disable all cursors except for Default in the cursor settings to disable this feature,
  - You can also toggle all non-default cursors in one click from More Cursor Options
  - You can disable adaptive cursors for certain actions in More Cursor Options
- Mods that shift the sizes and positions of certain GUIs may lead to unexpected cursor switching.
  - You can disable adaptive cursors for affected GUIs in More Cursor Options.

## 🔧 Configure Cursors
- Configure from Mod Menu (if installed on Fabric)
- Configure from Options > Controls > Mouse Settings... > Cursor Settings...
- Configure More Cursor Options from Cursor Settings > More Cursor Options...
  - Override the Scale, Offset X, and Offset Y of all cursors.
  - Disable Adaptive Cursor for certain actions and elements.
- Disabled cursors will fallback to the Default cursor unless it is also disabled.

<p>
<img alt="options" src="https://github.com/user-attachments/assets/6f8ca20a-5950-4d7d-ae0f-9a27996190a6" width="400"/>
<img alt="options" src="https://github.com/user-attachments/assets/2846a7d5-1834-4525-8e14-50efadfe2ac3" width="400"/>
<img alt="more-options" src="https://github.com/user-attachments/assets/82d876a9-2bd6-4354-9332-73367205ad36" width="400"/>
<img alt="more-options" src="https://github.com/user-attachments/assets/b640fa4b-1cb6-4980-8c94-e6584fcaad06" width="400"/>
</p>

## 🎨 Resource Pack Support

Create your own theme, customize and animate cursor designs, and configure cursor settings with resource packs.

Visit the Minecraft Cursor Wiki for more details: https://github.com/fishstiz/minecraft-cursor/wiki/Resource-Pack

## 🚀 Java API
The Minecraft Cursor mod provides an API for mod developers to:
- Create new cursors.
- Map elements with cursor type functions.
  - or declare the cursor type within the element itself.
- Directly change the cursor, bypassing the element-based system.

Visit the Minecraft Cursor Wiki for more details: https://github.com/fishstiz/minecraft-cursor/wiki/Java-API

JavaDocs: https://minecraft-cursor.bitbucket.io

## 🔗 Mod Compatibility
Minecraft Cursor does not use any custom rendering. It simply changes the look of the native cursor through GLFW. Input modes and other mouse behavior are left untouched.

Widgets are automatically detected (and registered) with the following conditions:
> **Note**: These are in yarn mappings.

- Pointer elements must be an instance of `PressableWidget`.
- Slider elements must be an instance of `SliderWidget`.
- Text elements must be an instance of `TextFieldWidget`.
- They must be a child of `ParentElement` (e.g., `Screen`), accessible through the `ParentElement#children()` method.
- Nested elements must be recursively accessible from the screen using the `ParentElement#children()` method.
- More on the source code [here](https://github.com/fishstiz/minecraft-cursor/blob/master/src/main/java/io/github/fishstiz/minecraftcursor/impl/MinecraftCursorInitializerImpl.java).

The adaptive cursor can be disabled on these non-element "elements" in case of unexpected cursor switching:
> **Note**: These are in yarn mappings.

- `CreativeInventoryScreen` tabs
- `EnchantmentScreen` choices
- `StonecutterScreen` recipes
- `BookEditScreen` book
- `LoomScreen` patterns
- `AdvancementsScreen` tabs
- `WorldListWidget` world icon play button
- `MultiplayerServerListWidget` server icon play button
