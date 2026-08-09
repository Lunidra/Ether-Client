# Ether Client

Ether is a modular Minecraft client built on Fabric.

The project started as a lightweight client focused around modules, HUD elements and a custom ClickGUI. It has since grown into a broader client platform with its own UI system, themes, account handling, realtime services and rendering layer.

The current development focus is cleaning up the UI architecture and building a proper rendering backend without tying the UI directly to Minecraft's rendering implementation.

> **Current target:** Minecraft 1.21.11
> **Loader:** Fabric
> **Language:** Java
> **Status:** Active development

---

## Features

### Client

* Modular feature system
* Custom EventBus
* Keybind management
* Persistent configuration
* HUD system
* Custom ClickGUI
* HUD editor
* Theme system
* UI animation utilities
* Global notification system

### Platform

* Microsoft / Minecraft account support
* Ether authentication
* EtherChat
* Realtime user presence
* WebSocket communication
* Discord Rich Presence

### UI

* Layered UI system
* Custom UI renderer
* Theme-aware surfaces
* Draggable panels
* Setting components
* Notifications and overlays
* Renderer sandbox for testing new rendering features

---

# Architecture

Ether is split into a few main areas rather than putting everything inside the client entrypoint.

```text
                           Minecraft
                               │
                               ▼
                         Fabric Loader
                               │
                               ▼
                         ┌───────────┐
                         │ EtherClient│
                         └─────┬─────┘
                               │
                               ▼
                        ┌──────────────┐
                        │ ClientContext │
                        └──────┬───────┘
                               │
          ┌────────────────────┼────────────────────┐
          │                    │                    │
          ▼                    ▼                    ▼
        Core                Features                UI
          │                    │                    │
    ┌─────┼─────┐       ┌─────┼─────┐       ┌─────┼─────────┐
    │     │     │       │     │     │       │     │         │
 Event  Input Config  Modules Account Chat   HUD ClickGUI Notifications
    │
    ├── Network
    ├── Window
    └── Discord
                               │
                               ▼
                         UI / Rendering
                               │
                         ┌─────┴─────┐
                         │ UIManager │
                         └─────┬─────┘
                               │
                    ┌──────────┼──────────┐
                    │          │          │
                   HUD      OVERLAY     future
                                      MODAL / TOOLTIP
                               │
                               ▼
                         ┌───────────┐
                         │ UIRenderer│
                         └─────┬─────┘
                               │
                               ▼
                         Render Backend
                               │
                               ▼
                       Minecraft Rendering
```

The diagram represents the intended direction of the architecture. Some parts,
particularly the rendering pipeline, are still being developed.

---

# Source Layout

```text
src/client/java/luni/ether/

├── core/
│   ├── config/
│   ├── discord/
│   ├── event/
│   ├── input/
│   ├── io/
│   ├── network/
│   ├── render/
│   └── window/
│
├── feature/
│   ├── account/
│   ├── chat/
│   ├── module/
│   └── setting/
│
├── mixin/
│
└── ui/
    ├── animation/
    ├── clickgui/
    ├── component/
    ├── debug/
    ├── hud/
    ├── notification/
    ├── render/
    ├── style/
    └── theme/
```

---

# Core

## ClientContext

`ClientContext` is the main runtime container.

It owns the services that make up the client:

```text
ClientContext
├── EventBus
├── InputManager
├── TitleManager
├── ModuleManager
├── HUDManager
└── UIManager
```

The goal is to keep the actual client entrypoint small and put runtime
initialisation in the context.

---

## EventBus

The EventBus is used for communication between the client systems.

It supports:

* Event priorities
* Cancellable events
* Annotation-based listeners
* Listener registration/unregistration
* Snapshot-safe dispatch

The normal tick path is:

```text
Minecraft Tick
      │
      ▼
ClientTickEvents.END_CLIENT_TICK
      │
      ├── ChatClient.tick()
      │
      ▼
EventBus.post(TickEvent)
      │
      ▼
ModuleManager
      │
      ▼
Enabled Modules
```

---

# Modules

Modules live under:

```text
feature/module/
```

The module system currently handles features such as:

* ClickGUI
* Fullbright
* Freelook
* Server information
* Sprint
* HUD
* HUD editor
* Sandbox
* Chat enhancements

Modules are separate from their settings.

```text
Module
   │
   └── Settings
          │
          ▼
   SettingComponent
          │
          ▼
      ClickGUI
```

This keeps the configuration model separate from how the setting is displayed.

---

# Configuration

Configuration is handled by `ConfigManager`.

