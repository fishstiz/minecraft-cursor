- Respect precedence of children when multiple registered elements overlap.
- Optimized cursor (and more) options screen.
- Fixed elements duplicating when resizing screen in 1.20.1 and 1.21.1 (#17)
- Fixed cursor list scroll overflowing when resizing screen.

Fabric:
- Add compatibility with Quilt for 1.21.2 and above (#27)
- `GlfwMixin` (cursor tracking) is now applied dynamically to prevent crashes with mods that load GLFW early other than early loading screen.
- Removed `minecraft-cursor.properties` (it's no longer needed due to above change).
