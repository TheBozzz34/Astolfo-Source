# Astolfo Source

This repository contains the Java source of the Astolfo client. Astolfo was a modified
Minecraft 1.8.9 client, its development has stopped.

The source is decompiled from the last release of the client. The tools in
[astolfo-decomp](https://github.com/TheBozzz34/astolfo-decomp) make this source. Do not edit
this source by hand. Change the tools in astolfo-decomp, then make the source again.

## Contents

1. [Status](#1-status)
2. [Repository layout](#2-repository-layout)
3. [Requirements](#3-requirements)
4. [Build the client](#4-build-the-client)
5. [Run the client](#5-run-the-client)
6. [Configuration files](#6-configuration-files)
7. [Safety](#7-safety)
8. [Known limits](#8-known-limits)
9. [How the source was made](#9-how-the-source-was-made)
10. [Legal notice](#10-legal-notice)

## 1. Status

- The source compiles without errors with JDK 8.
- The source has 3,190 files. They compile to 4,335 classes.
- Each method of the compiled source has the same bytecode as the release, after the names are mapped.
- All classes, methods and fields of the client have names.
- The names of the client are not the original names. The original names are not in the release.
  The tools give each name from what the code does.
- The client starts and shows a login screen. The login screen needs the authentication server of
  the original client. See [Known limits](#8-known-limits).

## 2. Repository layout

| Path | Contents |
|---|---|
| `src/bypass/` | The client: modules, GUI, Lua scripts, authentication, start-up checks |
| `src/net/minecraft/` | Minecraft 1.8.9, with MCP names and the hooks of the client |
| `src/net/minecraft/src/` | OptiFine 1.8.8 HD U I7 |
| `src/shadersmod/` | The shaders part of OptiFine |
| `src/oshi/` | OSHI 4.6.1, a library that reads hardware data |
| `src/net/hypixel/` | Hypixel Public API 3.0.0 |
| `src/com/` | ConcurrentLinkedHashMap 1.4.2 and Dexx Collections 0.7 |
| `src/start/` | The start class, `start.Main` |
| `resources/` | Textures, sounds, shaders and other files of the client |
| `lib/astolfo-libs.jar` | The libraries that the client uses (LWJGL, Netty, LuaJ, ViaVersion and others) |
| `natives/` | Native libraries for Windows (LWJGL, OpenAL, JInput, JNA) |
| `astolfo/` | Example configuration files of the client |
| `Astolfo.iml`, `.idea/` | The IntelliJ IDEA project and the run configuration |

The client code is in these packages of `src/bypass/`:

| Package | Contents |
|---|---|
| `module/` | The modules, by category: `combat`, `movement`, `player`, `visuals`, `exploits`, `other`, `hidden` |
| `gui/` | The click GUIs, the HUD, the tab GUI and the screens |
| `lua/` | The Lua scripting API |
| `auth/` | The connection to the authentication server |
| `loader/` | The start-up stages and the anti-tamper checks |
| `event/` | The event bus and the events |
| `setting/` | The settings of the modules |
| `anticheat/` | The checks of the HackerDetector module |

## 3. Requirements

- JDK 8. Later JDK versions do not compile the source. The source uses classes that JDK 9 and
  later removed (`javax.xml.bind`, Nashorn, `com.sun.istack.internal`).
- Windows, to run the client. The `natives/` folder has Windows libraries only.
- The Minecraft 1.8 assets. The official Minecraft launcher installs them in
  `%APPDATA%\.minecraft\assets`.
- 4 GB of free memory, to compile and to run the client.

## 4. Build the client

### 4.1 Build with IntelliJ IDEA

1. Open the repository folder in IntelliJ IDEA.
2. Open **File > Project Structure > Project**.
3. Set **SDK** to a JDK 8.
4. Select **Build > Build Project**.

The compiled classes go into `out/`.

NOTE: The project gives `-XDignore.symbol.file` to the compiler. Do not remove this option.
Without it, the compiler cannot find `com.sun.istack.internal`.

### 4.2 Build from the command line

Use the `javac` of a JDK 8.

On Windows:

1. Open a command prompt in the repository folder.
2. Make the list of source files:

   ```bat
   dir /s /b src\*.java > sources.txt
   ```

3. Compile the source:

   ```bat
   javac -J-Xmx4g -nowarn -encoding UTF-8 -XDignore.symbol.file -proc:none -cp lib\astolfo-libs.jar -d out @sources.txt
   ```

On Linux or macOS:

1. Open a terminal in the repository folder.
2. Make the list of source files:

   ```sh
   find src -name '*.java' > sources.txt
   ```

3. Compile the source:

   ```sh
   javac -J-Xmx4g -nowarn -encoding UTF-8 -XDignore.symbol.file -proc:none -cp lib/astolfo-libs.jar -d out @sources.txt
   ```

The compiler shows notes about unchecked and deprecated operations. These notes are not errors.

## 5. Run the client

### 5.1 Run with IntelliJ IDEA

The project has a run configuration with the name **Astolfo**.

1. Open **Run > Edit Configurations > Astolfo**.
2. In **Program arguments**, change the value of `--assetsDir` to the path of your Minecraft assets.
3. Make a folder with the name `run` in the repository folder.
4. Select **Run > Run 'Astolfo'**.

The run configuration uses `run/` as the game folder.

### 5.2 Run from the command line

1. Build the client. See [Build from the command line](#42-build-from-the-command-line).
2. Make a folder with the name `run` in the repository folder.
3. Start the client from the repository folder:

   ```bat
   java -Xmx4G -XX:+UseG1GC -Djava.library.path=natives -Dos.name="Windows 10" -Dos.version=10.0 ^
     -cp "out;resources;lib\astolfo-libs.jar" start.Main ^
     --username Player --version 1.8.9 --gameDir run ^
     --assetsDir "%APPDATA%\.minecraft\assets" --assetIndex 1.8 ^
     --uuid 887341c45ea94dd3bae9bb00502124f1 --accessToken 0 --userProperties {} --userType msa
   ```

NOTE: `start.Main` needs the `--accessToken` and `--version` arguments. The value of
`--accessToken` is not used to log in to the client.

## 6. Configuration files

The client keeps its configuration in a folder with the name `astolfo`. This folder is in the
parent folder of the game folder. With the run configuration of this project, the folder is
`astolfo/` in this repository.

| File | Contents |
|---|---|
| `default.json` | The modules and their settings |
| `alts.json` | The saved Minecraft accounts |
| `altening.json` | The TheAltening accounts |
| `relations.json` | The friends and the enemies |
| `chat.json` | The chat settings |
| `fonts.json` | The fonts |
| `killsay.txt` | The messages of the KillSay module |
| `scripts/scripts.json` | The Lua scripts and their state |

CAUTION: `alts.json` and `altening.json` keep account data as plain text. Do not put real
account data in these files if you share the folder.

## 7. Safety

The client has code that downloads and runs other code. Read this section before you run the
client.

WARNING: The client can download JavaScript from `astolfo.lgbt` and run it when the game stops.
The name of the file is the MD5 hash of the game jar. This code is in
`net.minecraft.util.MathHelper`. The client registers it the first time that `floor_double` runs.
Remove this code before you run the client.

WARNING: The Lua function `http.load` downloads Lua code from a URL and runs it. It also sends
the hardware ID of the computer. The function works only when the **Unsafe Functions** setting
is on. The code is in `bypass.lua.HttpLibrary.LoadUrlFunction`. Do not turn on this setting for
scripts that you do not trust.

CAUTION: The client sends the hardware ID of the computer to the authentication server. It also
sends the MD5 hash of the game jar to `security.astolfo.lgbt`. The code is in `bypass.auth` and
`bypass.loader.IntegrityCheck`.

CAUTION: At start-up, the client examines the computer. It looks for virtual machines, debug
options of the JVM, Java agents and some programs (for example Wireshark and Process Hacker). The
code is in `bypass.loader.check`.

## 8. Known limits

- The client needs the authentication server of the original client. The address of the server
  is in `bypass.auth.AddressParts`. This repository does not include a server.
- Some values are wrong until the authentication server sends the correct values at login.
  Thus, without a login, the client does not show entity positions and the world correctly. The
  values are in `bypass.module.ModuleManager` (`positionDivisor`, `modelViewMatrixMode`) and in
  `net.minecraft.client.renderer.EntityRenderer` (`authFovMultiplier`, `authClearMask`,
  `authMatrixParam`).
- About 1,440 Minecraft methods and fields have names like `func_178603_a` and `field_147431_j`.
  These are the SRG names. The last MCP release for Minecraft 1.8.9 does not give them other names.
- About 60 enum constants of the client keep their obfuscated names, for example
  `AstolfoUserStatus.A`. The GUI shows these names and the configuration files keep them. A new
  name would change the behavior of the client.
- Some names of the client are guesses. In the mapping file of astolfo-decomp
  (`tools/client-remap/names.txt`), the evidence of a guess starts with `?`.

## 9. How the source was made

The release of the client is obfuscated with Zelix KlassMaster. The tools in
[astolfo-decomp](https://github.com/TheBozzz34/astolfo-decomp) remove the obfuscation in these
steps:

1. Decrypt the strings, the numbers and the method references.
2. Give Minecraft the MCP names, and give OptiFine and the libraries their own names.
3. Remove the key code that the obfuscator left, and decrypt the strings of the client.
4. Give names to the members of OptiFine, Minecraft and the libraries from code that matches
   their releases.
5. Give names to the classes, methods and fields of the client.
6. Give names to the parameters and the local variables.
7. Decompile the classes with Vineflower.
8. Correct the source so that it compiles.
9. Give names to the remaining local variables from their types.
10. Compile the source with JDK 8.

Each step has a README in astolfo-decomp. The README tells what the step changes and how the
step was checked.

To make this source again:

1. Clone astolfo-decomp.
2. Run `./build.sh --jdk8 <path to JDK 8>`, or `.\build.ps1 --jdk8 <path to JDK 8>` on Windows.
3. Copy the `.java` files from `build/src/` into `src/` of this repository.

## 10. Legal notice

This repository is for research and education.

This source includes decompiled code of other projects. The owners of these projects keep all of
their rights:

- Minecraft is the property of Mojang Studios.
- OptiFine is the property of sp614x.
- OSHI, the Hypixel Public API and Dexx Collections use the MIT license.
- ConcurrentLinkedHashMap uses the Apache License 2.0.

The Astolfo client is the property of its authors.
