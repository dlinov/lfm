import math
import requests
import sys
from datetime import datetime

def main():
    user_name = sys.argv[2]
    api_key = sys.argv[1]
    print("Fetching data...")
    resp = fetchJson(user_name, api_key)
    respJson = resp.json()
    user = respJson['user']
    playcount = int(user['playcount'])
    registered_unixtime = user['registered']['#text']
    registered_at = datetime.fromtimestamp(registered_unixtime)
    now = datetime.now()
    delta = (now - registered_at).days
    avg = playcount / delta
    next_avg = math.ceil(avg)
    print(avg)
    print("It's needed to listen to {} more songs to make avg {}".format(next_avg * delta - playcount, next_avg))

    
def fetchJson(user_name, api_key):
    return requests.get('https://ws.audioscrobbler.com/2.0/?method=user.getinfo&user={}&api_key={}&format=json'.format(user_name, api_key))

if __name__ == '__main__':
    main()