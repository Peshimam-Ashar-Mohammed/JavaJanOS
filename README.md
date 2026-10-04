# JavaOS

This repository is the JavaOS continuation of the original FilzaNix prototype.
The source packages and launcher now use `com.javaos` and `JavaOS.bat`.

### A Java-powered Linux-inspired virtual command-line environment.

JavaOS is an experimental command-line environment developed from scratch using **Java 21**.

Inspired by Linux shells and virtual operating systems, FilzaNix provides its own simulated filesystem, command interpreter, interactive terminal interface, and execution environment.

The project is built as an educational systems-programming project to explore how command-line interfaces, shell interpreters, filesystem operations, terminal input, and system abstractions work internally.

> **Status:** Active Development — Networking Utilities Implemented

---

## Overview

FilzaNix is designed to evolve into a Linux-inspired virtual computing environment with its own filesystem, command execution engine, scripting capabilities, system utilities, and simulated operating-system concepts.

Rather than simply forwarding commands to the Windows operating system, FilzaNix implements its own command handling and virtual filesystem using Java.

The long-term goal is to build a small, understandable environment where operating-system and systems-programming concepts can be explored through actual implementation.

---

## Features

### Currently Implemented

- Interactive command-line interface
- Custom `filzanix:` terminal prompt
- Colored terminal output
- Startup and initialization sequence
- Java 21 runtime
- JLine-powered interactive command input
- Up/Down arrow command history navigation
- Terminal scrollback through the Windows console
- Built-in command history
- Virtual root filesystem
- Virtual working directory navigation
- Directory creation
- File creation
- File reading
- File deletion
- Empty-directory deletion
- File writing
- File appending
- Output redirection using `>` and `>>`
- Linux-inspired command structure
- Basic filesystem path protection and normalization

### Networking Utilities

- Network interface and IP address information
- DNS hostname resolution
- Host connectivity testing
- HTTP requests using Java HttpClient
- Routing table inspection
- Network connection inspection
- Traceroute and Windows tracert support
- TCP common-port scanner
- Port state classification
- Conventional service-name identification
- Scan duration and statistics
- Hostname and IP address targets

| Command | Description |
|---|---|
| `help` | Display available commands |
| `echo <text>` | Print text |
| `echo <text> > <file>` | Write text to a file |
| `echo <text> >> <file>` | Append text to a file |
| `pwd` | Display current working directory |
| `ls` | List files and directories |
| `mkdir <directory>` | Create a directory |
| `cd <directory>` | Change directory |
| `touch <file>` | Create a file |
| `cat <file>` | Display file contents |
| `rm <file>` | Remove a file |
| `rmdir <directory>` | Remove an empty directory |
| `history` | Display command history |
| `date` | Display date and time |
| `whoami` | Display current user |
| `uname` | Display operating system information |
| `uname -a` | Display extended system information |
| `arch` | Display system architecture |
| `hostname` | Display hostname |
| `sysinfo` | Display JVM memory information |
| `ip` | Display network information |
| `ip addr` | Display network interfaces |
| `ip route` | Display routing table |
| `nslookup <host>` | Resolve hostname |
| `ping <host>` | Test host connectivity |
| `curl <url>` | Send HTTP request |
| `netstat` | Display network connections |
| `traceroute <host>` | Trace network route |
| `tracert <host>` | Windows traceroute alias |
| `portscan <host>` | Scan common TCP ports |
| `exit` | Shut down FilzaNix |

---

## Planned Features

- Improved command parser
- Quoted arguments
- Support for filenames containing spaces
- Better error handling
- `clear` terminal command
- Environment variables
- Simulated users and permissions
- File permission management
- Command piping
- Advanced output redirection
- Custom `.bar` scripting engine
- System logging
- Simulated process management
- Process scheduling concepts
- Memory-management simulation
- Automated testing
- Improved shell architecture
- More modular command execution
- Configuration and environment management

