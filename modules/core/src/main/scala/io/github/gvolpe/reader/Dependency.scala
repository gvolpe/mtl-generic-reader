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

import cats.Functor
import cats.implicits._

/*
 * Interface (not a typeclass) that defines a relationship between dependencies.
 * It can be seen as a natural transformation (~>) with different laws.
 *
 * It can normally be created by requiring a lawful `GenReader[F, G, R]` if the
 * only way to obtain `R` is via an effectful operation.
 */
abstract class Dependency[F[_], G[_]] {
  def apply[A](fa: F[A]): G[A]
}

object Dependency {

  /*
   * Make a `Dependency[F, G]` after obtaining `R` from
   * an effectful operation on `G`.
   */
  def make[F[_], G[_]: Functor, R](
      ga: G[R]
  )(implicit ev: GenReader[F, G, R]): G[Dependency[F, G]] =
    ga.map { env =>
      new Dependency[F, G] {
        def apply[A](fa: F[A]): G[A] = ev.runReader[A](fa)(env)
      }
    }

  /*
   * Make a `Dependency[F[R, ?], F[Any, ?]]` after obtaining `R` from
   * an effectful operation on `F[Any, ?]`.
   */
  def make[F[_, _], R](
      ga: F[Any, R]
  )(implicit f: Functor[F[Any, ?]], reader: BiReader[F, R]): F[Any, Dependency[F[R, ?], F[Any, ?]]] =
    make[F[R, ?], F[Any, ?], R](ga)

  /*
   * Make a `Dependency[F[R, G, ?], G]` after obtaining `R` from
   * an effectful operation on `G`.
   */
  def makeReader[F[_[_], _, _], G[_]: Functor, R](
      ga: G[R]
  )(implicit reader: TransReader[F, G, R]): G[Dependency[F[G, R, ?], G]] =
    make[F[G, R, ?], G, R](ga)

}
