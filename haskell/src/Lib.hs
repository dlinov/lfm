{-# LANGUAGE DeriveAnyClass #-}
{-# LANGUAGE DeriveGeneric #-}
{-# LANGUAGE OverloadedStrings #-}
module Lib
    ( callApi
    ) where

import Data.Aeson (Value, FromJSON)
import Data.Aeson.Types
import Data.Time
import Data.Time.Clock.POSIX
import GHC.Generics
import Network.HTTP.Simple
import System.Environment
import Text.Printf

newtype Registered = Registered {unixtime :: String} deriving (Show, Generic, FromJSON)

data User = User {playcount :: String, registered :: Registered} deriving (Show, Generic, FromJSON)

newtype UserInfo = UserInfo {user :: User} deriving (Show, Generic, FromJSON)

epochToDay :: Integral a => a -> Day
epochToDay = utctDay . posixSecondsToUTCTime . fromIntegral

callApi :: IO ()
callApi = do
  args <- getArgs
  case args of
    [] -> error "must be used as 'lfm <api key> <user name>'"
    [apiKey, userName] -> do
      let url = printf "https://ws.audioscrobbler.com/2.0/?method=user.getinfo&format=json&user=%s&api_key=%s" userName apiKey
      request <- parseRequest url
      putStrLn "Fetching data..."
      response <- httpJSON request
      let jsObject = getResponseBody response :: Value
      case (fromJSON jsObject :: Result UserInfo) of
        Error msg -> error msg
        Success (UserInfo (User playcount (Registered unixtime))) -> do
          let playCount = fromIntegral (read playcount :: Integer)
          now <- getCurrentTime
          let registeredDay = epochToDay (read unixtime :: Integer)
          let registeredDayDiff = fromIntegral (diffDays (utctDay now) registeredDay)
          let avg = playCount / registeredDayDiff :: Double
          let nextAvg = toInteger (ceiling avg)
          let remToNext = registeredDayDiff * fromIntegral nextAvg - playCount
          let msg = printf "It's needed to listen to %.f more songs to make avg %d" remToNext nextAvg
          print avg
          putStrLn msg 
