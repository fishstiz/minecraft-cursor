- Added Russian Translation ([#33](https://github.com/fishstiz/minecraft-cursor/pull/33) by mpustovoi)
- Improved detection for `AbstractWidget`/`ClickableWidget`. Now also checks for `isMouseOver(double, double)` in case `isHovered()` is `false`.
- Improved detection for server icon buttons. Now checks for each sprite bounds instead of the icon only.
- Fixed in-between parent elements not being processed within the element hierarchy if the current screen is a registered element.
- Fixed search highlights not preserving when changing panels in the configuration screen.
- Fixed crash when hovered over icon bounds of world list if worlds have not loaded and world list icon adaptive option is enabled.
- Fixed cursor not reverting to default after closing a non-active rendered screen and mouse is unlocked without a screen open.

Added 1.21.6 Support:
- Removed option to toggle the **Book Edit Book** adaptive cursor. It's no longer needed since the book now uses a `MultilineEditBox` component.