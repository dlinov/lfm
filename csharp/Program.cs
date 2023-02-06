using System;
using System.Net.Http;
using System.Text.Json;
using System.Text.Json.Serialization;
using System.Text.Json.Serialization.Metadata;
using System.Threading.Tasks;

namespace dotnet
{
    class Program
    {
        private const String BaseUrl = "https://ws.audioscrobbler.com/2.0/?method=user.getinfo&format=json";
        private static readonly HttpClient client = new HttpClient();
        private static readonly JsonSerializerOptions options = new JsonSerializerOptions
        {
            PropertyNamingPolicy = JsonNamingPolicy.CamelCase,
            TypeInfoResolver = EntryContext.Default
        };
        private static readonly JsonTypeInfo<LFMResponse> ti =
            (JsonTypeInfo<LFMResponse>) options.GetTypeInfo(typeof(LFMResponse));

        static void Main(string[] args)
        {
            Console.WriteLine("Fetching data...");
            var url = $"{BaseUrl}&user={args[1]}&api_key={args[0]}";
            var response = MakeRequest(url).Result;
            Console.WriteLine(response.Stats.Avg);
            Console.WriteLine($"It's needed to listen to {response.Stats.Rem} more songs to make avg {response.Stats.Next}");
        }

        private static async Task<LFMResponse> MakeRequest(String url)
        {
            var response = await client.GetAsync(url);
            // var responseString = await response.Content.ReadAsStringAsync();
            var responseStream = await response.Content.ReadAsStreamAsync();
            return await JsonSerializer.DeserializeAsync<LFMResponse>(responseStream, ti);
        }
    }

    record Statistics(double Avg, int Next, int Rem);

    record LFMResponse(User User)
    {
        [JsonIgnore]
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
                return new Statistics(avg, (int) next, (int) rem);
            }
        }
    }

    record User(string Playcount, Registered Registered);

    record Registered(string Unixtime, [property: JsonPropertyName("#text")] long Text);
    
    [JsonSerializable(typeof(LFMResponse))]
    partial class EntryContext : JsonSerializerContext { }
}
