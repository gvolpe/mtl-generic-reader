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

import cats.{ Eq, Functor }
import cats.laws.discipline._
import laws.DependencyLaws
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws

trait DependencyTests[F[_], G[_], H[_]] extends Laws {
  def laws: DependencyLaws

  def dependency[A](
      implicit fa: Arbitrary[F[A]],
      eqFa: Eq[F[A]],
      ff: Functor[F],
      ev0: Dependency[F, F],
      ev1: Dependency[F, G],
      ev2: Dependency[G, H],
      ev3: Dependency[H, F]
  ): RuleSet =
    new DefaultRuleSet(
      name = "Dependency",
      parent = None,
      "identity" -> forAll(laws.identity[F, A] _),
      "associativityAndComposition" -> forAll(laws.associativityAndComposition[F, G, H, A] _)
    )
}

object DependencyTests {
  def apply[F[_]: Functor, G[_], H[_]]: DependencyTests[F, G, H] =
    new DependencyTests[F, G, H] {
      val laws = new DependencyLaws {}
    }
}
