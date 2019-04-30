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

package io.github.gvolpe.reader

import cats._
import cats.data.Kleisli
import cats.effect.IO
import cats.implicits._
import cats.tests.CatsSuite
import io.github.gvolpe.reader.laws.GenReaderLaws
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.{ arbitrary => getArbitrary }
import org.scalacheck.Gen
import scala.util.Try

class KleisliGenReaderTest extends CatsSuite {
  import CatsEqInstances._, kleisli._, DependencyInstances._

  checkAll("Kleisli[Try]", GenReaderTests(KleisliGenReaderLaws[Try, String]).genReader[String])
  checkAll("Kleisli[IO]", GenReaderTests(KleisliGenReaderLaws[IO, String]).genReader[String])

  checkAll("Kleisli", DependencyTests[Kleisli[IO, String, ?], IO, IO].dependency[String])

}

object DependencyInstances {
  implicit val idKleisliDep: Dependency[Kleisli[IO, String, ?], Kleisli[IO, String, ?]] =
    new Dependency[Kleisli[IO, String, ?], Kleisli[IO, String, ?]] {
      def apply[A](fa: Kleisli[IO, String, A]): Kleisli[IO, String, A] = fa
    }

  implicit val idIODep: Dependency[IO, IO] =
    new Dependency[IO, IO] {
      def apply[A](fa: IO[A]): IO[A] = fa
    }

  implicit val ioToKleisliDep: Dependency[IO, Kleisli[IO, String, ?]] =
    new Dependency[IO, Kleisli[IO, String, ?]] {
      def apply[A](fa: IO[A]): Kleisli[IO, String, A] = Kleisli(_ => fa)
    }

  implicit val kleisliToIORDep: Dependency[Kleisli[IO, String, ?], IO] =
    new Dependency[Kleisli[IO, String, ?], IO] {
      def apply[A](fa: Kleisli[IO, String, A]): IO[A] = fa.run("")
    }
}

object CatsEqInstances {

  implicit def eqIO[A: Eq]: Eq[IO[A]] =
    new Eq[IO[A]] {
      def eqv(x: IO[A], y: IO[A]): Boolean =
        Eq[A].eqv(x.unsafeRunSync(), y.unsafeRunSync())
    }

  implicit def eqTry[A: Eq]: Eq[Try[A]] =
    new Eq[Try[A]] {
      def eqv(x: Try[A], y: Try[A]): Boolean =
        Eq[A].eqv(x.get, y.get)
    }

  implicit def eqKleisli[F[_], A: Eq, R: Arbitrary](implicit eqFa: Eq[F[A]]): Eq[Kleisli[F, R, A]] =
    new Eq[Kleisli[F, R, A]] {
      def eqv(x: Kleisli[F, R, A], y: Kleisli[F, R, A]): Boolean = {
        lazy val env = getArbitrary[R].sample.get
        eqFa.eqv(x.run(env), y.run(env))
      }
    }

  implicit def arb[F[_]: Applicative, A: Arbitrary]: Arbitrary[F[A]] =
    Arbitrary(genPure[F, A])

  def genPure[F[_]: Applicative, A: Arbitrary]: Gen[F[A]] =
    getArbitrary[A].map(_.pure[F])

}

object KleisliGenReaderLaws {
  def apply[F[_], R](implicit ev: GenReader[Kleisli[F, R, ?], F, R]): GenReaderLaws[Kleisli[F, R, ?], F, R] =
    new GenReaderLaws[Kleisli[F, R, ?], F, R] { val M = ev }
}
