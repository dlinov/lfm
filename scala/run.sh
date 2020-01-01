. ../*.env

# JVM
sbt "run $API_KEY $USER_NAME"

# Graal
# sbt 'graalvm-native-image:packageBin' && ./target/graalvm-native-image/lfm $API_KEY $USER_NAME 