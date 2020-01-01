const https = require('https');

console.log("Fetching data...");
let baseUrl = "https://ws.audioscrobbler.com/2.0/?method=user.getinfo&format=json";
let args = process.argv.slice(2);
let url = `${baseUrl}&user=${args[1]}&api_key=${args[0]}`;
https.get(url, (resp) => {
  let data = '';
  resp.on('data', (chunk) => data += chunk);
  resp.on('end', () => {
    let user = JSON.parse(data).user;
    let playcount = parseInt(user.playcount);
    let registered = parseInt(user.registered.unixtime);
    let now = new Date().getTime() / 1000;
    let daysDiff = (now - registered) / (60 * 60 * 24);
    let avgCount = playcount / daysDiff;
    let nextAvg = Math.ceil(avgCount);
    let nextLeft = Math.ceil(nextAvg * daysDiff - playcount);
    console.log(avgCount);
    console.log(`It's needed to listen to ${nextLeft} more songs to make avg ${nextAvg}`);
  });
}).on("error", (err) => {
  console.log("Error: " + err.message);
});