The client currently persists things such as:

* Module state
* Keybinds
* Module settings
* ClickGUI positions
* HUD positions
* Active theme

The configuration is stored in the Ether configuration directory.

---

# HUD

The HUD system lives under:

```text
ui/hud/
```

Current HUD components include things such as:

* ArrayList
* FPS
* Coordinates
* Ping
* Session/playtime
* Watermark
* Social overlay

HUD components have their own positions and can be edited through the HUD editor.

The long-term goal is for HUD elements to use the same renderer and theme
system as the rest of the client rather than having separate rendering logic.

---

# ClickGUI

The ClickGUI lives under:

```text
ui/clickgui/
```

Its structure is roughly:

```text
ClickGuiScreen
│
├── CategoryPanel
│
└── SettingsPanel
      │
      ├── ToggleComponent
      ├── SliderComponent
      ├── ModeComponent
      └── KeybindComponent
```

The ClickGUI is deliberately kept separate from the low-level rendering code.

It should ask the renderer to draw things rather than knowing how Minecraft's
rendering implementation works.

---

# UI System

The current UI system is moving toward a layered compositor.

The main pieces are:

```text
UIManager
UILayer
UIRenderable
UIRenderer
```

Current layers:

```text
HUD
OVERLAY
```

The intended direction is:

```text
HUD
 │
 ▼
OVERLAY
 │
 ▼
MODAL
 │
 ▼
TOOLTIP
```

Not all of these layers exist yet.

---

# Notifications

Notifications are treated as global UI overlays rather than HUD components.

```text
NotificationManager
        │
        ▼
NotificationOverlay
        │
        ▼
UIManager
        │
        ▼
OVERLAY
```

Notifications are intended for things such as:

* Authentication events
* Connection problems
* Reconnects
* Backend events
* Social events
* Theme changes
* Other global client events

They are not intended to be used for every module toggle.

---

# Themes

Themes live under:

```text
ui/theme/
```

The theme system currently provides semantic values such as:

```text
accent
accentDark
textPrimary
textSecondary
surface
surfaceBright
border
success
warning
error
```

Current themes include:

```text
Green
Blue
Purple
Orange
Pink
```

The renderer is gradually being moved toward using these semantic values
instead of arbitrary colours being chosen by individual UI components.

---

# Rendering

The rendering system is currently being rewritten.

The main abstraction is:

```text
ui/render/UIRenderer
```

At the moment it provides higher-level operations such as:

```text
rect()
text()
line()

panel()
outlinedPanel()
panelHeader()

push()
pop()

translate()
scale()

pushScissor()
popScissor()
```

The renderer currently sits above Minecraft's `GuiGraphics`.

The intention is **not** to make every UI component directly interact with
Minecraft's rendering API.

Instead:

```text
UI Component
     │
     ▼
 UIRenderer
     │
     ▼
Render Backend
     │
     ▼
Minecraft
```

This gives us somewhere to build our own rendering implementation without
having to rewrite the entire UI system at once.

---

# Rendering Roadmap

The renderer is being developed incrementally.

### 1. Compositor

First, the ownership of each rendering layer needs to be made explicit.

```text
Minecraft HUD
     │
     ▼
UIManager
     │
     └── HUD

Minecraft Screen
     │
     ▼
UIManager
     │
     ├── OVERLAY
     ├── MODAL
     └── TOOLTIP
```

### 2. Geometry

The renderer will eventually support procedural UI geometry such as:

* Rectangles
* Rounded rectangles
* Borders
* Lines
* Circles / arcs
* Gradients

Basic UI geometry should not depend on textures unless there is a good reason
to use them.

### 3. Surfaces

Higher-level surfaces can then be built from the geometry primitives:

```text
Panel
Elevated Panel
Modal
Card
Tooltip
```

### 4. Effects

Once the basic renderer is stable:

```text
Shadows
Gradients
Glow
Blur
Acrylic
Bloom
Post-processing
```

These are deliberately later additions. The basic geometry renderer should be
stable before effects are introduced.

---

# Renderer Sandbox

Experimental renderer work is done in:

```text
ui/debug/RendererSandboxScreen
```

The sandbox exists so rendering changes can be tested without immediately
changing the ClickGUI or HUD.

It is used for testing things such as:

* Rectangles
* Panels
* Transforms
* Scaling
* Scissor clipping
* Layering
* Alpha
* New renderer primitives

New rendering functionality should be tested here before being used throughout
the client.

---

# Rendering Entry Points

There are currently two important Minecraft rendering hooks.

