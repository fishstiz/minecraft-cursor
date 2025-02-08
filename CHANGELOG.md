- Added API for Developers that allows:
    - Creation of cursor types in runtime (on resource reloads only)
    - Registering of elements with a cursor type function
    - Implementing an interface to elements to declare its cursor type
    - Direct control of the current cursor type
- Added pt_br translation


- Removed Version 3.0.0
    - 3.1.0 has breaking changes for Minecraft Cursor API
    - Renamed `CursorTypeRegistrar` to `ElementRegistrar`
        - Rename `ElementCursorTypeFunction` to `CursorTypeFunction`
    - Replaced `CursorTypeFactory` with `CursorTypeRegistrar`
        - use `CursorType#of(String)` to create a `CursorType`
        - use `CursorTypeRegistrar#register(CursorType)` to register a `CursorType`
    - The access and packages of some internal classes have been modified