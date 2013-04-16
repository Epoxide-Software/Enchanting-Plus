#!/bin/bash
#
#Original version by Lymia
#
##
heading() {
  echo "=== $1"
}
error() {
  echo "!!! Error - $1"
  exit 1
}
uerror() {
  echo "!!! Unexpected Error - $1"
  exit 1
}
warn() {
  echo "!!! Warning - $1"
}

echo "Enter which version of Forge to update to [Ex: 1.5.1-7.7.1.651]:" 

read FORGE_VERSION
FORGE_URL=http://files.minecraftforge.net/minecraftforge/minecraftforge-src-$FORGE_VERSION.zip

heading "Downloading Minecraft Forge"
wget -O minecraftforge.zip $FORGE_URL || rm -rf minecraftforge.zip || error "Failed to download Minecraft Forge"

heading "Unpacking Minecraft Forge"
unzip minecraftforge.zip > /dev/null || error "Failed to unpack Minecraft Forge"
rm minecraftforge.zip || warn "Failed to remove temp .zip file"

heading "Installing Minecraft Forge"
cd forge
sh install.sh || error "Failed to download Minecraft Forge"
cd ..

rm -rf mcp || warning "Failed to clean up old Minecraft Forge version"

heading "Prepring Minecraft Forge"
mv forge/mcp $CD || error "Failed to move MCP directory"
rm -rf forge || warning "Failed to remove temp directory"
