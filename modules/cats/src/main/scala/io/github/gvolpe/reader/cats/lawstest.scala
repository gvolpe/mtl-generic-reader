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

package io.github.gvolpe.reader.cats

import cats._
import cats.data.Kleisli
import cats.effect.IO
import cats.implicits._
import io.github.gvolpe.reader.{ CheckLaws, GenReader, GenReaderLaws }

object lawstest extends App with CheckLaws with CatsEqInstances {
  import instances._

  val env: String = "ctx"

  val ga1: Id[Int] = 123
  val ga2: IO[Int] = IO.pure(123)

  val fa1: Kleisli[Id, String, Int] = Kleisli(_ => ga1)
  val fa2: Kleisli[IO, String, Int] = Kleisli(_ => ga2)

  check(KleisliGenReaderLaws[Id, String].elimination(fa1, env, ga1))
  check(KleisliGenReaderLaws[Id, String].idempotency(fa1, env))
  check(KleisliGenReaderLaws[IO, String].elimination(fa2, env, ga2))
  check(KleisliGenReaderLaws[IO, String].idempotency(fa2, env))

  println("✔️  All tests have passed! (•̀ᴗ•́)و ̑̑")

}

trait CatsEqInstances {

  implicit def eqIO[A: Eq]: Eq[IO[A]] =
    new Eq[IO[A]] {
      def eqv(x: IO[A], y: IO[A]): Boolean =
        Eq[A].eqv(x.unsafeRunSync(), y.unsafeRunSync())
    }

}

object KleisliGenReaderLaws {
  def apply[F[_], R](implicit ev: GenReader[Kleisli[F, R, ?], F, R]): GenReaderLaws[Kleisli[F, R, ?], F, R] =
    new GenReaderLaws[Kleisli[F, R, ?], F, R] { val M = ev }
}
