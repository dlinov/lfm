#! /bin/sh
. ../*.env
dotnet publish -c Release
time ./bin/Release/netcoreapp3.1/publish/lfm $API_KEY $USER_NAME

