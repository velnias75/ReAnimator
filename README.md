# ReAnimator - Fabric Mod
Pre-build your animation models and turn them into command blocks.

## Building
Run `gradlew clean build` and you'll find the *mod* in `build/libs`.

## Installation
Just copy the *mod* in your `mods`-folder and restart *Minecraft*.

## Usage
1. build your structure you want to animate.
2. select two corners of your structue by targeting them with `/spos1` and `/spos2`. The selection will be marked with a $\color{red}{red\ box}$ around it.
3. scan your structure with `/scan` to get it copied into an internal buffer
4. go to the place where you want the *command blocks* to be placed and mark the place with `/tpos`. The space the *command blocks* will occupy is marked with a $\color{blue}{blue\ box}$.
5. create the *command block chain* with [`/assemble `*tag*](#assemble-options). *tag* is the tag used by the generated *armor stands*. A $\color{yellow}{yellow\ box}$ will show the space occupied by the *armor stands*.
6. place a *command block* at the *south-west* corner of the generated *command blocks*. Let it face into the *command blocks* and set it to **impulse** and **needs redstone**.<br />No command is required. Put a *button* onto it and press the *button*.
7. the structure will appear within the $\color{yellow}{yellow\ box}$. You can animate it now as you like with your own *command blocks*.

**Hint:** you can remove the *armor stands* with `/kill @e[tag=`*tag*`]`

### `/assemble` options
Syntax: `/assemble <tag> [gap] [time]`
- **tag**: the tag to assign to the *armor stands* (required)
- **gap**: the gap between the *command blocks* and the *armor stands* (default: 3, optional)
- **time**: the time in ticks before the *falling blocks* actually will really fall down (default: -2147483648, optional)

## Important
This *mod* is in very early development.<br />
So **don't use it on productive worlds**, but generate a test world.<br />
If you want to **support** the development, join my [Discord](https://discord.gg/tkX9BcwCCS) and/or use the [Issues](https://github.com/velnias75/ReAnimator/issues).
