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

/*
 * Generalized Reader typeclass for any `F[_]` that can eliminate the environment
 * `R` and in effect produce a `G[_]`.
 *
 * It abstracts over `provide` and `run`, for `ZIO` and `Kleisli`, respectively.
 *
 * Examples:
 *
 * - `Kleisli[IO, R, A] => R => IO[A]`
 * - `TaskR[R, A] => R => Task[A]`
 */
abstract class GenReader[F[_], G[_], R] {
  def runReader[A](fa: F[A])(env: R): G[A]
  def unread[A](ga: G[A]): F[A]
}

object GenReader {
  def apply[F[_], G[_], R](implicit ev: GenReader[F, G, R]): GenReader[F, G, R] = ev
}

/*
 * Specialized `GenReader` for Bifunctor-like data types such as `TaskR[R, ?]`.
 */
abstract class BiReader[F[_, _], R] extends GenReader[F[R, ?], F[Any, ?], R]

object BiReader {
  def apply[F[_, _], R](implicit ev: BiReader[F, R]): BiReader[F, R] = ev
}

/*
 * Specialized `GenReader` for Transformer-like data types such as `Kleisli[IO, R, ?]`.
 */
abstract class TransReader[F[_[_], _, _], G[_], R] extends GenReader[F[G, R, ?], G, R]

object TransReader {
  def apply[F[_[_], _, _], G[_], R](implicit ev: TransReader[F, G, R]): TransReader[F, G, R] = ev
}
