#!/bin/bash

# Extract the current version from the pom.xml
current_version=$(awk '/<parent>/,/<\/parent>/{next} /<version>/{print; exit}' pom.xml | sed -e 's/<version>\(.*\)<\/version>/\1/')
echo "Current version is $current_version"

# Split the version into an array
IFS='.' read -ra version_parts <<< "$current_version"

# Determine which part of the version to increment based on the command line argument
case $1 in
  "major")
    version_parts[0]=$((version_parts[0] + 1))
    version_parts[1]=0
    version_parts[2]=0
    ;;
  "minor")
    version_parts[1]=$((version_parts[1] + 1))
    version_parts[2]=0
    ;;
  "patch")
    version_parts[2]=$((version_parts[2] + 1))
    ;;
  *)
    echo "Invalid argument. Please use 'major', 'minor', or 'patch'."
    exit 1
    ;;
esac

# Construct the new version
#new_version="${version_parts[0]}.${version_parts[1]}.${version_parts[2]}"
new_version=$(echo "${version_parts[0]}.${version_parts[1]}.${version_parts[2]}" | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//')
echo "New version is $new_version"

# Set the new version in the pom.xml
mvn versions:set -DnewVersion="$new_version" -DgenerateBackupPoms=false
