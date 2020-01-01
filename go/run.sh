#! /bin/sh
. ../*.env
go build lfm.go
time ./lfm $API_KEY $USER_NAME