---

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Core application |
| Java Standard Library | Filesystem operations and application logic |
| JLine 3 | Interactive terminal input and command history |
| Jansi | Terminal support |
| Windows Batch | Build and application launcher |
| Git | Version control |
| GitHub | Source control and project hosting |

JavaOS currently does **not** use Maven or Gradle. External JAR dependencies are managed manually through the `lib/` directory.

---

## Project Structure

```text
JavaOS/
│
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── javaos/
│                   │
│                   ├── Main.java
│                   │
│                   ├── shell/
│                   │   └── Shell.java
│                   │
│                   └── filesystem/
│                   |   └── VirtualFileSystem.java
|                   ├── commands/
│                   └── commands/
│
├── env/
│   ├── home/
│   ├── etc/
│   ├── tmp/
│   └── root/
│
├── lib/
│   ├── jansi-2.4.2.jar
│   ├── jline-reader-3.30.17.jar
│   ├── jline-terminal-3.30.17.jar
│   └── jline-terminal-jansi-3.30.17.jar
│
├── logs/
├── out/
│
├── JavaOS.bat
├── .classpath
├── .gitignore
└── README.md
```

The current command architecture is split into:

- `shell/` — JLine lifecycle, parsing, command abstraction, and registry.
- `filesystem/` — virtual filesystem state and operations.
- `commands/filesystem/`, `commands/system/`, and `commands/networking/` — command registration by responsibility.
- `networking/` — reusable network operations.
- `runtime/` — process execution foundation for future commands.

### Directory Description

- `src/` — Java source code.
- `shell/` — Interactive terminal and command handling.
- `filesystem/` — Virtual filesystem implementation.
- `env/` — Root directory for the simulated environment.
- `lib/` — External JAR dependencies.
- `logs/` — Application logs.
- `out/` — Compiled Java classes.
- `JavaOS.bat` — Windows launcher and build script.

---

# Requirements

Before running FilzaNix, make sure the following are installed:

- **JDK 21 or later**
- **Windows 10 or Windows 11**
- **Command Prompt or Windows Terminal**

Git is optional if you are downloading the repository directly.

### Verify Java

Open Command Prompt and run:

```bat
java -version
javac -version
```

You should see Java 21 or a newer JDK.

### Verify Git

```bat
git --version
```

---

# Installation

## 1. Clone the repository

```bat
git clone https://github.com/Peshimam-Ashar-Mohammed/FilzaNix.git
```

Enter the project directory:

```bat
cd FilzaNix
```

---

## 2. Verify the dependency directory

Make sure the `lib` directory exists:

```text
lib/
├── jansi-2.4.2.jar
├── jline-reader-3.30.17.jar
├── jline-terminal-3.30.17.jar
└── jline-terminal-jansi-3.30.17.jar
```

These dependencies are required for the interactive terminal functionality.

If you cloned the complete repository, they should already be present.

---

# Running FilzaNix

## Method 1 — Double-click the launcher

Open the FilzaNix project folder and double-click:

```text
FilzaNix.bat
```

The launcher automatically:

1. Changes to the FilzaNix project directory.
2. Creates the `out` directory when necessary.
3. Compiles the Java source files.
4. Loads the JLine and Jansi libraries from `lib`.
5. Starts the FilzaNix environment.

---

## Method 2 — Command Prompt

Open Command Prompt and navigate to the project:

```bat
cd C:\path\to\FilzaNix
```

Then run:

```bat
FilzaNix.bat
```

Example:

```bat
cd C:\Users\HP\Desktop\Java\FilzaNix
FilzaNix.bat
```

---

## Method 3 — PowerShell

Open PowerShell in the project directory:

```powershell
.\FilzaNix.bat
```

---

# Important: Run From the Project Directory

FilzaNix uses the project's `env/` directory as its simulated filesystem.

The launcher automatically executes:

```bat
cd /d "%~dp0"
```

This ensures FilzaNix starts relative to the directory containing `FilzaNix.bat`.

For the most reliable behavior, launch the application through:

```text
FilzaNix.bat
```

