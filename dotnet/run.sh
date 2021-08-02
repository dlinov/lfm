#! /bin/sh
. ../*.env
RID='osx-x64'
dotnet publish -c Release -r $RID --self-contained true
time ./bin/Release/net5.0/$RID/publish/lfm $API_KEY $USER_NAME
