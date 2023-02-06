#! /bin/sh
. ../*.env
RUSTFLAGS="-C target-cpu=native" cargo build --release
time ./target/release/lfm $API_KEY $USER_NAME
