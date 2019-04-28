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

import cats.Eq
import cats.laws.discipline._
import laws.GenReaderLaws
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws

trait GenReaderTests[F[_], G[_], R] extends Laws {
  def laws: GenReaderLaws[F, G, R]

  def genReader[A](
      implicit fa: Arbitrary[F[A]],
      r: Arbitrary[R],
      eqGa: Eq[G[A]],
      eqFa: Eq[F[A]]
  ): RuleSet =
    new DefaultRuleSet(
      name = "GenReader",
      parent = None,
      "elimination" -> forAll(laws.elimination[A] _),
      "idempotency" -> forAll(laws.idempotency[A] _)
    )
}

object GenReaderTests {
  def apply[F[_], G[_], R](genReaderLaws: GenReaderLaws[F, G, R]): GenReaderTests[F, G, R] =
    new GenReaderTests[F, G, R] {
      val laws = genReaderLaws
    }
}
