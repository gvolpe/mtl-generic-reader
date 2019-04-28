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

package io.github.gvolpe.reader.laws

import cats.Functor
import cats.arrow.FunctionK
import cats.laws._
import io.github.gvolpe.reader.Dependency

trait DependencyLaws {

  def identity[F[_]: Functor, A](fa: F[A])(implicit ev: Dependency[F, F]) =
    FunctionK.id[F](fa) <-> ev(fa)

  def composition[F[_], G[_], H[_], A](fa: F[A], ga: G[A])(
      implicit ev1: Dependency[F, G],
      ev2: Dependency[G, H]
  ) =
    ev2(ev1(fa)) <-> ev2(ga)

  def associativity[F[_], G[_], H[_], A](fa: F[A])(
      implicit ev1: Dependency[F, G],
      ev2: Dependency[G, H],
      ev3: Dependency[H, F]
  ) = {
    val f1 = (ev3[A] _) compose ((ev2[A] _) compose (ev1[A] _))
    val f2 = ((ev3[A] _) compose (ev2[A] _)) compose (ev1[A] _)
    f1(fa) <-> f2(fa)
  }

}
