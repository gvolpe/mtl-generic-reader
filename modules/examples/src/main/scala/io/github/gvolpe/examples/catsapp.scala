/*
 * Copyright 2019 Gabriel Volpe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.gvolpe.examples

import cats._
import cats.effect._
import cats.implicits._
import cats.mtl._
import module._

/*
 * Exploring the option of using the IO Monad instance of ApplicativeAsk to build
 * the dependency graph while abstracting over the effect type (tagless final).
 *
 * Creating an instance of `ApplicativeAsk` for a type that has no Reader effect
 * baked-in is indeed a hack but if you can live with it the benefits are great.
 * */
object catsapp extends IOApp {

  // Hacky instance
  def mkModuleReader[F[_]: Applicative](module: AppModule[F]): HasAppModule[F] =
    new DefaultApplicativeAsk[F, AppModule[F]] {
      override val applicative: Applicative[F] = implicitly
      override def ask: F[AppModule[F]]        = module.pure[F]
    }

  def run(args: List[String]): IO[ExitCode] =
    Graph
      .make[IO]
      .map(g => mkModuleReader(g.appModule))
      .flatMap { implicit m: HasAppModule[IO] =>
        Program.run[IO].as(ExitCode.Success)
      }

}
