Enchanting Plus
=====
Enchanting Plus is a Minecraft mod which gives players more control over their enchanting experience. 

Required Mods
=============
[Bookshelf](https://github.com/Darkhax-Minecraft/Bookshelf)   

Inter Mod Communicaton (IMC API)
===============
As of Enchanting Plus V4, we have abandoned a tangable API in favour of an IMC based one. While direct method calls can still be made, the IMC based API is strongly recommended. If you are unfamiliar with what IMC is, or how it works, you can find a basic tutorial [here](http://tutorials.darkhax.net/inter-mod-communication.html). The following is a list of IMC hooks, and documentation on how to use them. 

blacklistItems: A message with the blacklistItems key can be sent to Enchanting Plus to prevent an Item from working in our advanced enchanting table. You can send this message as a string message, NBT message, or an ItemStack message. In a string message, the string is used as a namespace ID for the item to blacklist, such as minecraft:arrow. In a NBT message, a list of strings are stored under the blacklistItems name, each string in the list is treated as if it were a normal single string message. If the message is an ItemStack message, the namespace ID for the Item in the ItemStack will be used. 

blacklistEnchantments: A message with the blacklistEnchantments key can be sent to Enchating Plus to prevent an Enchantment from being usable within the advanced enchantment table. You can send this message as a string message, NBT message, or an ItemStack message. In a string message, the string is treated as an Integer which represents the enchantment ID. In an NBT message, a list of strings are stored under the blacklistEnchantments name, each string is treated as an Integer which represents an enchantment ID. In an ItemStack message, all Enchantments on that ItemStack will be blacklisted. This also works for enchanted books. 

Compilation
===========
It is very simple to build versions of Enchanting Plus. Enchanting Plus makes use of the Forge Gradle wrapper which is included within this project. To compile the mod, simply download a copy of the repository, and run the "gradlew build" command from within the same directory as the repository files. A compiled Jar file will be generated within the build/libs folder. 

Credits
=======
Darkhax - Current Maintainer   
Lclc98 - Current Maintainer   
Xkyouchoux - Original Developer   
odininon - Previous Meaintainer   
GnRSlash - Previous Constributor   

Translators
===========
Adaptivity   
Killerpommes   
