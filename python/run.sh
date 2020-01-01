#! /bin/sh
. ../*.env
pip3 install requests
time python3 lfm.py $API_KEY $USER_NAME