#!/bin/bash
wget http://mcp.ocean-labs.de/files/mcptest/methods.csv
wget http://mcp.ocean-labs.de/files/mcptest/fields.csv 

mv methods.csv mcp/conf/ && mv fields.csv mcp/conf/ && cd mcp && echo Yes | ./updatenames.sh 