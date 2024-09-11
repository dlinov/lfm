#! /bin/sh
. ../*.env

# Determine OS type
OS_TYPE=$(uname)
if [ "$OS_TYPE" = "Darwin" ]; then
    OS_NAME="macOS"
elif [ "$OS_TYPE" = "Linux" ]; then
    OS_NAME="Linux"
else
    OS_NAME="Unknown"
fi

# Determine CPU architecture
CPU_ARCH=$(uname -m)

# Set release moniker based on OS type and CPU architecture
if [ "$OS_NAME" = "macOS" ]; then
    if [ "$CPU_ARCH" = "x86_64" ]; then
        RID="osx-x64"
    elif [ "$CPU_ARCH" = "arm64" ]; then
        RID="osx-arm64"
    else
        RID="osx-unknown"
    fi
elif [ "$OS_NAME" = "Linux" ]; then
    if [ "$CPU_ARCH" = "x86_64" ]; then
        RID="linux-x64"
    elif [ "$CPU_ARCH" = "arm64" ]; then
        RID="linux-arm64"
    else
        RID="linux-unknown"
    fi
else
    RID="win-x64"
fi

dotnet publish -c Release -r $RID --self-contained true
time ./bin/Release/net8.0/$RID/publish/lfm $API_KEY $USER_NAME
# dotnet run -c Debug -r $RID $API_KEY $USER_NAME