## HUD

The Minecraft HUD render callback is used for HUD rendering.

```text
HudRenderCallback
       │
       ▼
   UIRenderer
       │
       ▼
   HUDManager
```

## Screens

`ScreenMixin` injects into Minecraft's screen rendering.

```text
Screen.render()
       │
       ▼
ScreenMixin
       │
       ▼
UIRenderer
       │
       ▼
UIManager
```

This is where global screen overlays such as notifications belong.

The rendering-engine branch is currently cleaning up the distinction between
these paths so that an overlay cannot accidentally be rendered twice.

---

# EtherChat

EtherChat provides the client-side realtime connection to the Ether backend.

The client communicates using WebSockets.

```text
Minecraft
    │
    ▼
ChatClient
    │
    ▼
WebSocket
    │
    ▼
Ether Backend
```

The client handles:

* Authentication
* Chat messages
* Presence
* User joins/leaves
* User updates
* Broadcasts
* Reconnection

Presence can include information such as:

```text
Username
Server
Server IP
Minecraft version
Singleplayer / multiplayer state
```

Incoming network events are moved back onto the Minecraft thread before
interacting with Minecraft state.

---

# Accounts

Account handling lives under:

```text
feature/account/
```

This includes:

* Ether accounts
* Microsoft accounts
* Minecraft account storage
* Authentication

Account infrastructure is intentionally separate from the module system.

---

# Mixins

Minecraft-specific hooks live under:

```text
mixin/
```

Mixins are used where Fabric APIs do not provide the required integration
point.

Notable areas include:

* Screen rendering
* Minecraft lifecycle
* Network/client packet handling
* Title screen
* Client branding
* Minecraft options

UI code should generally stay out of mixins where possible. Mixins should act
as the bridge between Minecraft and Ether's own systems.

---

# Runtime Overview

A simplified client tick looks like this:

```text
Minecraft
   │
   ▼
END_CLIENT_TICK
   │
   ├── ChatClient.tick()
   │
   └── EventBus.post(TickEvent)
                │
                ▼
          ModuleManager
                │
                ▼
          Enabled Modules
```

Rendering is separated from ticking:

```text
Minecraft Render
      │
      ├───────────────┐
      │               │
      ▼               ▼
     HUD            Screen
      │               │
      ▼               ▼
 HUDManager       ScreenMixin
      │               │
      └───────┬───────┘
              ▼
          UI System
              │
              ▼
          UIRenderer
```

---

# Development Branches

The project uses branches for larger changes rather than making experimental
changes directly on the main development line.

The current rendering work lives on:

```text
feature/rendering-engine
```

The intended progression is:

```text
feature/rendering-engine
          │
          │ stable
          ▼
         dev
          │
          │ release-ready
          ▼
         main
```

`feature/rendering-engine` is expected to contain experimental renderer work
until it is stable enough to merge into `dev`.

---

# Development Philosophy

Ether is intentionally moving away from the traditional Minecraft PvP client
style of UI.

The goal is not to cover the screen with module indicators, notifications and
panels.

The direction is:

* Clean
* Lightweight
* Responsive
* Consistent
* Modular
* Service-oriented
* Visually cohesive

The UI should feel like one application rather than a collection of unrelated
Minecraft screens.

Rendering is therefore treated as infrastructure, not as something individual
screens should implement themselves.

---

# Current Priorities

The rendering-engine branch is currently focused on:

1. Cleaning up render-layer ownership
2. Finishing the UI compositor
3. Establishing a proper renderer API
4. Building procedural UI geometry
5. Adding rounded corners
6. Building reusable surfaces
7. Adding shadows and other effects later
8. Migrating existing UI components once the renderer is stable

The renderer should be developed independently of the existing HUD and ClickGUI
until the underlying API is reliable.

---

# Project Structure at a Glance

```text
Ether
│
├── core
│   ├── config
│   ├── discord
│   ├── event
│   ├── input
│   ├── io
│   ├── network
│   ├── render
│   └── window
│
├── feature
│   ├── account
│   ├── chat
│   ├── module
│   └── setting
│
├── mixin
│
└── ui
    ├── animation
    ├── clickgui
    ├── component
    ├── debug
    ├── hud
    ├── notification
    ├── render
    ├── style
    └── theme
```

---

## Status

Ether is a work in progress.

The core client architecture is established, but several systems are still
being actively developed and refactored. In particular, the rendering pipeline
is currently undergoing a major architectural rewrite.

If you're looking at the repository and something appears half-finished,
there's a good chance it is.
