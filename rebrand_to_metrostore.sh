#!/bin/bash

# 1. Update app name in strings.xml
find . -type f -name "strings.xml" -exec sed -i 's/<string name="app_name">.*<\/string>/<string name="app_name">metrostore<\/string>/g' {} +

# 2. Rename all package names in Kotlin, Java, and AIDL files
find . -type f \( -name "*.kt" -o -name "*.java" -o -name "*.aidl" \) -exec sed -i \
  -e 's/package com\.aurora\.store/package com.metro.store/g' \
  -e 's/package com\.aurora/package com.metro/g' \
  -e 's/import com\.aurora\.store/import com.metro.store/g' \
  -e 's/import com\.aurora/import com.metro/g' \
  {} +

# 3. Rename package usage in XML files (layouts, preferences, manifests, etc.)
find . -type f -name "*.xml" -exec sed -i \
  -e 's/com\.aurora\.store/com.metro.store/g' \
  -e 's/com\.aurora/com.metro/g' \
  {} +

# 4. Move directories for Java, Kotlin, and AIDL
# (from com/aurora/store -> com/metro/store, and com/aurora -> com/metro)
for src in $(find ./app/src -type d -path "*/com/aurora/store"); do
    tgt=$(echo "$src" | sed 's/com\/aurora\/store/com\/metro\/store/')
    mkdir -p "$tgt"
    git mv "$src"/* "$tgt" 2>/dev/null || mv "$src"/* "$tgt"
    rmdir "$src"
done

for src in $(find ./app/src -type d -path "*/com/aurora" ! -path "*/com/aurora/store"); do
    tgt=$(echo "$src" | sed 's/com\/aurora/com\/metro/')
    mkdir -p "$tgt"
    git mv "$src"/* "$tgt" 2>/dev/null || mv "$src"/* "$tgt"
    rmdir "$src"
done

# 5. Update package name in AndroidManifest.xml
find . -type f -name "AndroidManifest.xml" -exec sed -i \
  -e 's/package="com\.aurora\.store"/package="com.metro.store"/g' \
  -e 's/package="com\.aurora"/package="com.metro"/g' \
  {} +

echo "Rebranding complete! Please review changes and rebuild your project."
