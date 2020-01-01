package main

import (
    "encoding/json"
    "fmt"
    "io/ioutil"
    "math"
    "net/http"
    "os"
    "runtime/debug"
    "strconv"
    "time"
)

func main() {
	debug.SetGCPercent(-1) // disable GC because it's short-living script
    fmt.Println("Fetching data...")
    apiKey := os.Args[1]
    userName := os.Args[2]
    baseUrl := "https://ws.audioscrobbler.com/2.0/?method=user.getinfo&format=json"
    url := fmt.Sprintf("%s&user=%s&api_key=%s", baseUrl, userName, apiKey)
    resp, err := http.Get(url)
    if (err != nil) {
        panic(err)
    }
    defer resp.Body.Close()
    body, err := ioutil.ReadAll(resp.Body)
    if (err != nil) {
        panic(err)
    }
    var dat map[string]interface{}
    if err := json.Unmarshal(body, &dat); err != nil {
        panic(err)
    }
    user := dat["user"].(map[string]interface{})
    // playCount := user["playcount"].(float64)
    playCount, err := strconv.ParseInt(user["playcount"].(string), 10, 32)
    if (err != nil) {
        panic(err)
    }
    registered := user["registered"].(map[string]interface{})
    startUnixTime, err := strconv.ParseInt(registered["unixtime"].(string), 10, 64)
    if err != nil {
        panic(err)
    }
    startDate := time.Unix(startUnixTime, 0)
    endDate := time.Now()
    daysRegistered := endDate.Sub(startDate).Hours() / 24
    avg := float64(playCount) / daysRegistered
    fmt.Println(avg)
    nextAvg := math.Ceil(avg)
    rem := math.Ceil(nextAvg * daysRegistered - float64(playCount))
    fmt.Printf("It's needed to listen to %.f more songs to make avg %v\n", rem, nextAvg)
}
