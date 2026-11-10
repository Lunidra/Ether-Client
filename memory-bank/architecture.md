# Ether Architecture Overview

## Core Components

### EtherClient
- Main mod entry point (implements ClientModInitializer)
- Manages core system initialization
- Coordinates with ClientContext for runtime operations

### ClientContext
- Central runtime coordinator
- Manages: EventBus, InputManager, TitleManager, ModuleManager, HUDManager
- Initializes core subsystems on client start
- Handles rendering callbacks and tick events

### NetworkConfig
- Network configuration (chat server configuration removed)

## Feature Modules

### Core Modules
- **Modules**: Core functionality (ClickGUI, Fullbright, Freelook, ServerInfo, Sprint, HudEditor, HUD)
- **QoL Modules**: Quality of life features (ChatEnhancer - chat functionality removed)

### UI/HUD
- **HudComponents**: Individual HUD elements (coordinates, fps, ping, playtime)
- **HudManager**: Manages HUD component registration and rendering
- **UIComponents**: Base UI components with rendering capabilities
- **SocialOverlay**: Social display overlay (chat functionality removed)

### Notification System
- **NotificationManager**: Manages notification creation and lifecycle
- **NotificationRenderer**: Renders notifications to UI

### Input System
- **InputManager**: Handles keyboard and mouse input
- **KeybindHandler**: Manages keybinding registration

### Title System
- **TitleManager**: Manages title bar rendering

### Discord Integration
- **DiscordRPCManager**: Discord Rich Presence integration

### Event System
- **EventBus**: Central event dispatching mechanism
- **TickEvent**: Tick-based event for module updates

## Mixins

### ClientPacketListenerMixin
- Client packet listener modifications (chat interception removed)

## UI Rendering

### UIRenderer
- Core UI rendering engine
- Handles drawing operations for all UI components

### ThemeManager
- Manages UI theme definitions and colors
