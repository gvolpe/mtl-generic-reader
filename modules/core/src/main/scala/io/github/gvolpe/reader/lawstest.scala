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

trait CheckLaws {
  def check[A: Eq](results: laws.IsEq[A]*): Unit =
    results.foreach { rs =>
      try {
        assert(Eq[A].eqv(rs.lhs, rs.rhs), s"${rs.lhs} was not equals to ${rs.rhs}")
      } catch {
        case e: AssertionError =>
          System.err.println(s"💥💥💥 ${e.getMessage} 💥💥💥")
          System.exit(-1)
      }
    }
}
