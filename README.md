# Minecraft Cursor

Replaces the boring old default system cursor with Minecraft-themed cursors.

## 📌 Requirements
- Minecraft 1.20.1 or 1.21.1+
- When on Fabric: Fabric API

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
  - You can disable adaptive cursors for certain actions and elements in **More Cursor Options**.

## 🔧 Configure Cursors
- Configure from **Mod Menu** (if installed on Fabric)
- Configure from **Options** > **Controls** > **Mouse Settings**... > **Cursor Settings**...
- Configure **More Cursor Options** from **Cursor Settings** > **More Cursor Options**...
  - Override the Scale, Offset X, and Offset Y of all cursors.
  - Disable Adaptive Cursor for certain actions and elements.
- Disabled cursors will fallback to the Default cursor unless it is also disabled.

<p>
<img alt="options" src="https://github.com/user-attachments/assets/6f8ca20a-5950-4d7d-ae0f-9a27996190a6" width="49%"/>
<img alt="options" src="https://github.com/user-attachments/assets/2846a7d5-1834-4525-8e14-50efadfe2ac3" width="49%"/>
<img alt="more-options" src="https://github.com/user-attachments/assets/82d876a9-2bd6-4354-9332-73367205ad36" width="49%"/>
<img alt="more-options" src="https://github.com/user-attachments/assets/b640fa4b-1cb6-4980-8c94-e6584fcaad06" width="49%"/>
</p>

## 🎨 Resource Pack Support

With the use of resource packs, you can create custom cursors, even animate them, and define the settings of each one.

Visit the Minecraft Cursor Wiki for more details: https://fishstiz.github.io/minecraft-cursor-wiki/resource-pack/getting-started

Here's just one example: 
<a href="https://modrinth.com/resourcepack/bedrock-style-cursors" target="_blank">
  <img src="https://cdn.modrinth.com/data/dUA5FyFq/images/28f9504fa044c59491a1555494b3e579eec186a1.png" width="100%" />
</a>

## 🚀 Java API
The Minecraft Cursor mod provides an API for mod developers to:
- Create new cursors.
- Map elements with cursor type functions.
  - or declare the cursor type within the element itself.
- Directly change the cursor, bypassing the element-based system.

Can be an optional dependency. See a simple implementation [here](https://github.com/fishstiz/packed_packs/blob/master/src/main/java/io/github/fishstiz/packed_packs/compat/minecraftcursor/PackedPacksMinecraftCursor.java#L8).

Visit the Minecraft Cursor Wiki for more details: https://fishstiz.github.io/minecraft-cursor-wiki/java-api/introduction

## 🔗 Mod Compatibility

Mods that shift the sizes and positions of certain GUIs may lead to unexpected cursor switching.
Adaptive cursor can be disabled on affected GUIs from **More Cursor Options**.

The list of registered vanilla widgets with an associated cursor are found [here](https://fishstiz.github.io/minecraft-cursor-wiki/resource-pack/getting-started#all-cursors). More on the [source code](https://github.com/fishstiz/minecraft-cursor/blob/master/common/src/main/java/io/github/fishstiz/minecraftcursor/impl/MinecraftCursorInitializerImpl.java#L35).

To check if an element is detected, the **Element Inspector** can be toggled from **More Cursor Options**. More info on how an element is detected (and how to make it detectable) on the [wiki](https://fishstiz.github.io/minecraft-cursor-wiki/java-api/introduction#the-parentelement-hierarchy).

### Fabric Only Compatibility Features

Note that some mods are unknowingly conflicting with **Minecraft Cursor** due to the libraries they are embedding/depending on (most notably with ImGui). These features work around that:
- When another mod sets a custom cursor through GLFW, the adaptive cursor will be disabled to let it do its thing.
- Standard system cursors from GLFW will be remapped to their Minecraft Cursor equivalents.

This is not supported by **Forge** and **NeoForge**, and even if they were, the early loading screen will break these features. 

Likewise, Fabric mods that initialize GLFW early, such as some early loading screen mods, will also disable these features.

---
<sup>
NOT AN OFFICIAL MINECRAFT PRODUCT. NOT APPROVED BY OR ASSOCIATED WITH MOJANG OR MICROSOFT.
</sup>