---

# Example Session

```text
==================================================
                 F I L Z A N I X
           Virtual Computing Environment
==================================================

[BOOT] Initializing environment...
[BOOT] Loading Java runtime...
[BOOT] Preparing system...

[ OK ] System initialized.

              FILZANIX v0.1

Runtime: Java 21

filzanix:/home$ help

Available commands:
  help      - Show this help message
  echo      - Print text to the terminal
  exit      - Exit the program
  pwd       - Print the current working directory
  ls        - List files in the current working directory
  mkdir     - Create a new directory
  cd        - Change the current working directory
  touch     - Create a new file
  cat       - Display the contents of a file
  rm        - Remove a file
  rmdir     - Remove an empty directory
  history   - Show command history

filzanix:/home$ mkdir projects

filzanix:/home$ cd projects

filzanix:/home/projects$ touch hello.txt

filzanix:/home/projects$ echo Hello FilzaNix > hello.txt

filzanix:/home/projects$ cat hello.txt
Hello FilzaNix

filzanix:/home/projects$ ls
hello.txt

filzanix:/home/projects$ history
1  help
2  mkdir projects
3  cd projects
4  touch hello.txt
5  echo Hello FilzaNix > hello.txt
6  cat hello.txt
7  ls
8  history

filzanix:/home/projects$ exit
Shutting down FilzaNix...
```

---

# Interactive Terminal History

FilzaNix uses **JLine 3** to provide interactive terminal input.

While the shell is running, previously entered commands can be recalled using the keyboard.

### Previous command

```text
↑
```

### Next command

```text
↓
```

Commands can be recalled, edited, and executed again.

FilzaNix also provides a separate built-in:

```text
history
```

command that displays the commands entered during the current session.

---

# Terminal Scrollback

When running FilzaNix through a Windows Command Prompt window, the console's scrollback buffer can be used to inspect previous terminal output.

This is separate from JLine's command history.

Therefore:

- **↑ / ↓** → command recall
- **Mouse wheel / console scrollbar** → previous terminal output

Both can be used independently.

---

# Virtual Filesystem

FilzaNix maintains a simulated filesystem inside the project's `env/` directory.

```text
env/
├── home/
├── etc/
├── tmp/
└── root/
```

Commands such as `mkdir`, `cd`, `touch`, `cat`, `rm`, and `rmdir` operate within this simulated environment.

For example:

```text
filzanix:/home$ mkdir test

filzanix:/home$ cd test

filzanix:/home/test$ touch example.txt

filzanix:/home/test$ echo Hello > example.txt

filzanix:/home/test$ cat example.txt
Hello
```

The filesystem implementation is handled by:

```text
src/main/java/com/filzanix/filesystem/VirtualFileSystem.java
```

---

# Output Redirection

FilzaNix supports basic Linux-inspired output redirection using `>` and `>>`.

### Write to a file

```text
echo Hello > file.txt
```

This writes the supplied text to the file.

### Append to a file

```text
echo Another line >> file.txt
```

This appends the text to the existing contents.

Example:

```text
filzanix:/home$ echo Hello > test.txt

filzanix:/home$ echo FilzaNix >> test.txt

filzanix:/home$ cat test.txt
Hello
FilzaNix
```

# Networking

FilzaNix includes networking utilities implemented using Java networking APIs
and selected native operating-system tools.

### DNS Lookup

```text
filzanix:/home$ nslookup github.com

HTTP Request
filzanix:/home$ curl https://example.com


Connectivity Test
filzanix:/home$ ping github.com


Route Inspection
filzanix:/home$ traceroute github.com


TCP Port Scanner
filzanix:/home$ portscan localhost


The port scanner checks a predefined collection of common TCP ports and
reports connection outcomes and conventional service names.
Use port scanning only on systems you own or are authorized to test.

---




---

# Architecture

FilzaNix currently uses a simple layered architecture:

```text
Main
 │
 ▼
