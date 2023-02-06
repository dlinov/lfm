use chrono::{DateTime, Utc};
use chrono::serde::ts_seconds;
use serde::Deserialize;
use std::env;
use reqwest::blocking::Client;

fn main() {
    let args: Vec<String> = env::args().collect();
    let api_key = &args[1];
    let username = &args[2];
    let stats = get_average_playcount(api_key, username);
    println!("{}", stats.avg);
    println!("It's needed to listen to {} more songs to make avg {}", stats.rem, stats.next);
}

fn get_average_playcount(api_key: &str, user: &str) -> Statistics {
    let client = Client::new();
    let url = format!("https://ws.audioscrobbler.com/2.0/?method=user.getinfo&user={}&api_key={}&format=json", user, api_key);
    println!("Fetching data...");
    let response = client.get(&url).send().unwrap();
    let resp: LFMResponse = response.json().unwrap();
    return stats(resp.user);
}

fn stats(user: User) -> Statistics {
    let registration_date = user.registered.unixtime;
    let days = (Utc::now() - registration_date).num_days();
    let played = user.playcount.parse::<i64>().unwrap();
    let avg = (played as f64) / (days as f64);
    // TODO: https://github.com/rust-lang/rust/issues/88581
    let next = avg.ceil() as i64;
    let rem = days * next - played;
    return Statistics { avg, next, rem };
}

#[derive(Debug, Deserialize)]
struct LFMResponse {
    user: User
}

#[derive(Debug, Deserialize)]
struct User {
    playcount: String,
    registered: Registered
}

#[derive(Hash, Eq, PartialEq, Debug, Deserialize)]
struct Registered {
    #[serde(rename = "#text", with = "ts_seconds",)]
    unixtime: DateTime<Utc>
}

struct Statistics {
    avg: f64,
    next: i64,
    rem: i64
}
