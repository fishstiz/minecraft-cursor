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
![shift](https://github.com/user-attachments/assets/27f97a5c-be91-45c9-ad5d-91a5e162fb50)

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

<details>
<summary><h2>All Cursors</h2></summary>
  <table>
    <thead>
      <tr>
        <th>Cursor Name</th>
        <th>Key</th>
        <th>Image</th>
        <th>When it is used</th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td>Default</td>
        <td><code>default</code></td>
        <td><img src="https://github.com/user-attachments/assets/6c632b54-e284-47a0-8634-f4ba1ef03f29" width="32" alt="default"/></td>
        <td>
          <ul>
            <li>The default cursor.</li>
            <li>If another cursor is disabled.</li>
          </ul>
        </td>
      </tr>
      <tr>
        <td>Pointer</td>
        <td><code>pointer</code></td>
        <td><img src="https://github.com/user-attachments/assets/83a41d81-5a0b-4399-8d70-61ca421117c0" width="32" alt="pointer" /></td>
        <td>
          <span>Hovered over:</span>
          <ul>
            <li>Discoverable <code>PressableWidget</code> elements.</li>
            <li>Inventory slots with item/s.</li>
            <li>Creative inventory tabs.</li>
            <li>Recipe book tabs and recipes.</li>
            <li>Available enchantments in the enchanting table.</li>
            <li>Available stonecutter recipes.</li>
            <li>Available loom patterns.</li>
            <li>Crafter slots.</li>
          </ul>
        </td>
      </tr>
      <tr>
        <td>Text</td>
        <td><code>text</code></td>
        <td><img src="https://github.com/user-attachments/assets/049fc447-6f3f-4c7a-a0a2-b87d0348c593" width="32" alt="text"/></td>
        <td>
          <ul>
            <li>Hovered over discoverable <code>TextFieldWidget</code> elements.</li>
            <li>Hovered inside Book and Quill book.</li>
          </ul>
        </td>
      </tr>
      <tr>
        <td>Grabbing</td>
        <td><code>grabbing</code></td>
        <td><img src="https://github.com/user-attachments/assets/bdcd6392-a8bb-40af-b2fa-10a465363545" width="32" alt="grabbing"/></td>
        <td>
          <ul>
            <li>Grabbing items.</li>
            <li>Dragging the slider in discoverable <code>SliderWidget</code> elements.</li>
          </ul>
        </td>
      </tr>
      <tr>
        <td>Shift</td>
        <td><code>shift</code></td>
        <td><img src="https://github.com/user-attachments/assets/27f97a5c-be91-45c9-ad5d-91a5e162fb50" width="32" alt="shift"/></td>
        <td>
          <span>Shift is pressed and mouse is hovered over:</span>
          <ul>
            <li>Inventory slots with item/s.</li>
            <li>Creative inventory destroy item slot.</li>
            <li>Recipe book recipes.</li>
            <li>Villager trade offers.</li>
          </ul>
        </td>
      </tr>
    </tbody>
  </table>
</details>

<details>
  <summary><h2>Resource Pack Support</h2></summary>
  <h3>Image Format</h3>
  <ul>
    <li>32x32 pixels</li>
    <li>png format</li>
  </ul>

  <h3>File Structure</h3>
  <pre><code>└── minecraft-cursor/
    ├── atlases/
    │   └── cursors.json
    └── textures/
        └── cursors/
            ├── default.png
            ├── grabbing.png
            ├── pointer.png
            ├── shift.png
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
    },
    "shift": {
      "xhot": 11,
      "yhot": 3
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
<summary><h2>Mod Compatibility</h2></summary>
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
</details>
