using System;
// using System.Collections.Generic;
using System.Net.Http;
using System.Text.Json;
using System.Text.Json.Serialization;
using System.Threading.Tasks;

namespace dotnet
{
    class Program
    {
        private static readonly HttpClient client = new HttpClient();
        private static readonly String baseUrl = "https://ws.audioscrobbler.com/2.0/?method=user.getinfo&format=json";

        static void Main(string[] args)
        {
            Console.WriteLine("Fetching data...");
            var url = $"{baseUrl}&user={args[1]}&api_key={args[0]}";
            var response = MakeRequest(url).Result;
            Console.WriteLine(response.Stats.Avg);
            Console.WriteLine($"It's needed to listen to {response.Stats.Rem} more songs to make avg {response.Stats.Next}");
        }

        private static async Task<LFMResponse> MakeRequest(String url)
        {
            var response = await client.GetAsync(url);
            // var responseString = await response.Content.ReadAsStringAsync();
            var responseStream = await response.Content.ReadAsStreamAsync();
            var options = new JsonSerializerOptions
            {
                PropertyNamingPolicy = JsonNamingPolicy.CamelCase
            };
            return await JsonSerializer.DeserializeAsync<LFMResponse>(responseStream, options);
        }
    }

    class Statistics {
        public double Avg { get; set; }
        public int Next { get; set; }
        public int Rem { get; set; }
    }

    class LFMResponse
    {
        public User User { get; set; }

        public Statistics Stats
        {
            get
            {
                // var registeredAt = DateTimeOffset.FromUnixTimeSeconds(long.Parse(User.Registered.Unixtime));
                var registeredAt = DateTimeOffset.FromUnixTimeSeconds(User.Registered.Text);
                var days = DateTimeOffset.Now.Subtract(registeredAt).Days;
                var played = double.Parse(User.Playcount);
                var avg = played / days;
                var next = Math.Ceiling(avg);
                var rem = Math.Round(days * next - played);
                return new Statistics
                {
                    Avg = avg,
                    Next = (int) next,
                    Rem = (int) rem
                };
            }
        }
    }

    class User
    {
        public String Playcount { get; set; }
        public Registered Registered { get; set; }
    }

    class Registered
    {
        public String Unixtime { get; set; }
        
        [JsonPropertyName("#text")]
        public long Text { get; set; }
    }
}
