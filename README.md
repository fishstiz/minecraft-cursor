# Minecraft Cursor

A Fabric mod that replaces the boring old default system cursor with a Minecraft-themed cursor.

## Requirements
- Fabric for Minecraft 1.21.2-1.21.3
- Compatible with [Mod Menu](https://modrinth.com/mod/modmenu) (optional)

## Adaptive Cursor
![default](https://github.com/user-attachments/assets/6c632b54-e284-47a0-8634-f4ba1ef03f29)
![pointer](https://github.com/user-attachments/assets/83a41d81-5a0b-4399-8d70-61ca421117c0)
![grabbing](https://github.com/user-attachments/assets/bdcd6392-a8bb-40af-b2fa-10a465363545)
![text](https://github.com/user-attachments/assets/049fc447-6f3f-4c7a-a0a2-b87d0348c593)

- Adaptive cursors when hovering over buttons, text fields, and when grabbing sliders (limited to common GUI elements only).
- You may submit an issue if you think this feature should be updated/removed on certain contexts.
- Disable all cursors except default in the cursor settings to disable this feature,
  - You can also toggle all non-default cursors in one click from More Cursor Options
  - You can disable adaptive cursors for certain actions in More Cursor Options
- Mods that shift the sizes and positions of certain **in-game** GUIs may lead to unexpected cursor switching.
  - Check the **Mod Compatibility** section to see affected GUIs.
  - You can disable adaptive cursors for affected GUIs in More Cursor Options

## Configure Cursors
- Configure from Mod Menu (if installed)
- Configure from Options > Controls > Mouse Settings... > Cursor Settings...
- Configure More Cursor Options from Cursor Settings > More Cursor Options...
- Disabled cursors will fallback to the default cursor unless it is also disabled.

<img alt="options-menu" src="https://github.com/user-attachments/assets/f45341d7-bacc-4c83-96e2-f03002195830" style="width:600px;"/>  

![image](https://github.com/user-attachments/assets/74d6e272-78ce-4735-8813-ac50a91f3485)

&nbsp;
<details>
  <summary>Resource Pack Support</summary>
  <h3>Image Format</h3>
  <ul>
    <li>32x32 pixels</li>
    <li>png format</li>
  </ul>

  <h3>File Structure</h3>
  <p>Missing cursors will automatically be disabled in-game and will not show up in the settings. If <code>default.png</code> is provided, it will use that instead.</p>
  <pre><code>└── minecraft-cursor/
    ├── atlases/
    │   └── cursors.json
    └── textures/
        └── cursors/
            ├── default.png
            ├── grabbing.png
            ├── pointer.png
            └── text.png</code></pre>

  <h3>Custom Configuration</h3>
  <ul>
    <li>Define a custom configuration for your resource pack in <code>atlases/cursors.json</code>.</li>
    <li>Can be overridden by users through the Cursor Settings menu.</li>
    <li>The user's config will reset to the provided config when changing resource packs.</li>
  </ul>

  <p><strong>Example</strong> <code>cursors.json</code>:</p>
  <pre><code>{
  "settings": {
    "default": {
      "xhot": 7,
      "yhot": 3,
      "scale": 0.8
    },
    "pointer": {
      "xhot": 7,
      "yhot": 3,
      "scale": 0.8
    },
    "text": {
      "xhot": 12,
      "yhot": 15,
      "scale": 0.8
    },
    "grabbing": {
      "enabled": false 
    }
  }
}</code></pre>

  <p><strong>All Settings:</strong></p>
  <ul>
    <li><code>enabled</code>: <code>true</code>/<code>false</code></li>
    <li><code>scale</code>: <code>0.50</code> - <code>3.00</code> (incrementing in 0.05)</li>
    <li><code>xhot</code>: <code>0</code> - <code>31</code></li>
    <li><code>yhot</code>: <code>0</code> - <code>31</code></li>
  </ul>
</details>

<details>
<summary>Mod Compatibility</summary>
  <h4>
    Widgets are automatically registered by this mod with the following conditions:&nbsp;
  </h4>
  <ul>
    <li>Pointer elements must be an instance of <code>PressableWidget</code>
        or <code>SliderWidget</code>
    </li>
      <ul>
        <li>
          <code>ClickableWidget</code> is not registered as they are not always a button. For example: <code>ScrollableWidget</code> is a subclass of <code>ClickableWidget</code>
        </li>
      </ul>
    <li>Text elements must be an instance of <code>TextFieldWidget</code></li>
    <li>They must be a child of <code>ParentElement</code> (e.g. <code>Screen</code>), 
      accessible through <code>children()</code> method
    </li>
    <li>Container elements must be an instance of <code>ParentElement</code>
      and nested containers must be an instance and child of <code>ParentElement</code>
    </li>
   </ul>
   <h4>GUI "elements" that may be affected from shifting their positions and sizes:</h4>
   <ul>
     <li><code>CreativeInventoryScreen</code> tabs</li>
     <li><code>EnchantmentScreen</code> choices</li>
     <li><code>StonecutterScreen</code> recipes</li>
     <li><code>BookEditScreen</code> book</li>
     <li><code>LoomScreen</code> patterns</li>
     <li><code>WorldListWidget</code> world icon play button</li>
   </ul>
  <a href="https://github.com/fishstiz/minecraft-cursor/blob/master/src/client/java/io/github/fishstiz/minecraftcursor/registry/CursorTypeRegistry.java" target="_blank">See More</a>
</details>
