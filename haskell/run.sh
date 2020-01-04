#! /bin/sh
. ../*.env
stack install --local-bin-path ./bin
time ./bin/lfm-exe $API_KEY $USER_NAME
