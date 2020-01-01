package io.github.dlinov.lfm

import java.time.temporal.ChronoUnit
import java.time.Instant
import java.util.concurrent._

import cats.data.EitherT
import cats.effect._
import cats.syntax.either._
import io.circe._
import io.circe.syntax._
import io.circe.parser._
import org.http4s._
import org.http4s.implicits._
import org.http4s.client.blaze._
import org.http4s.client._
import org.http4s.circe._

object Boot extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    if (args.length < 2) {
      IO {
        println("Provide API key and user name via program args: lfm <api key> <user name>")
        ExitCode.Error
      }
    } else {
      (for {
        _ <- EitherT.liftF[IO, String, Unit](IO(println("Fetching data...")))
        url <- EitherT.fromEither[IO] {
          Uri.fromString("https://ws.audioscrobbler.com/2.0/").bimap(
            _.message,
            _.withQueryParam("method", "user.getinfo")
              .withQueryParam("user", args(1))
              .withQueryParam("api_key", args(0))
              .withQueryParam("format", "json")
          )
        }
        blockingPool <- EitherT.liftF(IO(Executors.newSingleThreadExecutor()))
        httpClient <- EitherT.liftF(IO {
          val blocker = Blocker.liftExecutorService(blockingPool)
          JavaNetClientBuilder[IO](blocker).create // : Client[IO]
        })
        resp <- EitherT(httpClient.expect[String](url).attempt)
          .leftMap(_.getMessage)
        doc <- EitherT.fromEither[IO](parse(resp))
          .leftMap(_.message)
        cursor = doc.hcursor
        user = cursor.downField("user")
        playCount <- EitherT.fromEither[IO](user.downField("playcount").as[Long])
          .leftMap(_.message)
        registeredAtEpochSec <- EitherT.fromEither[IO] {
          user.downField("registered").downField("#text").as[Long]
        }.leftMap(_.message)
      } yield {
        blockingPool.shutdown()
        val registeredAt = Instant.ofEpochSecond(registeredAtEpochSec)
        val daysRegistered = ChronoUnit.DAYS.between(registeredAt, Instant.now())
        val avgPlayPerDay = playCount / daysRegistered.toDouble
        val nextAvg = math.ceil(avgPlayPerDay).toLong
        val remaining = (nextAvg * daysRegistered) - playCount
        println(s"$avgPlayPerDay\nIt's needed to listen to $remaining more songs to make avg $nextAvg")
      }).fold(
        err => { println(err); ExitCode.Error },
        _ => ExitCode.Success
      )
    }
  }

}