Shell
 │
 ├── Command Parsing
 ├── Command Dispatch
 │
 ├── NetworkCommand
 │    ├── DNS Resolution
 │    ├── HTTP Requests
 │    ├── Connectivity Tests
 │    ├── Route Inspection
 │    ├── Network Statistics
 │    └── TCP Port Scanner
 │
 └── VirtualFileSystem
 │
 ▼
VirtualFileSystem
 │
 ├── Virtual Root
 ├── Path Resolution
 ├── Directory Operations
 ├── File Operations
 └── Filesystem Protection
 │
 ▼
env/
```

### `Main.java`

The application entry point.

Responsible for starting the FilzaNix environment and creating the shell.

### `Shell.java`

Responsible for:

- Reading commands
- Displaying the terminal prompt
- Parsing command input
- Dispatching commands
- Maintaining command history
- Handling interactive terminal input

### `VirtualFileSystem.java`

Responsible for:

- Virtual filesystem root
- Current working directory
- Path resolution
- Directory creation and deletion
- File creation, reading, writing, and deletion
- Filesystem path protection

---

# Development Roadmap

| Version | Milestone |
|---|---|
| v0.1 | Terminal initialization and command handling |
| v0.2 | Interactive shell improvements |
| v0.3 | Virtual filesystem |
| v0.4 | File and directory manipulation |
| v0.5 | Environment variables and logging |
| v0.6 | Advanced command parsing |
| v0.7 | Permissions and simulated users |
| v0.8 | Custom scripting engine |
| v0.9 | Process and system simulation |
| v1.0 | Stable virtual command-line environment |

---

# Design Philosophy

FilzaNix is being developed incrementally, with emphasis on understanding the mechanisms behind command-line environments rather than simply relying on existing operating-system behavior.

The project explores:

- Object-oriented programming
- Command interpreters
- Filesystem abstraction
- Terminal input systems
- Input parsing
- Error handling
- Software architecture
- System simulation
- Process-management concepts
- Operating-system concepts

Each feature is implemented incrementally so that the system remains understandable, testable, and extensible.

---

# Current Limitations

FilzaNix is **not** a real operating system.

It currently does not provide:

- A Linux kernel
- Hardware virtualization
- Kernel-level process isolation
- Real Linux system calls
- Real Linux users
- Kernel-level permissions
- Native Linux command execution
```markdown
- Networking utilities are not a replacement for Nmap.
- Port scanning currently uses a predefined set of common TCP ports.
- Service names are inferred from conventional port assignments.
- Network route and connection information may depend on native OS utilities.

The filesystem and system behavior are simulations implemented in Java.

---

# Development

FilzaNix is currently built without Maven or Gradle.

The project uses a manual compilation process through:

```text
FilzaNix.bat
```

The launcher compiles the following source files:

```text
src/main/java/com/filzanix/Main.java
src/main/java/com/filzanix/shell/Shell.java
src/main/java/com/filzanix/filesystem/VirtualFileSystem.java
```

External dependencies are loaded from:

```text
lib/
```

Compiled classes are placed in:

```text
out/
```

---

# Contributing

FilzaNix is currently an independent experimental project.

Bug reports, suggestions, and technical discussions are welcome.

Before submitting changes, verify that the application still launches successfully using:

```bat
FilzaNix.bat
```

and that existing filesystem and shell commands continue to work.

---

# Disclaimer

FilzaNix is an experimental Java-based virtual computing environment.

It is **not** a Linux distribution, Linux kernel, hypervisor, hardware-level virtual machine, or replacement for an operating system.

The project is intended for educational and software-development purposes.

---

# Author

**Ashar**

Independent Java systems-programming project.

GitHub:

https://github.com/Peshimam-Ashar-Mohammed

Repository:

https://github.com/Peshimam-Ashar-Mohammed/FilzaNix

---

## License

No license has been selected for the project yet.
```

I also verified the repository URL you provided resolves to the public `Peshimam-Ashar-Mohammed/FilzaNix` repository on GitHub, with `main` as its default branch.