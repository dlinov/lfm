. ../*.env

sbt "stage; GraalVMNativeImage/packageBin"
# JVM
time ./target/universal/stage/bin/lfm $API_KEY $USER_NAME
# Graal
time ./target/graalvm-native-image/lfm $API_KEY $USER_NAME
